import pkg from 'mongodb';
const { MongoClient } = pkg;

const uri = "mongodb://127.0.0.1:27017/?directConnection=true&serverSelectionTimeoutMS=2000";

const client = new MongoClient(uri, { useUnifiedTopology: true });

client.connect();

const collection = client.db("mydb").collection("weather");

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
