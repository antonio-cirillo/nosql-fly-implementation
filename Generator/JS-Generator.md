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
	  «client.value_s»,
	  «ENDIF»
		{ useUnifiedTopology: true }
	);
							
	await __«exp.name»Clinet.connect();
				
	const «exp.name» = __«exp.name»Clinet.db("«database»").collection("«collection»");
	'''
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
