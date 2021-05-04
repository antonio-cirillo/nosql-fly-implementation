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

    var statement = "[ " + "{ \"name\": \"Antonio\", \"surname\": \"Cirillo\", \"age\": 21 }" + ", " + "{  \"name\": \"Giovanni\", \"surname\": \"Rapa\", \"age\": 21 }" + " ]"
	let insertStudents;
	if(statement.charAt(0) === "[")
		insertStudents = JSON.parse(statement);
	else
		insertStudents = JSON.parse("[" + statement + "]");
	
	await nosql.insertMany(insertStudents); 

    var updateFilter = "{ \"name\": \"Antonio\" }"
	var updateStatement = "{ \"$set\": { \"name\": \"Marcello\" } }"
	const updateQueryFilter = JSON.parse(updateFilter);
	
	const updateQuery = JSON.parse(updateStatement);
	
	(await nosql.updateMany(updateQueryFilter, updateQuery));
	
	var replaceFilter = "{ \"name\": \"Marcello\" }"
	var replaceStatement = "{ \"name\": \"Antonio\", \"surname\": \"Montella\" }"
	const replaceQueryFilter = JSON.parse(replaceFilter);
	
	const replaceQuery = JSON.parse(replaceStatement);
	
	(await nosql.replaceOne(replaceQueryFilter, replaceQuery));
	
    await __nosqlClient.close(); 

}

func(); 