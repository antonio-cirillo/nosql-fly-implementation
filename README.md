# Implementazione NoSQL in FLY
## Indice
- [Stabilire una connessione a MongoDB in FLY](#stabilire-una-connessione-a-mongodb-in-fly)
- [Operazioni CRUD](#operazioni-crud)
- [Modifiche effettuate al generatore](#modifiche-effettuate-al-generatore)
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
In questo modo verrà stabilita una connessione al database `namedatabase` e verrà utilizzta la collezione `namecollection`. Poiché il parametro `path` non è stato specificato
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
	statement = "{ 'name': 'Antonio', 'surname': 'Cirillo', 'age': 21, 'address': { 'state': 'Italy', 'city': Battipaglia', 'zip': 84091 } }"]
```
In questo modo abbiamo inizializzato una query di tipo **insert** sulla nostra collezione **students** specificando nel campo `statement` l'oggetto che vogliamo aggiungere.  
Ora non ci resta che eseguire la query chiamando semplicemente il metodo `execute` sull'oggetto di tipo `query`.
```
insertOneStudent.execute()
```
Inoltre, è possibile aggiungere più di un oggetto per volta, infatti basta inserire all'interno del parametro stamtent, non più un solo oggetto JSON, ma un JSON array contenente tutti gli oggetti JSON che vogliamo aggiungere, ad esempio:
```
twoStudents = "[ { 'name': 'Antonio', 'surname': 'Cirillo', 'age': 21, 'address': { 'state': 'Italy', 'city': Battipaglia', 'zip': 84091 } }, "
	+ "{ 'name': 'Giovanni', 'surname': 'Rapa', 'age': 21, 'address': { 'state': 'Italy', 'city': Giffoni Vale Piana', 'zip': 84095 } } ]"

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
### Update
### Replace
### Delete
