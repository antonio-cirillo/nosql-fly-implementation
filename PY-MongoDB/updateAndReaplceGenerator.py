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
	updateFilter =  '{ \"name\": \"Antonio\" }' 
	updateStatement =  '{ \"$set\": { \"name\": \"Marcello\" } }' 
	updateQueryFilter = json.loads(updateFilter)
	
	updateQuery = json.loads(updateStatement)
	
	nosql.update_many(updateQueryFilter, updateQuery)
	
	replaceFilter =  '{ \"name\": \"Marcello\" }' 
	replaceStatement =  '{ \"name\": \"Antonio\", \"surname\": \"Montella\" }' 
	replaceQueryFilter = json.loads(replaceFilter)
	
	replaceQuery = json.loads(replaceStatement)
	
	nosql.replace_one(replaceQueryFilter, replaceQuery)

	#__queue_service_client = __queue_service.get_queue_client('termination-"${function}"-"${id}"')
	#__queue_service_client.send_message('terminate')
main()