All'interno della funzione `generateBodyPy`.
```python
from pymongo import MongoClient
```
All'interno della funzione `generatePyExpression`.
```python
case "nosql": {
	var client = ((exp.right as DeclarationObject).features.get(1) as DeclarationFeature).value_s
	var database = ((exp.right as DeclarationObject).features.get(2) as DeclarationFeature).value_s
	var collection = ((exp.right as DeclarationObject).features.get(3) as DeclarationFeature).value_s
	return '''
	«exp.name»Client = MongoClient('«client»') 
	«exp.name»Database = «exp.name»Client['«database»']
	«exp.name» = «exp.name»Database['«collection»']
	'''
}

case "query":{
	var connection = ((exp.right as DeclarationObject).features.get(2) as DeclarationFeature).value_f.name
	var con = (((exp.right as DeclarationObject).features.get(2) as DeclarationFeature).value_f as VariableDeclaration)
	var databaseType = ((con as VariableDeclaration).right as DeclarationObject).features.get(0).value_s
	if(databaseType.equals("sql")) {
		return '''
		«exp.name» = __cursor«connection».execute(
		«IF 
			((exp.right as DeclarationObject).features.get(3) as DeclarationFeature).value_s.nullOrEmpty
		»
			«((exp.right as DeclarationObject).features.get(3) as DeclarationFeature).value_f.name»
		« ELSE » 
			'«((exp.right as DeclarationObject).features.get(3) as DeclarationFeature).value_s»'
		«ENDIF»
		)
		'''
	} else if(databaseType.equals("nosql")) {
		var query_type = ((exp.right as DeclarationObject).features.get(1) as DeclarationFeature).value_s
		var collection = (exp.right as DeclarationObject).features.get(2).value_f.name						
		if(query_type.equals("select")) {
			typeSystem.get(scope).put(exp.name, "List <Table>")
			return '''
			def __«exp.name»__():
				result = «collection».find(json.loads(«IF((exp.right as DeclarationObject).features.get(3).value_s.nullOrEmpty)
				»«((exp.right as DeclarationObject).features.get(3) as DeclarationFeature).value_f.name»«
			 	ELSE
			 	»"«((exp.right as DeclarationObject).features.get(3) as DeclarationFeature).value_s»"«
			 	ENDIF»))
			 	features = []
			 	objects = []
			 	for value in result:
			 		feature = value.keys()
					i = 0
					for f in features:
				 		if f == feature:
							break
						else:
				 			i = i + 1 
					if i + 1 > len(features):
			  		features.append(feature)
						objects.append([])
						objects[len(objects) - 1].append(value)
					else:
				  	objects[i].append(value)
									
				df = []
				for i in range(len(features)):
					df.append(pd.DataFrame(objects[i]))
									
				return df
									
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
							«exp.name» = pd.read_csv(«IF
							(variables.features.get(1).value_s.nullOrEmpty)»«variables.features.get(1).value_f.name»«
							ELSE»"«variables.features.get(1).value_s»"«ENDIF», skiprows = range(1, «from»), nrows = («to» - «from»)).to_dict('records')
							
							'''
						} else {
							return '''
							«exp.name» = pd.read_csv(«IF
							(variables.features.get(1).value_s.nullOrEmpty)»«variables.features.get(1).value_f.name»«
							ELSE»"«variables.features.get(1).value_s»"«ENDIF»).to_dict('records')
							
							'''
						}
					}
				} else {
					return '''
					«exp.name» = ""
					if «((exp.right as DeclarationObject).features.get(3) as DeclarationFeature).value_f.name»[0] == "[" :
						«exp.name» = json.loads(«((exp.right as DeclarationObject).features.get(3) as DeclarationFeature).value_f.name»)
					else:
						«exp.name» = json.loads("[" + «((exp.right as DeclarationObject).features.get(3) as DeclarationFeature).value_f.name» + "]")
											
					'''
				} 
			} else {
				return '''
				«exp.name» = """
				if "«((exp.right as DeclarationObject).features.get(3) as DeclarationFeature).value_s»"[0] == "[" :
					«exp.name» = json.loads("«((exp.right as DeclarationObject).features.get(3) as DeclarationFeature).value_s»")
				else:
					«exp.name» = json.loads("[" + "«((exp.right as DeclarationObject).features.get(3) as DeclarationFeature).value_s»" + "]")
								
				'''
			}
		} else {
			if((exp.right as DeclarationObject).features.size() == 4) {
				return '''
				«exp.name» = json.loads(«IF
				((exp.right as DeclarationObject).features.get(3).value_s.nullOrEmpty)»«(exp.right as DeclarationObject).features.get(3).value_f.name»«
				ELSE»"«(exp.right as DeclarationObject).features.get(3).value_s»"«ENDIF»)
				
				'''
			} else {
				return '''
				«exp.name»Filter = json.loads(«IF
				((exp.right as DeclarationObject).features.get(3).value_s.nullOrEmpty)»«(exp.right as DeclarationObject).features.get(3).value_f.name»«
				ELSE»"«(exp.right as DeclarationObject).features.get(3).value_s»"«ENDIF»)

				«exp.name» = json.loads(«IF
				((exp.right as DeclarationObject).features.get(4).value_s.nullOrEmpty)»«(exp.right as DeclarationObject).features.get(4).value_f.name»«
				ELSE»"«(exp.right as DeclarationObject).features.get(4).value_s»"«ENDIF»)

				'''
			}
		}
	}
}
```
All'interno della funzione `generatePyVariableFunction`.
```python
case "query":{
	var queryType = (expression.target.right as DeclarationObject).features.get(1).value_s
	var connection = (expression.target.right as DeclarationObject).features.get(2).value_f.name
	var databaseType = ((((expression.target.right as DeclarationObject).features.get(2) as DeclarationFeature)
		.value_f as VariableDeclaration).right as DeclarationObject).features.get(0).value_s
	if(databaseType.equals("sql")) {
		if(expression.feature.equals("execute")) {
			if(queryType.equals("value")) {
				return '''
					__cursor«connection».fetchone()[0]
				''' 
			} else {
				return '''
				«connection».commit()
				''' 
			}
		}
	} else if(databaseType.equals("nosql")) {
		if(queryType.equals("insert")) {
			return '''
			«connection».insert_many(«expression.target.name»)

			'''
		} else if(queryType.equals("select")) {
			return '''
			__«expression.target.name»__()

			'''
		} else if(queryType.equals("delete")) {
			return '''
			«connection».delete_many(«expression.target.name»).deleted_count

			'''
		} else if(queryType.equals("update")) {
			return '''
			«connection».update_many(«expression.target.name»Filter, «expression.target.name»)

			'''
		} else if(queryType.equals("replace")) {
			return '''
			«connection».replace_one(«expression.target.name»Filter, «expression.target.name»)

			'''
		}
	}
} 
```
All'interno della funzione `generatePyForExpression`.
```python
else if(typeSystem.get(scope).get((exp.object as VariableLiteral).variable.name).equals("List <Table>")) {
	typeSystem.get(scope).put((exp.index.indices.get(0) as VariableDeclaration).name, "Table");
	return '''
	for «(exp.index.indices.get(0) as VariableDeclaration).name» in «(exp.object as VariableLiteral).variable.name»:
		«IF exp.body instanceof BlockExpression»
		«FOR e: (exp.body as BlockExpression).expressions»
		«generatePyExpression(e,scope, local)»
		«ENDFOR»
		«ELSE»
		«generatePyExpression(exp.body,scope, local)»
		«ENDIF»
	'''
}
```
