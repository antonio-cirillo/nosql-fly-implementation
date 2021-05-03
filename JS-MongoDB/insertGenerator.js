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
	var pathOfCSV = "/home/devcirillo/Documenti/GitHub/nosql-fly-implementation/FLY-MongoDB/weatherHistory.csv"
	/* var weatherCSVClient = containerClient.getBlobClient(pathOfCSV);
	var weatherCSV = await weatherCSVClient.download(); */ // -> Fly code gen.

	const insertWeatherCSV = async () => {
		
		let i = 0;
		let features;
		let objects = [];
		
		await new Promise((resolve) => {
		
			__fs.createReadStream(pathOfCSV)
			.pipe(__parse())
			.on("data", (row) => {
				if(i == 0) {
					features = row;
					++i;
				} else if(i >= 1 && i <= 10) {
					let object = { };
					for([index, value] of features.entries())
						object[features[index]] = row[index];
					objects.push(object);
					++i;
				} else if(i < 1)
					++i;
			})
			.on("end", () => {
				resolve();
			});
		});
		
		return objects;
	}
	
	await nosql.insertMany((await insertWeatherCSV())); // TEST PASSED! -> Insert from csv.

	var statement = "{ \"name\": \"Antonio\", \"surname\": \"Cirillo\", \"age\": 21 }"
	let insertStudent;
	if(statement.charAt(0) === "[")
		insertStudent = JSON.parse(statement);
	else
		insertStudent = JSON.parse("[" + statement + "]");
	
	await nosql.insertMany(insertStudent); // TEST PASSED! -> Insert single object.

	var statement = "[ " + "{ \"name\": \"Antonio\", \"surname\": \"Cirillo\", \"age\": 21 }" + ", " + "{  \"name\": \"Giovanni\", \"surname\": \"Rapa\", \"age\": 21 }" + " ]"
	let insertStudents;
	if(statement.charAt(0) === "[")
		insertStudents = JSON.parse(statement);
	else
		insertStudents = JSON.parse("[" + statement + "]");
	
	await nosql.insertMany(insertStudents); // TEST PASSED ! -> Insert multi objects.

	await __nosqlClient.close(); 

}

func(); 