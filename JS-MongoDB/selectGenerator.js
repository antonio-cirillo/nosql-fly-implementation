var __dataframe = require("dataframe-js").DataFrame;
var __nosql = require("mongodb");
var __fs = require("fs")
var __parse = require("csv-parse");

const func = async () => {

	const __nosqlClient = new __nosql.MongoClient(
		"mongodb://127.0.0.1:27017/",
		{ useUnifiedTopology: true }
	);
	
	await __nosqlClient.connect();
	
	const nosql = __nosqlClient.db("mydb").collection("weather");

    var statement = "{ }"
	const select = async () => {
	
		let features = [];
		let objects = [];
		
		await nosql.find(JSON.parse(statement)).forEach((object) => {
			
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
	
	var result = (await select()); // => TEST PASSED! -> Retrive list of dataframe from select operation.

    for(const table of result) {
        table.show();
        console.log();
    }

    await __nosqlClient.close(); 
    
}

func(); 