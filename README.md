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

## Modifiche effettuate al generatore
- [Generatore Java](#generatore-java)
### Generatore Java
All'interno della funzione `compileJava` del generatore, sono stati aggiunti tutti gli import necessari per poter utilizzare MongoDB e per poter interagire con esso.
```java import
import com.mongodb.client.*;
import org.bson.*;
import java.io.FileReader;
import com.opencsv.CSVReader;
import java.util.Properties;
import org.apache.log4j.PropertyConfigurator;
import java.util.Iterator;
import tech.tablesaw.api.StringColumn;
import tech.tablesaw.api.IntColumn;
```
Oltre agli import alle librerie neccesarie, sono state aggiunte le implementazioni delle funzioni globali `___generateTableFromNoSQLQuery`, necessaria per poter ottenere una 
lista di oggetti `tech.tablesaw.Table` a partire da un oggetto di tipo `com.mongodb.client.MongoCursor`.
```java
private static List <Table> __generateTableFromNoSQLQuery(MongoCursor <Document> ___mongoCursor) {
		List <Table> ___resultGenerateTableFromNoSQLQuery = new ArrayList <> ();
				
	if(!___mongoCursor.hasNext()) 
		return ___resultGenerateTableFromNoSQLQuery;
							
	org.json.JSONObject ___jsonObject; // L'oggetto corrente che andiamo a leggere.
	ArrayList <ArrayList <String>> ___listFeatures = new ArrayList <> (); // La liste di tutte le features.
	ArrayList <org.json.JSONArray> ___listObjects = new ArrayList <> (); // La corrispondente lista di oggetti per quel tipo di features.
											
	while(___mongoCursor.hasNext()) {
		___jsonObject = new org.json.JSONObject(___mongoCursor.next().toJson()); // Leggiamo un oggetto.
		Iterator <String> ___iteratorJsonObjectKeys = ___jsonObject.keys(); // Otteniamo le features dell'oggetto appena letto.		
		ArrayList <String> ___listFeaturesCurrent = new ArrayList <String> (); // La liste delle features dell'oggetto che stiamo leggendo.
		
		while(___iteratorJsonObjectKeys.hasNext())
			___listFeaturesCurrent.add(___iteratorJsonObjectKeys.next());
		// Ora abbiamo la lista completa delle features che compongo l'oggetto appena letto.				
						
		int ___i = 0;			
		// Andiamo a contraollare se la struttura dell'oggetto che stiamo leggendo corrisponde ad una struttura già letta.
		for(; ___i < ___listFeatures.size(); ++___i)
			if(___listFeatures.get(___i).equals(___listFeaturesCurrent))
				break;
		
		if(___i >= ___listFeatures.size()) { // Se non abbiamo ancora letto un oggetto con questa struttura, allora... 
			___listFeatures.add(___listFeaturesCurrent); // Aggiungiamo questa struttura alla lista delle strutture,
			org.json.JSONArray ___listObjectForThisFeatures = new org.json.JSONArray(); // Creaiamo la lista di oggetti corrispondenti per questa struttura,
			___listObjectForThisFeatures.put(___jsonObject); // Aggiungiamo l'oggetto che abbiamo letto alla lista di oggetti,
			___listObjects.add(___listObjectForThisFeatures); // Aggiungiamo la lista di oggetti per questa struttura alla lista di tutti gli oggetti.
		} else  // Se abbiamo letto già un oggetto con questa struttura, allora...
			___listObjects.get(___i).put(___jsonObject); // Aggiungiamo l'oggetto alla lista degli oggetti con questa struttura.			
	}
	
	for(int ___i = 0; ___i < ___listFeatures.size(); ++___i) { // Ora per ogni lista di features... 
		Table ___table = Table.create(); // Creiamo una tabella,
		ArrayList <String> ___features = ___listFeatures.get(___i); // Otteniamo le features,
		org.json.JSONArray ___objects = ___listObjects.get(___i); // Otteniamo gli oggetti che hanno queste features,
		
		for(Column <?> ___column : ___generateColumns("", ___features, ___objects))
			___table.addColumns(___column); // Creiamo la colonna
		
		___resultGenerateTableFromNoSQLQuery.add(___table);
	}			
	return 	___resultGenerateTableFromNoSQLQuery;		
	
}
```
Infine, abbiamo l'implementazione della funzione `___generateColumns`. Quest'ultima è una funzione ricorsiva in grado di generare, a partire da un oggetto di tipo `org.json.JSONArray` contente una serie di oggetti al suo interno, e la lista di features dell'oggetto JSONArray, una lista di colonne tipizzate. Quest'ultima serve per consentire alla funzione descritta sopra, di poter generare ricorsivamente tutte le colonne che verranno poi aggiunte all'interno di una singola tabella.
```java
private static List <Column <?>> ___generateColumns(
		String ___nameColumn, List <String> ___features, org.json.JSONArray ___objects) {
	List <Column <?>> ___columns = new ArrayList <> ();
	int ___j = 0;
	
	for(String ___feature : ___features) {				
		org.json.JSONObject ___object = ___objects.getJSONObject(0);
		Object ___value = ___object.get(___feature);
		
		if(___value instanceof org.json.JSONObject) {
			___columns.add(StringColumn.create(___feature, ((org.json.JSONObject) ___value).toString()));
			
			for(int ___i = 1; ___i < ___objects.length(); ++___i) {					
				___object = ___objects.getJSONObject(___i);
				___value = ___object.get(___feature);
		
				((StringColumn) ___columns.get(___j)).append(((org.json.JSONObject) ___value).toString());
			}
			++___j;
								
		} else if(___value instanceof org.json.JSONArray) {
			___columns.add(StringColumn.create(___feature, ((org.json.JSONArray) ___value).toString()));
			
			for(int ___i = 1; ___i < ___objects.length(); ++___i) {					
				___object = ___objects.getJSONObject(___i);
				___value = ___object.get(___feature);
			
				((StringColumn) ___columns.get(___j)).append(((org.json.JSONArray) ___value).toString());
			}
			++___j;
			
		} else {
		
			if(___value instanceof Integer) {
				IntColumn ___columnToAdd = IntColumn.create(___nameColumn + ___feature);
				___columnToAdd.append((Integer) ___value);
				___columns.add(___columnToAdd);
			} else				
				___columns.add(StringColumn.create(___nameColumn +___feature, "" + ___value));
			
			for(int ___i = 1; ___i < ___objects.length(); ++___i) {					
				___object = ___objects.getJSONObject(___i);
				___value = ___object.get(___feature);
				
				if(___value instanceof Integer)
					((IntColumn) ___columns.get(___j)).append((Integer) ___value);
				else
					((StringColumn) ___columns.get(___j)).append("" + ___value);					
			}
			++___j;
					
		}								
	}			
	return ___columns;
		
}
```
### Inside generateVariableDeclaration function
```java
case "nosql":{
	// var client = ((dec.right as DeclarationObject).features.get(1) as DeclarationFeature).value_s
	var database = ((dec.right as DeclarationObject).features.get(2) as DeclarationFeature).value_s
	var collection = ((dec.right as DeclarationObject).features.get(3) as DeclarationFeature).value_s
	if((dec.right as DeclarationObject).features.size() < 5) {
		return '''
		try {
			Properties props = new Properties();
			props.put("log4j.rootLogger", "INFO, stdout");
			props.put("log4j.appender.stdout", "org.apache.log4j.ConsoleAppender");
			props.put("log4j.appender.stdout.Target", "System.out");
			props.put("log4j.appender.stdout.layout", "org.apache.log4j.PatternLayout");
			props.put("log4j.appender.stdout.layout.ConversionPattern", "%d{yy/MM/dd HH:mm:ss} %p %c{2}: %m%n");
			FileOutputStream outputStream = new FileOutputStream("log4j.properties");
			props.store(outputStream, "This is a default properties file");
			System.out.println("Default properties file created.");
		} catch (IOException e) {
			System.out.println("Default properties file not created.");
			e.printStackTrace();
		}
									
		String log4jConfPath = "log4j.properties";
		PropertyConfigurator.configure(log4jConfPath);
		
		MongoCollection <Document> «dec.name» = MongoClients.create().getDatabase("«database»").getCollection("«collection»");
		'''
	} else {
		return '''
		PropertyConfigurator.configure(«IF 
		((dec.right as DeclarationObject).features.get(4) as DeclarationFeature).value_s.nullOrEmpty
		»«((dec.right as DeclarationObject).features.get(4) as DeclarationFeature).value_f.name
		»«ELSE
		»"«((dec.right as DeclarationObject).features.get(4) as DeclarationFeature).value_s»"«ENDIF»);
							
		MongoCollection <Document> «dec.name» = MongoClients.create().getDatabase("«database»").getCollection("«collection»");
		'''
	}
}
```
```java
case "query":{
	var query_type = ((dec.right as DeclarationObject).features.get(1) as DeclarationFeature).value_s
	var connectionVar = (((dec.right as DeclarationObject).features.get(2) as DeclarationFeature).value_f as VariableDeclaration).right as DeclarationObject
	var typeDatabase = (connectionVar.features.get(0) as DeclarationFeature).value_s
	var connection = ((dec.right as DeclarationObject).features.get(2) as DeclarationFeature).value_f.name
	if(typeDatabase.equals("sql")) {
		if(query_type.equals("update")){
			typeSystem.get(scope).put(dec.name, "int")
		} else if(query_type.equals("value")){
			typeSystem.get(scope).put(dec.name, "Table")
		} else {
			typeSystem.get(scope).put(dec.name, "Table")
		}
		return '''
		PreparedStatement «dec.name» = «connection».prepareStatement(
		«IF 
			((dec.right as DeclarationObject).features.get(3) as DeclarationFeature).value_s.nullOrEmpty
		»
		«((dec.right as DeclarationObject).features.get(3) as DeclarationFeature).value_f.name»
		« ELSE » 
			"«((dec.right as DeclarationObject).features.get(3) as DeclarationFeature).value_s»"
		«ENDIF»
		);
		'''
	} else if(typeDatabase.equals("nosql")) {
		if(query_type.equals("select")){
			typeSystem.get(scope).put(dec.name, "List <Table>")
		} else if(query_type.equals("delete")){
			typeSystem.get(scope).put(dec.name, "boolean")
		}
		if(query_type.equals("insert")) {
			if(((dec.right as DeclarationObject).features.get(3) as DeclarationFeature).value_s.nullOrEmpty) {
				if((((dec.right as DeclarationObject).features.get(3) as DeclarationFeature).value_f as VariableDeclaration).right instanceof DeclarationObject) {
					var variables = (((dec.right as DeclarationObject).features.get(3) as DeclarationFeature).value_f as VariableDeclaration).right as DeclarationObject
					if(variables.features.get(0).value_s.equals("file")) {
						if((dec.right as DeclarationObject).features.size() == 6) {
							var from = ((dec.right as DeclarationObject).features.get(4) as DeclarationFeature).value_s
							var to = ((dec.right as DeclarationObject).features.get(5) as DeclarationFeature).value_s
							return '''
							List <Document> «dec.name» = new ArrayList <Document> ();
							try (CSVReader ___CSVReader = new CSVReader(new FileReader(
									«((dec.right as DeclarationObject).features.get(3) as DeclarationFeature).value_f.name»))) {
								List <String[]> ___listOfArrayString = ___CSVReader.readAll();
								String[] ___nosqlfeatures = ___listOfArrayString.get(0);
								for(int ___indexOfReadingFromCSV = «from» + 1; ___indexOfReadingFromCSV < «to» + 1; ++___indexOfReadingFromCSV) {
									Document ___dcmnt_tmp = new Document();
									for(int ___indexOfReadingFromCSV2 = 0; ___indexOfReadingFromCSV2 < ___nosqlfeatures.length; ___indexOfReadingFromCSV2++) 
										___dcmnt_tmp.append(___nosqlfeatures[___indexOfReadingFromCSV2], 
												___listOfArrayString.get(___indexOfReadingFromCSV)[___indexOfReadingFromCSV2]); 
									«dec.name».add(___dcmnt_tmp);
								}
							}
							'''
						} else {
							return '''
							List <Document> «dec.name» = new ArrayList <Document> ();
							try (CSVReader ___CSVReader = new CSVReader(new FileReader(
									«((dec.right as DeclarationObject).features.get(3) as DeclarationFeature).value_f.name»))) {
								List <String[]> ___listOfArrayString = ___CSVReader.readAll();
								String[] ___nosqlfeatures = ___listOfArrayString.get(0);
								for(int ___indexOfReadingFromCSV = 1; ___indexOfReadingFromCSV < ___listOfArrayString.size(); ++___indexOfReadingFromCSV) {
									Document ___dcmnt_tmp = new Document();
									for(int ___indexOfReadingFromCSV2 = 0; ___indexOfReadingFromCSV2 < ___nosqlfeatures.length; ___indexOfReadingFromCSV2++) 
										___dcmnt_tmp.append(___nosqlfeatures[___indexOfReadingFromCSV2], 
												___listOfArrayString.get(___indexOfReadingFromCSV)[___indexOfReadingFromCSV2]); 
									«dec.name».add(___dcmnt_tmp);
								}
							}
							'''
						}
					} 
				} else {
					return '''
					String ___«dec.name»Statement = «((dec.right as DeclarationObject).features.get(3) as DeclarationFeature).value_f.name»;
					if(___«dec.name»Statement.charAt(0) != '[')
						___«dec.name»Statement = "[" + ___«dec.name»Statement + "]";

					org.json.JSONArray ___«dec.name»JsonArrayQuery = new org.json.JSONArray(___«dec.name»Statement);
					List <Document> «dec.name» = new ArrayList <Document> ();

					for(int ___indexForJsonArray = 0; ___indexForJsonArray < ___«dec.name»JsonArrayQuery.length(); ___indexForJsonArray++) 
						«dec.name».add(Document.parse(___«dec.name»JsonArrayQuery.get(___indexForJsonArray).toString()));
					'''
				}
			} else {
				return '''
				String ___«dec.name»Statement = "«((dec.right as DeclarationObject).features.get(3) as DeclarationFeature).value_s»";
				if(___«dec.name»Statement.charAt(0) != '[')
					___«dec.name»Statement = "[" + ___«dec.name»Statement + "]";

				org.json.JSONArray ___«dec.name»JsonArrayQuery = new org.json.JSONArray(___«dec.name»Statement);
				List <Document> «dec.name» = new ArrayList <Document> ();

				for(int ___indexForJsonArray = 0; ___indexForJsonArray < ___«dec.name»JsonArrayQuery.length(); ___indexForJsonArray++) 
					«dec.name».add(Document.parse(___«dec.name»JsonArrayQuery.get(___indexForJsonArray).toString()));'''
			}
		} else {
			return '''
			BsonDocument «dec.name» = Document.parse(«IF 
				((dec.right as DeclarationObject).features.get(3) as DeclarationFeature).value_s.nullOrEmpty
			»
			«((dec.right as DeclarationObject).features.get(3) as DeclarationFeature).value_f.name»
			« ELSE » 
				"«((dec.right as DeclarationObject).features.get(3) as DeclarationFeature).value_s»"«ENDIF»).toBsonDocument();
			'''
		}
	}
}
```
### Inside generateVariableFunction
```java
case "query":{
	if(expression.feature.equals("execute")){
		var queryType = (expression.target.right as DeclarationObject).features.get(1).value_s
		var connection = (((expression.target.right as DeclarationObject).features.get(2) as DeclarationFeature).value_f as VariableDeclaration)
		var databaseType = (connection.right as DeclarationObject).features.get(0).value_s
		if(databaseType.equals("sql")) {
			if(queryType.equals("update")){
				return '''«(expression.target as VariableDeclaration).name».executeUpdate();'''
			} else if(queryType.equals("value")){
				return '''Table.read().db(
					«(expression.target as VariableDeclaration).name».executeQuery()
					).printAll().replaceAll("[^\\d.]+|\\.(?!\\d)", "");'''
			} else {
				return '''Table.read().db(
					«(expression.target as VariableDeclaration).name».executeQuery()
					);'''
			}
		} else if(databaseType.equals("nosql")){
			if(queryType.equals("select")) {
				return '''
					__generateTableFromNoSQLQuery(«connection.name».find(«expression.target.name»).cursor());'''
			} else if(queryType.equals("delete")) {
				return '''
				(«connection.name».deleteMany(«expression.target.name»).getDeletedCount() > 0) ? true : false;'''
			} else if(queryType.equals("insert")) {
				return '''
				«connection.name».insertMany(«expression.target.name»);'''
			}
		}
	}					
}
```
### Inside generateFor
```java
else if(typeSystem.get(scope).get((object as VariableLiteral).variable.name).equals("List <Table>")) {
	var name = (object as VariableLiteral).variable.name;
	var index_name = (indexes.indices.get(0) as VariableDeclaration).name
	typeSystem.get(scope).put(index_name,name);
	return '''
	for(Table «index_name» : «name») {
		«IF body instanceof BlockExpression»
			«FOR exp : body.expressions »
				«generateExpression(exp,scope)»
			«ENDFOR»
		«ELSE»
			«generateExpression(body,scope)»
		«ENDIF»
	}
	'''
}
```
### Inside valuateArithmeticExpression 
```java
else if (type.equals("query")){
	var queryType = (exp.target.right as DeclarationObject).features.get(1).value_s
	var typeDatabase = (((exp.target.right as DeclarationObject)
			.features.get(2).value_f as VariableDeclaration).right as DeclarationObject).features.get(0).value_s
	if(typeDatabase.equals("sql")) {
		if(queryType.equals("update")){
			return "int"
		} else if(queryType.equals("value")){
			return "String"
		} else {
			return "Table"
		}
	} else {
		if(queryType.equals("select")){
			return "List <Table>"
		} else {
			return "boolean"
		}
	}
} 
```
### Inside pom.xml
```xml
<dependency>
	<groupId>org.mongodb</groupId>
	<artifactId>mongodb-driver-sync</artifactId>
	<version>4.2.2</version>
</dependency>
<dependency>
	<groupId>com.opencsv</groupId>
	<artifactId>opencsv</artifactId>
	<version>5.3</version>
</dependency>
<dependency>
    	<groupId>org.slf4j</groupId>
    	<artifactId>slf4j-api</artifactId>
    	<version>1.7.13</version>
</dependency>
<dependency>
    	<groupId>org.slf4j</groupId>
    	<artifactId>slf4j-log4j12</artifactId>
    	<version>1.7.13</version>
</dependency>
<dependency>
    	<groupId>org.json</groupId>
    	<artifactId>json</artifactId>
    	<version>20210307</version>
</dependency>
```
