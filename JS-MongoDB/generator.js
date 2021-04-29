var __dataframe = require("dataframe-js").DataFrame;
var __nosql = require("mongodb");

const func = async () => {

    const string = "{ }";
    const event = JSON.parse(string);
    console.log(`Event: ${string}`);

	const __nosqlClient = new __nosql.MongoClient(
		"mongodb://127.0.0.1:27017/",
		{ useUnifiedTopology: true }
	);
	
	await __nosqlClient.connect();
	
	const nosql = __nosqlClient.db("mydb").collection("weather");
	const query = JSON.parse("{ }");
	
	var result = await nosql.find(query);
	await __nosqlClient.close();
}

func();