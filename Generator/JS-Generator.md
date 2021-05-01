All'interno della funzione `generateBodyJS`.
```node
var __nosql = require("mongodb");
var __fs = require("fs")
var __parse = require("csv-parse");

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
		var query_type = ((exp.right as DeclarationObject).features.get(1) as DeclarationFeature).value_s						
		if(query_type.equals("select"))
			typeSystem.get(scope).put(exp.name, "List <Table>")
		if(query_type.equals("insert")) {
			if(((exp.right as DeclarationObject).features.get(3) as DeclarationFeature).value_s.nullOrEmpty) {
				if((((exp.right as DeclarationObject).features.get(3) as DeclarationFeature).value_f as VariableDeclaration).right instanceof DeclarationObject) {
					var variables = (((exp.right as DeclarationObject).features.get(3) as DeclarationFeature).value_f as VariableDeclaration).right as DeclarationObject
					if(variables.features.get(0).value_s.equals("file")) {
						if((exp.right as DeclarationObject).features.size() == 6) {
							var from = ((exp.right as DeclarationObject).features.get(4) as DeclarationFeature).value_s
							var to = ((exp.right as DeclarationObject).features.get(5) as DeclarationFeature).value_s
							return '''
							const «exp.name» = async () => {
														
								let i = 0;
								let features;
								let objects = [];
														
								await new Promise((resolve) => {
												
									__fs.createReadStream(«IF
									(variables.features.get(1).value_s.nullOrEmpty)»«variables.features.get(1).value_f.name»«
									ELSE»"«variables.features.get(1).value_s»"«ENDIF»)
									.pipe(__parse())
									.on("data", (row) => {
										if(i == 0) {
											features = row;
											++i;
										} else if(i >= «from» && i <= «to») {
											let object = { };
											for([index, value] of features.entries())
												object[features[index]] = row[index];
											objects.push(object);
											++i;
										} else if(i < «from»)
											++i;
										})
									.on("end", () => {
										resolve();
									});
								});
														
								return objects;
							}
													
							'''
						} else {
							return '''
							const «exp.name» = async () => {
														
								let i = 0;
								let features;
								let objects = [];
														
								await new Promise((resolve) => {
														
									__fs.createReadStream(«IF
									(variables.features.get(1).value_s.nullOrEmpty)»«variables.features.get(1).value_f.name»«
									ELSE»"«variables.features.get(1).value_s»"«ENDIF»)
									.pipe(__parse())
									.on("data", (row) => {
										if(i == 0) {
											features = row;
											++i;
										} else {
											let object = { };
											for([index, value] of features.entries())
												object[features[index]] = row[index];
											objects.push(object);
											++i;
										}
									})
								.on("end", () => {
									resolve();
								});
							});
											
							return objects;
						}
									
					'''
					}
				}
			} else {
				return '''
				let «exp.name»;
				if(«((exp.right as DeclarationObject).features.get(3) as DeclarationFeature).value_f.name».charAt(0) === "[")
					«exp.name» = JSON.parse(«((exp.right as DeclarationObject).features.get(3) as DeclarationFeature).value_f.name»);
				else
					«exp.name» = JSON.parse("[" + «((exp.right as DeclarationObject).features.get(3) as DeclarationFeature).value_f.name» + "]");
								
				'''
			}
		} else {
			return '''
			let «exp.name»;
			if("«((exp.right as DeclarationObject).features.get(3) as DeclarationFeature).value_s»".charAt(0) === "[")
				«exp.name» = JSON.parse("«((exp.right as DeclarationObject).features.get(3) as DeclarationFeature).value_s»");
			else
				«exp.name» = JSON.parse("[" + "«((exp.right as DeclarationObject).features.get(3) as DeclarationFeature).value_s»" + "]");
											
			'''
			}
		} else {
			return ''''''				
		}								
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
		if(queryType.equals("insert")) {
			if((expression.target.right as DeclarationObject).features.get(3).value_s.nullOrEmpty) {
				if((expression.target.right as DeclarationObject).features.get(3).value_f.right instanceof DeclarationObject)
					return '''
					await «connection».insertMany((await «expression.target.name»()));
										
					'''
				else 
					return '''
					await «connection».insertMany(«expression.target.name»);
										
					'''
			} else 
				return '''
				await «connection».insertMany(«expression.target.name»);
								
				'''
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
