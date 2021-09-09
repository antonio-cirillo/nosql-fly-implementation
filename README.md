# Interagire con database NoSQL all'interno di FLY
## Indice
- [Stabilire una connessione a MongoDB in FLY](#stabilire-una-connessione-a-mongodb-in-fly)
- [Effettuare query sul database](#effettuare-query-sul-database)

## Stabilire una connessione a MongoDB in FLY
Per stabilire una connesione ad un cluster MongoDB all'interno di FLY risulta necessario istanziare un oggetto di tipo **nosql**.  
La dichiarazione di una variabile di **type = "nosql"** è composta dai seguenti parametri:
- **endpoint** (solo in caso non si usano i parametri **resource_group** e **instance**): stringa rappresentante l’endpoint del database;
- **resourse_group** (solo in caso il database sia su Azure): stringa rappresentante il nome del ResourceGroup in cui + contenuta l’istanza del servizio DBaaS;
- **instance** (solo in caso il database sia su Azure): stringa rappresentante il nome dell’istanza del servizio DBaaS su cui è in esecuzione il database;
- **db_name**: stringa rappresentante il nome del database;
- **collection**: stringa rappresentante il nome della collezione del database;
- **properties** (opzionale): stringa rappresentante il path assoluto del file *log4j.properties*.  
  
L’entità **nosql** offre un metodo alternativo per permettere all’utente di stabilire una connessione ad un cluster MongoDB distribuito su Azure, utilizzando i parametri **resource_group** e **instance**, in sostituzione al parametro
**endpoint**. Questi due parametri permettono a FLY di ottenere, in modo del tutto astratto all’utente, l’endpoint del database tramite il nome dell’istanza del cluster e il nome del gruppo di risorse in cui è in esecuzione. Per poter utilizzare questi due parametri occorre utilizzare la parola chiave on seguita dalla variabile **type = "azure"**.  
Di seguito sono riportati due esempi in modo da mostrare le due diverse sintassi possibili per la dichiarazione di un'entita **nosql**.
```
var dbConnLocal = [ type = "nosql", endpoint = "endpointDatabase ", db_name = "nomeDatabase", collection = "nomeCollection"]
```
```
[...]

var dbConnAz = [ type = "nosql", resource_group = "nomeResourceGroup", instance = "nomeIstanza", db_name = "nomeDatabase", collection = "nomeCollection"] on CloudAz
```

## Effettuare query sul database
FLY mette a disposizione dell’utente l’entità **query** per poter effettuare operazioni di inserimento, selezione, modifica e cancellazione sui database MySQL e database MongoDB.  
La dichiarazione di una variabile di **type = "query"** è composta dai seguenti parametri:
- **query_type**: stringa rappresentante il tipo di query da eseguire: 
  * per i database MySQL i valori ammissibili sono: *select*, *value* e *update*;
  * per i database MongoDB i valori ammissibili sono: *select*, *insert*, *update*, *replace* e *delete*.
- **connection**: nome della variabile di tipo **sql** o **nosql** rappresentante la connessione ad un database;
- **filter** (solo per operazioni di *update* e *replace* su database MongoDB): interrogazione in linguaggio BSON per selezionare l’oggetto che si intende modificare, può essere specificata in due differenti formati:
  * stringa di testo;
  * nome di una variabile di tipo stringa.
- **query**: interrogazione (in linguaggio SQL per database MySQL o BSON per database MongoDB) da eseguire, può essere specificata utilizzando i tipo visti per il parametro precedente. Inoltre, per le operazioni di inserimento su database MongoDB, è possibile assegnare a questo parametro il nome di una variabile di tipo **file**. Questa
funzionalità permette, con una semplice riga di codice, di prelevare i dati presenti all’interno di un file CSV e inserirli all’interno di una collezione specifica di un database MongoDB. Inoltre, è possibile specificare altri due parametri (opzionali) in questo contesto:
  * **start**: specifica la posizione all’interno del file CSV del primo elemento da inserire nel database;
  * **end**: specifica la posizione all’interno del file CSV dell’ultimo elemento da inserire nel database.

