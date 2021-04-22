# Implementazione NoSQL in FLY
## Indice
- [Stabilire una connessione a MongoDB in FLY](#stabilire-una-connessione-a-mongodb-in-fly)
- [Operazioni CRUD](#operazioni-crud)
## Stabilire una connessione a MongoDB in FLY
Per stabili una connessione a MongoDB in FLY non ci basta che creare una nuova variabile di tipo `nosql`.  
Le variabili di tipo `nosql` sono composte da, oltre al parametro type, tre parametri obbligari, che sono:  
- `client:` ancora da implementare...
- `database:` serve per specifiare il nome del database al quale vogliamo connetterci;
- `collection:` serve per specifiare il nome della collezione di quel database che vogliamo utilizzare.  
  
Oltre a questi tre parametri, vi è un parametro opzionale, `path`,  che serve per specifiare il path del file log4j.properties. Quest'ultimo è opzionale
in quanto, se non specificato, verrà generato ed utilizzato un file log4j.properties, all'interno del progetto, contenenti delle proprietà "predefinite", ovvero:
```
log4j.appender.stdout.Target=System.out
log4j.appender.stdout.layout=org.apache.log4j.PatternLayout
log4j.appender.stdout=org.apache.log4j.ConsoleAppender
log4j.rootLogger=INFO, stdout
log4j.appender.stdout.layout.ConversionPattern=%d{yy/MM/dd HH\:mm\:ss} %p %c{2}\: %m%n
```
Un esempio di dichiarazione di questa variabile potrebbe essere:
```
var nosql = [type = "nosql", client = "", database = "namedabase", collection = "namecollection"]
```
In questo modo verrà stabilita una connessione al database `namedatabase` e verrà utilizzata la collezione `namecollection`. Poiché il parametro `path` non è stato specificato
verrà generato e utilizzato un file log4j.properties contenente le proprietà descritte sopra.
## Operazioni CRUD
- [Insert](#insert)
- [Select](#select)
- [Update](#update)
- [Replace](#replace)
- [Delete](#delete)

Per effettuare operazioni CRUD sulla nostra collection, abbiamo bisono di creare una nuova variabile di tipo `query`.  
Le variabili di tipo `query` sono composte, oltre che dal parametro `type`, da altri tre parametri obbligatori, che sono:
- `type_query:` serve per specificare il tipo di query (`insert`, `select`, `update`, `replace`, `delete`);
- `collection:` questo parametro deve essere inizializzato con una variabile di tipo `nosql` la quale stabilisce la connessione con la collezione su cui vogliamo operare;
- `statement:` una stringa che specifica la sintassi NoSQL dell'operazione che vogliamo effettuare.
### Insert
Il tipo di query **insert** ci consente di effettuare operazione di inserimento di nuovi dati all'interno della nostra collezione.  
Grazie a questo oggetto, possiamo inserire uno o più oggetti all'interno della nostra collezione.  
Per poter eseguire quest'operazione, non ci basta che inizializzare una variabile di tipo `query`, settando gli altri parametri nel modo corretto.
All'interno del parametro `statement` dobbiamo inserire gli oggetti in formato JSON che intendiamo inserire all'interno della nostra collezione.  
Mostriamo uno snippet di codice per intenderci meglio. Intendiamo aggiungere questo oggetto JSON all'interno della nostra collezione `students`:
```json
{
	"name": "Antonio",
	"surname": "Cirillo",
	"age": 21,
	"address": {
		"state": "Italy",
		"city": "Battipaglia",
		"zip": 84091
	}
}
```
La prima cosa da fare è stabilire la connessione al database NoSQL tramite l'inizializzazione di una variabile di tipo 'nosql':
```
var studentCollection = [type = "nosql", client = "", database = "mydatabase", collection = "students"]
```
In questo modo ora abbiamo stabilito una connessione al database **mydatabase** su MongoDB, e possiamo effettuare operazioni CRUD sulla collection **students**. *Ricordiamo che non avendo specificato il path del nostro file log4j.properties, quest'ultimo verrà generato automaticamente con le proprietà di default sopra descritte, e verrà utilizzato per stabilire la connessione con il client MongoDB*.  
Ora non ci resta che creare una nuova variabile di tipo `query` impostando i vari parametri opportunamente:
```
var insertOneStudent = [type = "query", query_type = "insert", collection = studentCollection,
	statement = "{ 'name': 'Antonio', 'surname': 'Cirillo', 'age': 21, 'address': { 'state': 'Italy', 'city': 'Battipaglia', 'zip': 84091 } }"]
```
In questo modo abbiamo inizializzato una query di tipo **insert** sulla nostra collezione **students** specificando nel campo `statement` l'oggetto che vogliamo aggiungere.  
Ora non ci resta che eseguire la query chiamando semplicemente il metodo `execute()` sull'oggetto di tipo `query`.
```
insertOneStudent.execute()
```
Inoltre, è possibile aggiungere più di un oggetto per volta, infatti basta inserire all'interno del parametro statement, non più un solo oggetto JSON, ma un JSON array contenente tutti gli oggetti JSON che vogliamo aggiungere, ad esempio:
```
twoStudents = "[ { 'name': 'Antonio', 'surname': 'Cirillo', 'age': 21, 'address': { 'state': 'Italy', 'city': 'Battipaglia', 'zip': 84091 } }, "
	+ "{ 'name': 'Giovanni', 'surname': 'Rapa', 'age': 21, 'address': { 'state': 'Italy', 'city': 'Giffoni Vale Piana', 'zip': 84095 } } ]"

var insertTwoStudents = [type = "query", query_type = "insert", collection = studentCollection, statement = twoStudents]

twoStudents.execute()
```
Infine, è possibile aggiungere all'interno di una collezione, tutti o almeno una parte, di oggetti presenti su un file di tipo CSV. Infatti, è possibile specificare nel campo 
statement, non solo una stringa contenente gli oggetti JSON che si vogliono aggiungere, un oggetto di tipo `file` che ha come parametro `ext` il valore `csv`.  
Se il parametro statement è inizializzato con un oggetto di tipo `file` è possibile specificare altri due parametri opzionali, oltre a quelli visti in precedenza, ovvero:
- `from:` indica la posizione del primo oggetto presente nel file CSV che vogliamo inserire nella collezione **(l'indice parte da 0)**;
- `to:` indica la posizione dell'ultimo oggetto presente nel file CSV che vogliamo leggere.  
  
Riportaimo di seguito uno snippet di codice che indica come inserire i primi 150 oggetti presenti nel file `student.csv`.
```
var studentCSV = [type = "file", path = "path/to/student.csv", ext = "csv"]

var insertStudentFromCSV = [type = "query", query_type = "insert", collection = studentCollection,
	statement = studentCSV, from = "0", to = "150"]
	
insertStudentFromCSV.execute()
```
### Select
Il tipo di query **select** ci consente di effettuare operazioni di selezione sulla nostra collezione.  
In modo analogo andiamo a creare la nostra variabile di tipo `query`, questa volta però specificando, nel parametro `query_type`, `select`.
```
var selectStudent = [type = "query", query_type = "select", collection = studentCollection, statement = "{ 'name': 'Antonio' }"]
```
Una volta dichiarata la variabile, possiamo eseguire la query semplicemente con il metodo `execute()`. Questo metodo restituisce tutti i record che verranno selezionati dalla nostra query sotto forma di tabelle. Mostriamo un esempio dell'utilizzo: salviamo tutte le tabelle in una variabile `result` e stampiamo poi tutte le tabelle:
```
var result = selectStudent.execute()

for table in result {
	println table
}
```
### Update
Il tipo di query **update** ci consente di effettuare operazioni di update sulla nostra collezione.  
Per poter effettuare un operazione di update sulla nostra collezione, come per le altre query, dobbiamo creare una nuova variabile di tipo `query`, ma questa volta con un parametro in più. Oltre al parametro `type` che sarà `query`, il parametro `query_type` che sarà `update` e il parametro `collection`, vi sarà il parametro `filter` dove bisogna indicare una query che ci consentirà di selezionare tutti gli oggetti che intendiamo modificare all'interno della nostra collezione, seguito dal parametro `statement` che indica invece quali attributi degli oggetti selezionati devono essere modificati e con quale valore.  
Facciamo un esempio:  
Intendiamo selezionare tutti gli oggetti all'interno della nostra collezione che hanno un valore maggiore uguale a 22 per il parametro `age` e sostituire a quest'ultimi, il valore dell'attributo `address.city`, il valore `Salerno`.  
La sintassi FLY sarà quindi:
```
var filter_update = "{ 'age': { $gte: 22 } }"
var statement_update = " { 'address.city': 'Salerno' }"

var updateStudent = [type = "query", query_type = "update", collection = studentCollection, 
	filter = filter_update, statement = statement_update]
```
Come per le altre query, per poterla eseguire, basterà utilizzare il metodo `execute()` sulla variabile `updateStudent`.
```
updateStudent.execute()
```
### Replace
Il tipo di query **replace** ci consente di effettuare operazioni di tipo replace sulla nostra collezione.  
Questa operazione è analoga a quella di **update** eccetto per il risultato finale: mentre le query di tipo **update** utilizzano una prima istruzione per ottenere un insieme di record da moficiare all'interno della collezione e una seconda istruzione che indica quali parametri modificare con quali valori, le query di tipo **replace** utilizzano comunque la prima istruzione per ottenere un insieme di record all'interno della collezione, ma solo il primo di essi verrà modificato con i valori presenti nella seconda istruzione.  
Prendiamo in considerazione l'esempio fatto per l'update:
```
var filter_replace = "{ 'age': { $gte: 22 } }"
var statement_replace = " { 'address.city': 'Salerno' }"

var replaceStudent = [type = "query", query_type = "reaplce", collection = studentCollection, 
	filter = filter_replace, statement = statement_replace]
```
Eseguendo questa istruzione, solo il primo elemento all'interno della collezione che ha un valore per l'attributo `age` maggiore uguale a 22 verrà modificato con i valori descritti nella variabile `statement_replace`.  
Come per le altre query, basta utilizzare il metodo `execute()` per eseguire la query.
```
replaceStudent.execute()
```
### Delete
Il tipo di query **delete** ci consente di effettuare operazioni di tipo delete sulla nostra collezione.  
Quest'operazione utilizza la stessa struttura per le query di tipo select, l'unica differenza è che tutti i record che verranno selezionati all'interno della collezione, non verranno restituiti, ma verrano eliminati dall'interno della collezione.  
Mostriamo come ad esempio possiamo eliminare tutti gli studenti all'interno della collezione che vivono a Battipaglia.
```
var deleteStudents = [type = "query", type_query = "delete", collection = studentCollection, statement = "{ 'address.city': 'Battipaglia' }]
```
Eseguendo la query, sempre utilizzando il metodo `execute()`, otterremo come valore di ritorno il numero di elementi che sono stati elementi dalla collezione.
