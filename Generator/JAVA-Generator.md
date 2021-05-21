## Modifiche effettuate al generatore
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
All'interno della funzione `generateVariableDeclaration` è stato aggiunto il meccanismo di dichiarazione di variabili di tipo nosql.
```java
case "nosql":{
	var database = ((dec.right as DeclarationObject).features.get(1) as DeclarationFeature).value_s
	var collection = ((dec.right as DeclarationObject).features.get(2) as DeclarationFeature).value_s
	if((dec.right as DeclarationObject).features.size() < 4) {
		return '''
		if(!(new File("log4j.properties").exists())) {
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
		}
			
		PropertyConfigurator.configure("log4j.properties");
					
		MongoCollection <Document> «dec.name» = MongoClients.create().getDatabase("«database»").getCollection("«collection»");
		'''
	} else {
		return '''
		PropertyConfigurator.configure(«IF 
		((dec.right as DeclarationObject).features.get(3) as DeclarationFeature).value_s.nullOrEmpty
		»«((dec.right as DeclarationObject).features.get(3) as DeclarationFeature).value_f.name
		»«ELSE
		»"«((dec.right as DeclarationObject).features.get(3) as DeclarationFeature).value_s»"«ENDIF»);
							
		MongoCollection <Document> «dec.name» = MongoClients.create().getDatabase("«database»").getCollection("«collection»");
		'''
	}
}
```
Inoltre, sempre all'interno della funzione `generateVariableDeclaration` è stato modificato il funzionamento delle dichiarazioni di variabili di tipo  `query` in modo da adattarle 
anche a query su database NoSQL.
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
			typeSystem.get(scope).put(dec.name, "long")
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
		} else if(query_type.equals("update")) { 
			if((dec.right as DeclarationObject).features.size() == 5)
				return '''
					BsonDocument «dec.name»_filter = Document.parse(«IF 
						((dec.right as DeclarationObject).features.get(3) as DeclarationFeature).value_s.nullOrEmpty
					»
					«((dec.right as DeclarationObject).features.get(3) as DeclarationFeature).value_f.name»
					« ELSE » 
						"«((dec.right as DeclarationObject).features.get(3) as DeclarationFeature).value_s»"«ENDIF»).toBsonDocument();
										
					BsonDocument «dec.name» = Document.parse(«IF 
						((dec.right as DeclarationObject).features.get(4) as DeclarationFeature).value_s.nullOrEmpty
					»
					«((dec.right as DeclarationObject).features.get(4) as DeclarationFeature).value_f.name»
					« ELSE » 
						"«((dec.right as DeclarationObject).features.get(4) as DeclarationFeature).value_s»"«ENDIF»).toBsonDocument();
					'''	
		} else if(query_type.equals("replace")) {
			if((dec.right as DeclarationObject).features.size() == 5)
				return '''
				BsonDocument «dec.name»_filter = Document.parse(«IF 
					((dec.right as DeclarationObject).features.get(3) as DeclarationFeature).value_s.nullOrEmpty
				»
					«((dec.right as DeclarationObject).features.get(3) as DeclarationFeature).value_f.name»
				« ELSE » 
					"«((dec.right as DeclarationObject).features.get(3) as DeclarationFeature).value_s»"«ENDIF»).toBsonDocument();
										
				Document «dec.name» = Document.parse(«IF 
					((dec.right as DeclarationObject).features.get(4) as DeclarationFeature).value_s.nullOrEmpty
				»
					«((dec.right as DeclarationObject).features.get(4) as DeclarationFeature).value_f.name»
				« ELSE » 
					"«((dec.right as DeclarationObject).features.get(4) as DeclarationFeature).value_s»"«ENDIF»);
				'''
		}	else {
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
All'interno della funzione  `generateVariableFunction` è stato modificato il comportamento della funzione `execute` su variabili di tipo `query`, in modo da poter eseguire questo 
comando su tutti i tipi di query applicabili ad un databse NoSQL.
```java
case "query":{
	if(expression.feature.equals("execute")){
		var queryType = (expression.target.right as DeclarationObject).features.get(1).value_s
		var connection = (((expression.target.right as DeclarationObject).features.get(2) as DeclarationFeature).value_f as VariableDeclaration)
		var databaseType = (connection.right as DeclarationObject).features.get(0).value_s
		if(databaseType.equals("sql")) {
			if(queryType.equals("update")) {
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
		} else if(databaseType.equals("nosql")) {
			if(queryType.equals("select")) {
				return '''
					__generateTableFromNoSQLQuery(«connection.name».find(«expression.target.name»).cursor());'''
			} else if(queryType.equals("delete")) {
				return '''
				«connection.name».deleteMany(«expression.target.name»).getDeletedCount();'''
			} else if(queryType.equals("insert")) {
				return '''
				«connection.name».insertMany(«expression.target.name»);'''
			} else if(queryType.equals("update")) {
				return '''
				«connection.name».updateMany(«expression.target.name»_filter, «expression.target.name»);'''
			} else if(queryType.equals("replace")) {
				return '''
				«connection.name».replaceOne(«expression.target.name»_filter, «expression.target.name»);'''
			}
		}
	}					
}
```
All'interno della funzione `generateLocalFlyFunction`.
```java
else if ((call.input as FunctionInput).f_index instanceof VariableLiteral &&
		typeSystem.get(scope).get(((call.input as FunctionInput).f_index as VariableLiteral).variable.name) != null &&
		typeSystem.get(scope).get(((call.input as FunctionInput).f_index as VariableLiteral).variable.name).equals("List <Table>")) {
	s+='''
	final int __numThread = (Integer) __fly_environment.get("«call.environment.name»").get("nthread");
	ArrayList <ArrayList <Table>> __list_data_«call.target.name» = new ArrayList<> ();
	for(int __i = 0; __i < __numThread; ++__i)
		__list_data_«call.target.name».add(new ArrayList <Table> ());
	for(int __j = 0; __j < «generateArithmeticExpression((call.input as FunctionInput).f_index,scope)».size(); ++__j) {
		Table ___table = «generateArithmeticExpression((call.input as FunctionInput).f_index,scope)».get(__j);
		for(int __i = 0; __i < __numThread; ++__i) 
			__list_data_«call.target.name».get(__i).add(___table.emptyCopy());
		for(int __i = 0; __i < ___table.rowCount(); ++__i)
			__list_data_«call.target.name».get(__i % __numThread).get(__j).addRow(__i, ___table);
	}
	for(int __i = 0; __i < __numThread; ++__i) {
	final ArrayList <Table> __tables = __list_data_«call.target.name».get(__i);
	Future<Object> __f = __thread_pool_«call.environment.name».submit(new Callable<Object>() {
		public Object call() throws Exception {
			Object __ret = «call.target.name»(__tables);
			return __ret;
		}
	});
	«call.target.name»_«func_ID»_return.add(__f);
	}
	'''
}
```
All'interno della funzione `generateFor` è stato inserito il meccanismo di iterazione da applicare sulle variabili di tipo `List <Table>`.
```java
else if(typeSystem.get(scope).get((object as VariableLiteral).variable.name).equals("List <Table>")) {
	var name = (object as VariableLiteral).variable.name;
	(indexes.indices.get(0) as VariableDeclaration).typeobject='var'
	var index_name = (indexes.indices.get(0) as VariableDeclaration).name
	typeSystem.get(scope).put(index_name, "Table");
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
All'interno della funzione `valuateArithmeticExpression` sono stati definiti i tipi di ritorno delle varie operazioni CRUD effettuate su database di tipo NoSQL.
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
			return "long"
		}
	}
} 
```
All'interno del file `pom.xml` sono state aggiunte queste dipendenze:
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
