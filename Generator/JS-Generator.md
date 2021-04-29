All'interno della funzione `generateBodyJS`.
```node
var __nosql = require("mongodb");

«IF (((exp as VariableDeclaration).right as DeclarationObject).features.get(0).value_s.equals("nosql"))»
	await __«(exp as VariableDeclaration).name»Client.close();
«ENDIF»
```
All'interno della funzione `generateJsExpression`.
```node
case "nosql":{
	var client = ((exp.right as DeclarationObject).features.get(1) as DeclarationFeature)
	var database = ((exp.right as DeclarationObject).features.get(2) as DeclarationFeature).value_s
	var collection = ((exp.right as DeclarationObject).features.get(3) as DeclarationFeature).value_s
	return '''
	const __«exp.name»Client = new __nosql.MongoClient(
	  «IF client.value_s.nullOrEmpty»
	  «client.value_f.name»,
	  «ELSE»
	  "«client.value_s»",
	  «ENDIF»
		{ useUnifiedTopology: true }
	);
							
	await __«exp.name»Client.connect();
				
	const «exp.name» = __«exp.name»Client.db("«database»").collection("«collection»");
	'''
}

case "query":{
	var connection = (((exp.right as DeclarationObject).features.get(2) as DeclarationFeature).value_f as VariableDeclaration)	
	var databaseType = (connection.right as DeclarationObject).features.get(0).value_s
	if(databaseType.equals("sql")) {
		return '''
		var «exp.name» = «
		IF ((exp.right as DeclarationObject).features.get(3) as DeclarationFeature).value_s.nullOrEmpty
		»«((exp.right as DeclarationObject).features.get(3) as DeclarationFeature).value_f.name»«
		ELSE
		»"«((exp.right as DeclarationObject).features.get(3) as DeclarationFeature).value_s»"«
		ENDIF»;
		'''	 
	} else if(databaseType.equals("nosql")) {
		return '''
		const «exp.name» = «
		IF ((exp.right as DeclarationObject).features.get(3) as DeclarationFeature).value_s.nullOrEmpty
		»JSON.parse(«((exp.right as DeclarationObject).features.get(3) as DeclarationFeature).value_f.name»)«
		ELSE 
		»JSON.parse("«((exp.right as DeclarationObject).features.get(3) as DeclarationFeature).value_s»")«
		ENDIF»;
		
		'''
	}
}
```
All'interno della funzione `generateJsVariableFunction`.
```node
case "query":{
	var queryType = (expression.target.right as DeclarationObject).features.get(1).value_s
	if(expression.feature.equals("execute")){
		var connection = (expression.target.right as DeclarationObject).features.get(2).value_f.name
		var databaseType = ((((expression.target.right as DeclarationObject).features.get(2) as DeclarationFeature)
			.value_f as VariableDeclaration).right as DeclarationObject).features.get(0).value_s
		if(databaseType.equals("sql")) {
			if (queryType.equals("value")){
				return '''
				JSON.stringify(
					await (__util.promisify(«connection».query).bind(«connection»))(
				«IF(expression.target.right as DeclarationObject).features.get(3).value_s.nullOrEmpty»
					«(expression.target.right as DeclarationObject).features.get(3).value_f.name»
				«ELSE» 
					"«(expression.target.right as DeclarationObject).features.get(3).value_s»"
				«ENDIF»
					)
				).match(/[+-]?\d+(?:\.\d+)?/g);
			''' 
			} else {
				return '''
				await (__util.promisify(«connection».query).bind(«connection»))(
				«IF(expression.target.right as DeclarationObject).features.get(3).value_s.nullOrEmpty»
					«(expression.target.right as DeclarationObject).features.get(3).value_f.name»
				«ELSE» 
					"«(expression.target.right as DeclarationObject).features.get(3).value_s»"
				«ENDIF»
				);
				''' 
			}
		} else if(databaseType.equals("nosql")) {
			if(queryType.equals("select")) {
				return '''
				await «connection».find(«expression.target.name»);'''
			} else if(queryType.equals("delete")) {
				return '''
				await «connection».deleteMany(«expression.target.name»);'''
			} else if(queryType.equals("insert")) {
				return '''
				await «connection».insertMany(«expression.target.name»);'''
//			} else if(queryType.equals("update")) {
//				return '''
//				«connection».updateMany(«expression.target.name»_filter, «expression.target.name»);'''
//			} else if(queryType.equals("replace")) {
//				return '''
//				«connection».replaceOne(«expression.target.name»_filter, «expression.target.name»);'''
			}
		}
	}
}
```
All'interno delle funzioni `AWSDeploy`, `AWSDebugDeploy`.
```bash
echo "npm install mongodb"
		npm install mongodb
		if [ $? -eq 0 ]; then
			echo "..."
		else
			echo "npm install mysql failed"
			exit 1
		fi
```
All'interno della funzione `AzureDeploy`.
```bash
echo '{
  "name": "'${function}'",
	"version": "1.0.0",
	"main": "index.js",
	"dependencies": {
	  "azure-storage": "2.10.3",
	  "async": "3.2.0",
	  "axios": "0.19.2",
	  "qs": "6.9.4",
	  "util": "0.12.3",
	  "dataframe-js": "1.4.3",
	  "mysql": "2.18.1",
	  "mongodb": "^3.6.6"
},
```
