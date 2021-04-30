var __dataframe = require("dataframe-js").DataFrame;
var __nosql = require("mongodb");
var __fs = require("fs")
var __parse = require("csv-parse");

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

	/* const result = async () => {

		const features = [];
		const objects = [];

		const ___result = nosql.find(JSON.parse("{ }"));
		
		await ___result.forEach((obj) => {
			objects.push(obj);
			for(const f in obj)
				features.push(f);
		});

		return await new __dataframe(
			objects, 
			features
		);
		
	} */

	const result = async () => {

		let features;
		const objects = [];
		let i = 0;

		await new Promise((resolve) => {
			__fs.createReadStream("C:\\Users\\devci\\Documents\\GitHub\\nosql-fly-implementation\\FLY-MongoDB\\weatherHistory.csv")
			.pipe(__parse())
			.on('data', (row) => {
				if(i == 0) {
					features = row;
					++i;
				}					
				else if(i >= 1 && i <= 5) {
					console.log(row);
					objects.push(row);
					++i;
				}
			})
			.on('end', () => {
				resolve();
			});
		});
		
		return await new __dataframe(
			objects, 
			features
		);

	}
	
	(await result()).show();

	await __nosqlClient.close();
  }

func(); 