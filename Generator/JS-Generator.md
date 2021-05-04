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
		if(query_type.equals("select")) {
			typeSystem.get(scope).put(exp.name, "List <Table>")
			return '''
			const «exp.name» = async () => {
						
				let features = [];
				let objects = [];
										
				await «collection».find(JSON.parse(«IF((exp.right as DeclarationObject).features.get(3).value_s.nullOrEmpty)
				»«((exp.right as DeclarationObject).features.get(3) as DeclarationFeature).value_f.name»«
				ELSE
				»"«((exp.right as DeclarationObject).features.get(3) as DeclarationFeature).value_s»"«
				ENDIF»)).forEach((object) => {
											
				const keys = Object.keys(object);
				const n = features.length;
				let i;
											
				for(i = 0; i < n; ++i)
					if(!(JSON.stringify(features[i]) !== JSON.stringify(keys)))
							break;
											
				if(i === n) {
					features.push(keys);
					const __array = [];
					__array.push(object);
					objects.push(__array);
				} else
					objects[i].push(object);
											
				});
										
				let tables = [];
										
				for(i = 0; i < features.length; ++i)
					tables.push(new __dataframe(
						objects[i],
						features[i]
					));
											
				return tables;
			}
									
			'''
		} if(query_type.equals("insert")) {
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
			if((exp.right as DeclarationObject).features.size() == 4) {
			return '''
			const «exp.name» = JSON.parse(«IF
			((exp.right as DeclarationObject).features.get(3).value_s.nullOrEmpty)»«(exp.right as DeclarationObject).features.get(3).value_f.name»«
			ELSE»"«(exp.right as DeclarationObject).features.get(3).value_s»"«ENDIF»);
										
			'''
		} else 
			return '''
			const «exp.name»Filter = JSON.parse(«IF
			((exp.right as DeclarationObject).features.get(3).value_s.nullOrEmpty)»«(exp.right as DeclarationObject).features.get(3).value_f.name»«
			ELSE»"«(exp.right as DeclarationObject).features.get(3).value_s»"«ENDIF»);
								
			const «exp.name» = JSON.parse(«IF
			((exp.right as DeclarationObject).features.get(4).value_s.nullOrEmpty)»«(exp.right as DeclarationObject).features.get(4).value_f.name»«
			ELSE»"«(exp.right as DeclarationObject).features.get(4).value_s»"«ENDIF»);
										
			'''				
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
			} else if(queryType.equals("select")) {
				return '''
				(await «expression.target.name»());
					
				'''
			} else if(queryType.equals("delete")) {
				return '''
				(await «connection».deleteMany(«expression.target.name»)).deletedCount
					
				'''
			} else if(queryType.equals("update")) {
				return '''
				(await «connection».updateMany(«expression.target.name»Filter, «expression.target.name»));
								
				'''
			} else if(queryType.equals("replace")) {
				return '''
				(await «connection».replaceOne(«expression.target.name»Filter, «expression.target.name»));
				
				'''
			}
		}
	}
} 				
```
All'interno della funzione `generateJsForExpression`.
```node
else if(typeSystem.get(scope).get((exp.object as VariableLiteral).variable.name).equals("List <Table>")) {
	typeSystem.get(scope).put((exp.index.indices.get(0) as VariableDeclaration).name, "Table");
	return '''
	for(let «(exp.index.indices.get(0) as VariableDeclaration).name» of «(exp.object as VariableLiteral).variable.name») {
		«(exp.index.indices.get(0) as VariableDeclaration).name» = «(exp.index.indices.get(0) as VariableDeclaration).name».toArray();
		«IF exp.body instanceof BlockExpression»
			«FOR e: (exp.body as BlockExpression).expressions»
				«generateJsExpression(e,scope)»
			«ENDFOR»
		«ELSE»
			«generateJsExpression(exp.body,scope)»
		«ENDIF»
	}
		
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
