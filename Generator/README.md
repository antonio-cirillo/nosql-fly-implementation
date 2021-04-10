### Inside compileJava function
```
import com.mongodb.client.*;
import org.bson.*;
import java.io.FileReader;
import com.opencsv.CSVReader;
import java.util.Properties;
import org.apache.log4j.PropertyConfigurator;
import java.util.Iterator;
import tech.tablesaw.api.StringColumn;

private static List <Table> __generateTableFromNoSQLQuery(MongoCursor <Document> ___mongoCursor) {
	List <Table> ___resultGenerateTableFromNoSQLQuery = new ArrayList <> ();
				
	if(!___mongoCursor.hasNext()) 
		return ___resultGenerateTableFromNoSQLQuery;
							
	org.json.JSONObject ___jsonObject;
	ArrayList <ArrayList <String>> ___featuresOfMongoCursorResult = new ArrayList <> ();
	ArrayList <org.json.JSONArray> ___jsonObjectOfMongoCursorResult = new ArrayList <> ();
											
	while(___mongoCursor.hasNext()) {
		___jsonObject = new org.json.JSONObject(___mongoCursor.next().toJson());
		Iterator <String> ___iteratorJsonObjectKeys = ___jsonObject.keys(); 
								
		ArrayList <String> ___tmpFeatureOfJsonObject = new ArrayList <String> ();
		while(___iteratorJsonObjectKeys.hasNext())
			___tmpFeatureOfJsonObject.add(___iteratorJsonObjectKeys.next());
									
		boolean ___structureIsEquals = false;
		int ___indexGenerateTableFromNoSQLQuery = 0;
		for(; ___indexGenerateTableFromNoSQLQuery < ___featuresOfMongoCursorResult.size();
				++___indexGenerateTableFromNoSQLQuery) {
			if(___featuresOfMongoCursorResult.get(___indexGenerateTableFromNoSQLQuery).equals(___tmpFeatureOfJsonObject)) {
				___structureIsEquals = true;
				break;
			}
		}
					
		if(!___structureIsEquals) {
			___featuresOfMongoCursorResult.add(___tmpFeatureOfJsonObject);
			org.json.JSONArray ___tmpJsonArrayToAdd = new org.json.JSONArray();
			___tmpJsonArrayToAdd.put(___jsonObject);
			___jsonObjectOfMongoCursorResult.add(___tmpJsonArrayToAdd);
		} else {
			org.json.JSONArray ___tmpJsonArrayToAdd = 
			___jsonObjectOfMongoCursorResult.remove(___indexGenerateTableFromNoSQLQuery);
			___tmpJsonArrayToAdd.put(___jsonObject);
			___jsonObjectOfMongoCursorResult.add(___indexGenerateTableFromNoSQLQuery, ___tmpJsonArrayToAdd);
		}
				
	}
				
	for(int ___indexForCreatingTable = 0; ___indexForCreatingTable < ___featuresOfMongoCursorResult.size();
			++___indexForCreatingTable) {
		Table ___tableToReturn = Table.create();
		ArrayList <String> ___tmpFeaturesForColumns = ___featuresOfMongoCursorResult.get(___indexForCreatingTable);
		org.json.JSONArray ___tmpJsonArrayForThisTable = ___jsonObjectOfMongoCursorResult.get(___indexForCreatingTable);
		for(int ___indexForReadingFeatures = 0; 
				___indexForReadingFeatures < ___tmpFeaturesForColumns.size(); ___indexForReadingFeatures++) {
			String ___feature = ___tmpFeaturesForColumns.get(___indexForReadingFeatures);
			ArrayList <String> ___columnToAdd = new ArrayList <> ();
			for(int ___indexForReadingObject = 0; 
					___indexForReadingObject < ___tmpJsonArrayForThisTable.length(); ___indexForReadingObject++) {
				org.json.JSONObject ___tmpJsonForThisTable =
						___tmpJsonArrayForThisTable.getJSONObject(___indexForReadingObject);
				if(___feature.equals("_id"))
					___columnToAdd.add(___tmpJsonForThisTable.getJSONObject("_id").getString("$oid"));
				else
					___columnToAdd.add(___tmpJsonForThisTable.getString(___feature));
			}
			___tableToReturn.addColumns(
				StringColumn.create(
					___feature, ___columnToAdd));
		}
		___resultGenerateTableFromNoSQLQuery.add(___tableToReturn);
	}
				
	return 	___resultGenerateTableFromNoSQLQuery;
		
}
```
### Inside generateVariableDeclaration function
```
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
				var variables = (((dec.right as DeclarationObject).features.get(3) as DeclarationFeature)
					.value_f as VariableDeclaration).right as DeclarationObject
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
							for(int ___indexOfReadingFromCSV = «from» + 1; ___indexOfReadingFromCSV 
								< «to» + 1; ++___indexOfReadingFromCSV) {
								Document ___dcmnt_tmp = new Document();
								for(int ___indexOfReadingFromCSV2 = 0; ___indexOfReadingFromCSV2 < 
									___nosqlfeatures.length; ___indexOfReadingFromCSV2++) 
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
							for(int ___indexOfReadingFromCSV = 1; ___indexOfReadingFromCSV 
								< ___listOfArrayString.size(); ++___indexOfReadingFromCSV) {
								Document ___dcmnt_tmp = new Document();
								for(int ___indexOfReadingFromCSV2 = 0; ___indexOfReadingFromCSV2 < 
									___nosqlfeatures.length; ___indexOfReadingFromCSV2++) 
									___dcmnt_tmp.append(___nosqlfeatures[___indexOfReadingFromCSV2],
										___listOfArrayString.get(___indexOfReadingFromCSV)[___indexOfReadingFromCSV2]); 
								«dec.name».add(___dcmnt_tmp);
							}
						}
						'''
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
					«dec.name».add(Document.parse(___«dec.name»JsonArrayQuery.get(___indexForJsonArray).toString()));
				'''
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
```
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
```
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
```
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
    	<groupId>org.json</groupId>
    	<artifactId>json</artifactId>
    	<version>20210307</version>
</dependency>
```
