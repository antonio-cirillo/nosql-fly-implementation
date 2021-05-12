All'interno della funzione `generatePyExpression`.
```python
case "nosql": {
	var client = ((exp.right as DeclarationObject).features.get(1) as DeclarationFeature)
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
							'''
						} else {
							return '''
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
