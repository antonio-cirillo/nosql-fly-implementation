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
		if(query_type.equals("insert")) {
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
					else :
						«exp.name» = json.loads("[" + «((exp.right as DeclarationObject).features.get(3) as DeclarationFeature).value_f.name» + "]")
											
					'''
				} 
			} else {
				return '''
				«exp.name» = """
				if "«((exp.right as DeclarationObject).features.get(3) as DeclarationFeature).value_s»"[0] == "[" :
					«exp.name» = json.loads("«((exp.right as DeclarationObject).features.get(3) as DeclarationFeature).value_s»")
				else :
					«exp.name» = json.loads("[" + "«((exp.right as DeclarationObject).features.get(3) as DeclarationFeature).value_s»" + "]")
								
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
						if(expression.feature.equals("execute")){
							if (queryType.equals("value")){
								return '''
								__cursor«connection».fetchone()[0]
								''' 
							}else{
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
						}
					}
				} 
```
