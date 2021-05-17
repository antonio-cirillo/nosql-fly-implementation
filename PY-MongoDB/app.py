from numpy import array
from pymongo import MongoClient
import json
import pandas as pd
from pymongo.results import InsertManyResult

client = MongoClient("mongodb://127.0.0.1:27017/")

db = client['mydb']

collection = db['weather']

'''
studentsString = "{\"name\": \"Antonio\", \"surname\": \"Cirillo\", \"age\": 21, \"address\": { \"city\": \"Battipaglia\", \"zip\": 84091, \"street\": \"Via Paolo Baratta\" } }"

if studentsString[0] != "[":
    studentsString = "[ " + studentsString + " ]"
else :
    print()

y = json.loads(studentsString)
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

'''
collection.update_many({ "name": 'Antonio' }, { "$set": { "address.city": 'Salerno' } })

for post in collection.find({}):
    print(post)

collection.replace_one({ "name" : 'Antonio' }, { "replaced": 'true' })

for post in collection.find({}):
    print(post)

collection.delete_many({})
'''

file = pd.read_csv("C:\\Users\\devci\\Documents\\GitHub\\nosql-fly-implementation\\FLY-MongoDB\\weatherHistory.csv", skiprows = range(1,4), nrows = 2)

collection.insert_many(file.to_dict('records'))

def __select__():
    result = collection.find({})
    features = []
    objects = []
    for value in result:
        feature = value.keys()
        i = 0
        for f in features:
            if f == feature:
                break
            else:
                i = i + 1 
        if i + 1 > len(features):
            features.append(feature)
            objects.append([])
            objects[len(objects) - 1].append(value)
        else:
            objects[i].append(value)

    df = []
    for i in range(len(features)):
        df.append(pd.DataFrame(objects[i]))

    return df

df = __select__()

for value in df:
    print(value)

collection.delete_many({})