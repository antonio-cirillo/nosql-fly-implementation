### Inside compileJava function
```
import com.mongodb.client.*;
import org.bson.*;
import java.io.FileReader;
import com.opencsv.CSVReader;
import java.util.Properties;
import org.apache.log4j.PropertyConfigurator;
import com.github.vincentrussell.query.mongodb.sql.converter.MongoDBQueryHolder;
import com.github.vincentrussell.query.mongodb.sql.converter.QueryConverter;
import java.util.Iterator;
import tech.tablesaw.api.StringColumn;

private static Table __generateTableFromNoSQLQuery(MongoCursor <Document> ___mongoCursor) {
	if(!___mongoCursor.hasNext())
		return Table.create();
	org.json.JSONObject ___jsonObject = new org.json.JSONObject(___mongoCursor.next().toJson());
	org.json.JSONArray ___jsonArray = new org.json.JSONArray();
	Iterator <String> ___iteratorJsonObjectKeys = ___jsonObject.keys();
				
	while(___mongoCursor.hasNext())
		___jsonArray.put(new org.json.JSONObject(___mongoCursor.next().toJson()));
					
	Table ___tableToReturn = Table.create();
	HashMap <String, ArrayList <String>> ___hashMapFeaturesData = new HashMap <> ();
	ArrayList <String> ___jsonObjectKeys = new ArrayList <> ();
				
	while(___iteratorJsonObjectKeys.hasNext()) {
		String ___nextObjectFromIteratorJson = ___iteratorJsonObjectKeys.next();
		___hashMapFeaturesData.put(___nextObjectFromIteratorJson, new ArrayList <> ());
		___jsonObjectKeys.add(___nextObjectFromIteratorJson);
	}
				
	for(int ___firstIndexForGenerateTable = 0; ___firstIndexForGenerateTable < ___jsonArray.length(); ___firstIndexForGenerateTable++) {
		org.json.JSONObject ___currentJsonObject = ___jsonArray.getJSONObject(___firstIndexForGenerateTable);
		for(int ___secondIndexForGenerateTable = 0; ___secondIndexForGenerateTable < ___jsonObjectKeys.size(); ___secondIndexForGenerateTable++) {
			ArrayList <String> ___currentDataInsideHashMap = ___hashMapFeaturesData.get(___jsonObjectKeys.get(___secondIndexForGenerateTable));
			if(___jsonObjectKeys.get(___secondIndexForGenerateTable).equals("_id"))
				___currentDataInsideHashMap.add(___currentJsonObject.getJSONObject("_id").getString("$oid"));
			else
				___currentDataInsideHashMap.add(___currentJsonObject.getString(___jsonObjectKeys.get(___secondIndexForGenerateTable)));
			___hashMapFeaturesData.put(___jsonObjectKeys.get(___secondIndexForGenerateTable), ___currentDataInsideHashMap);
		}
	}
				
	for(int ___firstIndexForGenerateTable = 0; ___firstIndexForGenerateTable < ___jsonObjectKeys.size(); ___firstIndexForGenerateTable++) 
		___tableToReturn.addColumns(StringColumn.create(___jsonObjectKeys.get(___firstIndexForGenerateTable), 		
			___hashMapFeaturesData.get(___jsonObjectKeys.get(___firstIndexForGenerateTable))));
					
	return ___tableToReturn;
				
}
```
### Inside generateVariableDeclaration function
```
case "nosql":{
	 // var client = ((dec.right as DeclarationObject).features.get(1) as DeclarationFeature).value_s
	var database = ((dec.right as DeclarationObject).features.get(2) as DeclarationFeature).value_s
	var collection = ((dec.right as DeclarationObject).features.get(3) as DeclarationFeature).value_s
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

		MongoClient «dec.name» = MongoClients.create();
		MongoDatabase «dec.name»_«database» = «dec.name».getDatabase("«database»");
		«IF !(collection.nullOrEmpty)»
			MongoCollection <Document> «dec.name»_«database»_«collection» = «dec.name»_«database».getCollection("«collection»");
		«ENDIF»
	'''
}

case "query":{
	var query_type = ((dec.right as DeclarationObject).features.get(1) as DeclarationFeature).value_s
	if (query_type.equals("update")) {
		typeSystem.get(scope).put(dec.name, "int")
	} else if (query_type.equals("value")){
		typeSystem.get(scope).put(dec.name, "Table")
	} else {
		typeSystem.get(scope).put(dec.name, "Table")
	}
	var connectionVar = (((dec.right as DeclarationObject).features.get(2) 
		as DeclarationFeature).value_f as VariableDeclaration).right as DeclarationObject
	var typeDatabase = (connectionVar.features.get(0) as DeclarationFeature).value_s
	var connection = ((dec.right as DeclarationObject).features.get(2) as DeclarationFeature).value_f.name
	if (typeDatabase.equals("sql")) {
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
		var database = (connectionVar.features.get(2) as DeclarationFeature).value_s
		var collection = (connectionVar.features.get(3) as DeclarationFeature).value_s
		return '''
			QueryConverter «dec.name» = new QueryConverter.Builder().sqlString(«IF 
			((dec.right as DeclarationObject).features.get(3) as DeclarationFeature).value_s.nullOrEmpty
			»«((dec.right as DeclarationObject).features.get(3) as DeclarationFeature).value_f.name»
			« ELSE »"«((dec.right as DeclarationObject).features.get(3) as DeclarationFeature).value_s»"«ENDIF»).build();
			MongoDBQueryHolder «dec.name»Holder = «dec.name».getMongoQuery();
		'''
	}
}
```
### Inside generateVariableFunction
```
else if(databaseType.equals("nosql")){
	if(queryType.equals("select")) {
		return '''__generateTableFromNoSQLQuery(«(expression.target as VariableDeclaration).name»Cursor)'''
	}
}
```
### Inside generateVariableFunction
```
if (expression.target.right instanceof DeclarationObject) {
	var type = (expression.target.right as DeclarationObject).features.get(0).value_s
	switch (type){
		case "query":{
			if(expression.feature.equals("execute")){
				var queryType = (expression.target.right as DeclarationObject).features.get(1).value_s
				var connection = (((expression.target.right as DeclarationObject).features.get(2) as DeclarationFeature)
					.value_f as VariableDeclaration)
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
					var database = (connection.right as DeclarationObject).features.get(2).value_s
					var collection = (connection.right as DeclarationObject).features.get(3).value_s
					if(queryType.equals("select")) {
						return '''__generateTableFromNoSQLQuery(«connection.name»_«database»_
							«collection».find(«expression.target.name»Holder.getQuery().toBsonDocument()).cursor())'''
					}
				}
			}
					
		}
		case "nosql":{
			if(expression.feature.equals("insert")) {
				var arg = generateArithmeticExpression(expression.expressions.get(0), scope)
				var database = ((expression.target.right as DeclarationObject).features.get(2) as DeclarationFeature).value_s
				var collection = ((expression.target.right as DeclarationObject).features.get(3) as DeclarationFeature).value_s
				var nameVar = expression.target.name + "_" + database + "_" + collection
				if (expression.expressions.size() > 1) {
					if(!generateArithmeticExpression(expression.expressions.get(1), scope)
						.toString().matches("-?\\d+(\\.\\d+)?"))
						return ''''''
				}
					
			return '''if(«arg» instanceof File) {
				List <Document> documents = new ArrayList <Document> ();
				try (CSVReader reader = new CSVReader(new FileReader(«arg»))) {
					List <String[]> r = reader.readAll();
					String[] ___nosqlfeatures = r.get(0);
					for(int «arg»_count = 1; «arg»_count < 
						«IF(expression.expressions.size() > 1)»«generateArithmeticExpression(expression.expressions.get(1),
							scope)»«ELSE»r.size()«ENDIF»; ++«arg»_count) {
						Document ___dcmnt_tmp = new Document();
						for(int «arg»_c = 0; «arg»_c < ___nosqlfeatures.length; «arg»_c++) 
							___dcmnt_tmp.append(___nosqlfeatures[«arg»_c], r.get(«arg»_count)[«arg»_c]); 
							documents.add(___dcmnt_tmp);
						}
					}
					«nameVar».insertMany(documents);
				}
				'''
			}
		}
```
### Inside pom.xml
```
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
   	<groupId>com.github.vincentrussell</groupId>
   	<artifactId>sql-to-mongo-db-query-converter</artifactId>
   	<version>1.18</version>
</dependency>
<dependency>
    	<groupId>org.json</groupId>
    	<artifactId>json</artifactId>
    	<version>20210307</version>
</dependency>
```
