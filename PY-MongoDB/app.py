from pymongo import MongoClient
import json

client = MongoClient("mongodb://127.0.0.1:27017/")

db = client['mydb']

collection = db['weather']

studentsString = "{\"name\": \"Antonio\", \"surname\": \"Cirillo\", \"age\": 21, \"address\": { \"city\": \"Battipaglia\", \"zip\": 84091, \"street\": \"Via Paolo Baratta\" } }"

if studentsString[0] != "[":
    studentsString = "[ " + studentsString + " ]"
else :
    print()

y = json.loads(studentsString)

print(studentsString)


'''
students = [{
    "name": 'Antonio', 
    "surname": 'Cirillo',
    "age": 21, 
    "address": {
        "city": 'Battipaglia',
        "zip": 84091,
        "street": 'Via Paolo Baratta'
    },
    "tel": ['333-xxxxxx']
}]

collection.insert_many(students)

collection.update_many({ "name": 'Antonio' }, { "$set": { "address.city": 'Salerno' } })

for post in collection.find({}):
    print(post)

collection.replace_one({ "name" : 'Antonio' }, { "replaced": 'true' })

for post in collection.find({}):
    print(post)

collection.delete_many({})
'''