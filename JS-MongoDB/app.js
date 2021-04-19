import pkg from 'mongodb';
const { MongoClient } = pkg;

import parse from 'csv-parse';
import fs from 'fs';

const uri = "mongodb://127.0.0.1:27017/?directConnection=true&serverSelectionTimeoutMS=2000";

const client = new MongoClient(uri, { useUnifiedTopology: true });

client.connect();

const collection = client.db("mydb").collection("weather");

const data = []
fs.createReadStream("C:\\Users\\devci\\Documents\\GitHub\\nosql-fly-implementation\\FLY-MongoDB\\weatherHistory.csv")
  .pipe(parse({ delimiter: ',' }))
  .on('data', (r) => {
    data.push(r);        
  })
  .on('end', () => {
    insertToDB(data);
  })

async function insertToDB(___data) {    
    const ___features = ___data[0];
    const ___objects = [];

    for(let ___i = 1; ___i < /*___data.length*/ 150; ++___i) {
        let ___object = { };
        for(const [___index, ___value] of ___data[___i].entries()) {
            ___object[___features[___index]] = ___value;
        }
        ___objects.push(___object);
    }

    await collection.insertMany(___objects);

    await collection.find({ }).forEach(console.dir);

    await collection.deleteMany({ });

    await client.close();

}

/*

async function crudOperation() {

    const students = [{
        name: 'Antonio', 
        surname: 'Cirillo',
        age: 21, 
        address: {
            city: 'Battipaglia',
            zip: 84091,
            street: 'Via Paolo Baratta'
        },
        tel: ['333-xxxxxx']
    }];

    await collection.insertMany(students);

    await collection.updateMany({ name: 'Antonio' }, { $set: { 'address.city': 'Salerno' } });

    await collection.find({ name: 'Antonio' }).forEach(console.dir);

    await collection.replaceOne({ name : 'Antonio' }, { replaced: 'true' });

    await collection.find({ }).forEach(console.dir);

    await collection.deleteMany({ });

    await client.close();

}

crudOperation();
*/
