#import os
#import random
#import time
#import math 
import pandas as pd
#import numpy as np
import json
#import urllib.request
#import pymysql
from pymongo import MongoClient

#import azure.functions as func
#from azure.storage.queue import QueueServiceClient

def main() :
	#__environment = 'azure'
	#__queue_service = QueueServiceClient(account_url='https://"${storageName}".queue.core.windows.net', credential='"${storageKey}"')
	#__event = req.get_json()
	#data = __event['data']
	nosqlClient = MongoClient('mongodb://127.0.0.1:27017/') 
	nosqlDatabase = nosqlClient['mydb']
	nosql = nosqlDatabase['weather']
	pathOfCSV =  '/home/devcirillo/Documenti/GitHub/nosql-fly-implementation/FLY-MongoDB/weatherHistory.csv' 
	#if 'http' in pathOfCSV:
	#	weatherCSV = urllib.request.urlopen(urllib.request.Request(pathOfCSV,headers={'Content-Type':'application/x-www-form-urlencoded;charset=utf-8'}))
	#else:
	#	weatherCSV = open(pathOfCSV,'rw')
	insertWeatherCSV = pd.read_csv(pathOfCSV, skiprows = range(1, 100), nrows = (155 - 100)).to_dict('records')
	
	nosql.insert_many(insertWeatherCSV)
	
	statement =  '[ '  +  '{ \"name\": \"Antonio\", \"surname\": \"Cirillo\", \"age\": 21 }'  +  ', '  +  '{  \"name\": \"Giovanni\", \"surname\": \"Rapa\", \"age\": 21 }'  +  ' ]' 
	insertStudents = ""
	if statement[0] == "[" :
		insertStudents = json.loads(statement)
	else :
		insertStudents = json.loads("[" + statement + "]")
	
	nosql.insert_many(insertStudents)
	
	#__queue_service_client = __queue_service.get_queue_client('termination-"${function}"-"${id}"')
	#__queue_service_client.send_message('terminate')
main()