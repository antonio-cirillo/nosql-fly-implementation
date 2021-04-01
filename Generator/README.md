### Inside compileJava function
```
import com.mongodb.client.*;
import org.bson.*;
```
### Inside generateVariableDeclaration function
```
case "nosql":{
        // var client = ((dec.right as DeclarationObject).features.get(1) as DeclarationFeature).value_s
	var database = ((dec.right as DeclarationObject).features.get(2) as DeclarationFeature).value_s
        var collection = ((dec.right as DeclarationObject).features.get(3) as DeclarationFeature).value_s
	return '''
	MongoClient «dec.name» = MongoClients.create();
	MongoDatabase «dec.name»_«database» = «dec.name».getDatabase("«database»");
	«IF !(collection.nullOrEmpty)»
	MongoCollection <Document> «collection» = «dec.name»_«database».getCollection(«collection»);
	«ENDIF»
	'''
}
``` 
