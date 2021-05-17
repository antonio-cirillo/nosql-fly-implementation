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

def main():
	#__environment = 'azure'
	#__queue_service = QueueServiceClient(account_url='https://"${storageName}".queue.core.windows.net', credential='"${storageKey}"')
	#__event = req.get_json()
	#data = __event['data']
	nosqlClient = MongoClient('mongodb://127.0.0.1:27017/') 
	nosqlDatabase = nosqlClient['mydb']
	nosql = nosqlDatabase['weather']
	statement =  '{ }' 
	def __selectQuery__():
	    result = nosql.find(json.loads(statement))
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
	
	result = __selectQuery__()
	
	for table in result:
		print(table)
	#__queue_service_client = __queue_service.get_queue_client('termination-"${function}"-"${id}"')
	#__queue_service_client.send_message('terminate')

main()