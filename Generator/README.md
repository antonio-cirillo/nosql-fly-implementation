### Inside compileJava function
```
import com.mongodb.client.*;
import org.bson.*;
import java.io.FileReader;
import com.opencsv.CSVReader;
import java.util.Properties;
import org.apache.log4j.PropertyConfigurator;
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
	
case "query-nosql":{
		var connection = ((dec.right as DeclarationObject).features.get(1) as DeclarationFeature).value_f.name
		return '''
			QueryConverter «dec.name» = new QueryConverter.Builder().sqlString(«IF 
			((dec.right as DeclarationObject).features.get(2) as DeclarationFeature).value_s.nullOrEmpty
			»«((dec.right as DeclarationObject).features.get(2) as DeclarationFeature).value_f.name»
			« ELSE »"«((dec.right as DeclarationObject).features.get(2) as DeclarationFeature).value_s»"«ENDIF»).build();
			MongoDBQueryHolder «dec.name»Holder = «dec.name».getMongoQuery();
		'''
	}
```
### Inside generateVariableFunction
```
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
					«IF(expression.expressions.size() > 1)»
					«generateArithmeticExpression(expression.expressions.get(1), scope)»
					«ELSE»r.size()«ENDIF»; ++«arg»_count) {
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
### Inside valuateArithmeticExpression
```
else if(type.equals("query-nosql")) {
	return "Table"
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
```
