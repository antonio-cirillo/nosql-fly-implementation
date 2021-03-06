All'interno di `doGenerate`.
```python
allReqs.add("pymongo")
allReqs.add("requests")
```
All'interno della funzione `generateBodyPy`.
```python
import requests
from pymongo import MongoClient
```
All'interno della funzione `generatePyExpression`.
```python
case "nosql": {
    if (exp.onCloud && (exp.environment.get(0).right as DeclarationObject).features.get(0).value_s.contains("aws")){
        var database = ((exp.right as DeclarationObject).features.get(2) as DeclarationFeature).value_s
        var collection = ((exp.right as DeclarationObject).features.get(3) as DeclarationFeature).value_s
        return '''
        «exp.name»Client = MongoClient('mongodb://«IF((exp.right as DeclarationObject).features.get(1).value_s.nullOrEmpty)
        »' + «((exp.right as DeclarationObject).features.get(1) as DeclarationFeature).value_f.name» + '«
        ELSE
        »«((exp.right as DeclarationObject).features.get(1) as DeclarationFeature).value_s»«
        ENDIF»')
        «exp.name»Database = «exp.name»Client['«database»']
        «exp.name» = «exp.name»Database['«collection»']
        
        '''
    } else if(exp.onCloud && (exp.environment.get(0).right as DeclarationObject).features.get(0).value_s.contains("azure")){
        var resourceGroup = ((exp.right as DeclarationObject).features.get(1) as DeclarationFeature).value_s
        var instance = ((exp.right as DeclarationObject).features.get(2) as DeclarationFeature).value_s
        var database = ((exp.right as DeclarationObject).features.get(3) as DeclarationFeature).value_s
        var collection = ((exp.right as DeclarationObject).features.get(4) as DeclarationFeature).value_s 
        return '''
        auth_url = 'https://login.microsoftonline.com/' + '"${tenant}"' + '/oauth2/v2.0/token'
        scope = 'https://management.azure.com/.default'
        
        headers = {'Content-Type': 'application/x-www-form-urlencoded'}
        url = auth_url
        data = { 'client_id': '"${user}"', 'scope': scope, 'client_secret': '"${secret}"', 'grant_type': 'client_credentials' }
        r = requests.post(url=url, data=data, headers=headers)
        
        get_token = r.json()
        
        access_token = get_token['access_token']
        header_token = {'Authorization': 'Bearer {}'.format(access_token)}
        
        __url_ = 'https://management.azure.com/subscriptions/' + '"${subscription}"' + '/resourceGroups/«resourceGroup»/providers/Microsoft.DocumentDB/databaseAccounts/«instance»/listConnectionStrings?api-version=2021-03-01-preview'
        r = requests.post(url =__url_, headers=header_token)
        r = r.json()
        
        «exp.name»Client = MongoClient(r['connectionStrings'][0]['connectionString'])
        «exp.name»Database = «exp.name»Client['«database»']
        «exp.name» = «exp.name»Database['«collection»']
        '''
    } else {
        var database = ((exp.right as DeclarationObject).features.get(2) as DeclarationFeature).value_s
        var collection = ((exp.right as DeclarationObject).features.get(3) as DeclarationFeature).value_s
        return '''
        «exp.name»Client = MongoClient('«IF((exp.right as DeclarationObject).features.get(1).value_s.nullOrEmpty)
        »' + «((exp.right as DeclarationObject).features.get(1) as DeclarationFeature).value_f.name» + '«
        ELSE
        »«((exp.right as DeclarationObject).features.get(1) as DeclarationFeature).value_s»«
        ENDIF»')
        «exp.name»Database = «exp.name»Client['«database»']
        «exp.name» = «exp.name»Database['«collection»']
        
        '''
    }
} case "query":{
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
                »'«((exp.right as DeclarationObject).features.get(3) as DeclarationFeature).value_s»'«
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
                    «exp.name» = ''
                    if «((exp.right as DeclarationObject).features.get(3) as DeclarationFeature).value_f.name»[0] == '[' :
                        «exp.name» = json.loads(«((exp.right as DeclarationObject).features.get(3) as DeclarationFeature).value_f.name»)
                    else:
                        «exp.name» = json.loads('[' + «((exp.right as DeclarationObject).features.get(3) as DeclarationFeature).value_f.name» + ']')
                    
                    '''
                } 
            } else {
                return '''
                «exp.name» = ''
                if '«((exp.right as DeclarationObject).features.get(3) as DeclarationFeature).value_s»'[0] == '[' :
                    «exp.name» = json.loads('«((exp.right as DeclarationObject).features.get(3) as DeclarationFeature).value_s»')
                else:
                    «exp.name» = json.loads('[' + '«((exp.right as DeclarationObject).features.get(3) as DeclarationFeature).value_s»' + ']'
                    
                '''
            }
        } else {
            if((exp.right as DeclarationObject).features.size() == 4) {
                return '''
                «exp.name» = json.loads(«IF
                ((exp.right as DeclarationObject).features.get(3).value_s.nullOrEmpty)»«(exp.right as DeclarationObject).features.get(3).value_f.name»«
                ELSE»'«(exp.right as DeclarationObject).features.get(3).value_s»'«ENDIF»)
                
                '''
            } else {
                return '''
                «exp.name»Filter = json.loads(«IF
                ((exp.right as DeclarationObject).features.get(3).value_s.nullOrEmpty)»«(exp.right as DeclarationObject).features.get(3).value_f.name»«
                ELSE»'«(exp.right as DeclarationObject).features.get(3).value_s»'«ENDIF»)
                
                «exp.name» = json.loads(«IF
                ((exp.right as DeclarationObject).features.get(4).value_s.nullOrEmpty)»«(exp.right as DeclarationObject).features.get(4).value_f.name»«
                ELSE»'«(exp.right as DeclarationObject).features.get(4).value_s»'«ENDIF»)
                
                '''
            }
        }
    }							
} case "distributed-query":{
    var query_type = ((exp.right as DeclarationObject).features.get(1) as DeclarationFeature).value_s
    if(query_type.equals("insert")) {
        if(((exp.right as DeclarationObject).features.get(2) as DeclarationFeature).value_s.nullOrEmpty) {
            if((((exp.right as DeclarationObject).features.get(2) as DeclarationFeature).value_f as VariableDeclaration).right instanceof DeclarationObject) {
                var variables = (((exp.right as DeclarationObject).features.get(2) as DeclarationFeature).value_f as VariableDeclaration).right as DeclarationObject
                if(variables.features.get(0).value_s.equals("file")) {
                    return '''
                    «exp.name» = pd.read_csv(«IF
                    (variables.features.get(1).value_s.nullOrEmpty)»«variables.features.get(1).value_f.name»«
                    ELSE»"«variables.features.get(1).value_s»"«ENDIF»).to_dict('records')
                    
                    '''
                }
            } else {
                return '''
                «exp.name» = ''
                if «((exp.right as DeclarationObject).features.get(2) as DeclarationFeature).value_f.name»[0] == '[' :
                    «exp.name» = json.loads(«((exp.right as DeclarationObject).features.get(2) as DeclarationFeature).value_f.name»)
                else:
                    «exp.name» = json.loads('[' + «((exp.right as DeclarationObject).features.get(2) as DeclarationFeature).value_f.name» + ']')
                
                '''
            } 
        } else {
            return '''
            «exp.name» = ''
            if '«((exp.right as DeclarationObject).features.get(2) as DeclarationFeature).value_s»'[0] == '[' :
                «exp.name» = json.loads('«((exp.right as DeclarationObject).features.get(2) as DeclarationFeature).value_s»')
            else:
                «exp.name» = json.loads('[' + '«((exp.right as DeclarationObject).features.get(2) as DeclarationFeature).value_s»' + ']')
                
            '''
        }
    } else if(query_type.equals("delete")) {
        var ret = ''''''
        ret += '''
        «exp.name»Delete = json.loads(«IF
        ((exp.right as DeclarationObject).features.get(2).value_s.nullOrEmpty)»«(exp.right as DeclarationObject).features.get(2).value_f.name»«
        ELSE»'«(exp.right as DeclarationObject).features.get(2).value_s»'«ENDIF»)
        
        def __«exp.name»__():
            delete_count = 0
        '''
        
        for(i : 3 ..< (exp.right as DeclarationObject).features.size)	
            ret += '''	delete_count += «((exp.right as DeclarationObject).features.get(i) as DeclarationFeature).value_f.name».delete_many(«exp.name»Delete).deleted_count
            '''
        ret +='''

        '''
                                            
        ret += '''	return delete_count
        '''
            
        return ret								
    } else if(query_type.equals("select")) {
        typeSystem.get(scope).put(exp.name, "List <Table>")
        var ret = ''''''
        ret += '''
        def __«exp.name»__():
            sources = []
        '''
        for(i : 3 ..< (exp.right as DeclarationObject).features.size)	
            ret += '''	sources.append(«((exp.right as DeclarationObject).features.get(i) as DeclarationFeature).value_f.name»)
            '''
         
        ret +=
        '''
        #
            features = []
            objects = []
            
            for source in sources:
                result = source.find(json.loads(«IF((exp.right as DeclarationObject).features.get(2).value_s.nullOrEmpty)
                    »«((exp.right as DeclarationObject).features.get(2) as DeclarationFeature).value_f.name»«
                    ELSE
                    »'«((exp.right as DeclarationObject).features.get(2) as DeclarationFeature).value_s»'«
                    ENDIF»))
                    
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
        return ret
    } else {
        return '''
        «exp.name»Filter = json.loads(«IF
        ((exp.right as DeclarationObject).features.get(2).value_s.nullOrEmpty)»«(exp.right as DeclarationObject).features.get(2).value_f.name»«
        ELSE»'«(exp.right as DeclarationObject).features.get(2).value_s»'«ENDIF»)
        
        «exp.name» = json.loads(«IF
        ((exp.right as DeclarationObject).features.get(3).value_s.nullOrEmpty)»«(exp.right as DeclarationObject).features.get(3).value_f.name»«
        ELSE»'«(exp.right as DeclarationObject).features.get(3).value_s»'«ENDIF»)
        
        '''
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
} case "distributed-query": {
    var queryType = (expression.target.right as DeclarationObject).features.get(1).value_s
    if(expression.feature.equals("execute")) {
        if(queryType.equals("insert")) {
            var ret = ''''''
            for(i : 3 ..< (expression.target.right as DeclarationObject).features.size)	
                ret += '''
                «((expression.target.right as DeclarationObject).features.get(i) as DeclarationFeature).value_f.name».insert_many(«expression.target.name»)
                '''
            ret += '''

            '''
            return ret
        } else if(queryType.equals("delete")) {
            return '''
            __«expression.target.name»__()
            
            '''
        } else if(queryType.equals("select")) {
            return '''
            __«expression.target.name»__()
            
            '''
        } else if(queryType.equals("update")) {
            var ret = ''''''
            for(i : 4 ..< (expression.target.right as DeclarationObject).features.size)	
                ret += '''
                «((expression.target.right as DeclarationObject).features.get(i) as DeclarationFeature).value_f.name».update_many(«expression.target.name»Filter, «expression.target.name»)
                '''
            ret += '''

            '''
            return ret
        } else if(queryType.equals("replace")) {
            var ret = ''''''
            for(i : 4 ..< (expression.target.right as DeclarationObject).features.size)	
                ret += '''
                «((expression.target.right as DeclarationObject).features.get(i) as DeclarationFeature).value_f.name».replace_one(«expression.target.name»Filter, «expression.target.name»)
                '''
            ret += '''

            '''
            return ret
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
