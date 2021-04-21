var __nosql = require("mongodb");

const uri = "mongodb://127.0.0.1:27017/";

const __client = new __nosql.MongoClient(
    uri,
    { useUnifiedTopology: true }
);

async function test() {
    
    await __client.connect();

    var __collection = __client.db("mydb").collection("weather");

    console.log(await __collection.countDocuments());

    await __client.close();

}

test();