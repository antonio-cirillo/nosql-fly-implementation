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
	deleteQuery = json.loads("{ }")
	
	result = nosql.delete_many(deleteQuery).deleted_count
	
	if result > 0:
		print( 'One or more item(s) was eliminated' )
	#__queue_service_client = __queue_service.get_queue_client('termination-"${function}"-"${id}"')
	#__queue_service_client.send_message('terminate')

main()