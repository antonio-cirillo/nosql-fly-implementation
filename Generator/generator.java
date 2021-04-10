private static List <Table> __generateTableFromNoSQLQuery(MongoCursor <Document> ___mongoCursor) {
			List <Table> ___resultGenerateTableFromNoSQLQuery = new ArrayList <> ();
			
			if(!___mongoCursor.hasNext()) 
				return ___resultGenerateTableFromNoSQLQuery;
						
			org.json.JSONObject ___jsonObject;
			ArrayList <ArrayList <String>> ___featuresOfMongoCursorResult = new ArrayList <> ();
			ArrayList <org.json.JSONArray> ___jsonObjectOfMongoCursorResult = new ArrayList <> ();
										
			while(___mongoCursor.hasNext()) {
				___jsonObject = new org.json.JSONObject(___mongoCursor.next().toJson());
				Iterator <String> ___iteratorJsonObjectKeys = ___jsonObject.keys(); 
							
				ArrayList <String> ___tmpFeatureOfJsonObject = new ArrayList <String> ();
				while(___iteratorJsonObjectKeys.hasNext())
					___tmpFeatureOfJsonObject.add(___iteratorJsonObjectKeys.next());
								
				boolean ___structureIsEquals = false;
				int ___indexGenerateTableFromNoSQLQuery = 0;
				for(; ___indexGenerateTableFromNoSQLQuery < ___featuresOfMongoCursorResult.size();
					++___indexGenerateTableFromNoSQLQuery) {
					if(___featuresOfMongoCursorResult.get(___indexGenerateTableFromNoSQLQuery).equals(___tmpFeatureOfJsonObject)) {
						___structureIsEquals = true;
						break;
					}
				}
				
				if(!___structureIsEquals) {
					___featuresOfMongoCursorResult.add(___tmpFeatureOfJsonObject);
					org.json.JSONArray ___tmpJsonArrayToAdd = new org.json.JSONArray();
					___tmpJsonArrayToAdd.put(___jsonObject);
					___jsonObjectOfMongoCursorResult.add(___tmpJsonArrayToAdd);
				} else {
					org.json.JSONArray ___tmpJsonArrayToAdd = 
					___jsonObjectOfMongoCursorResult.remove(___indexGenerateTableFromNoSQLQuery);
					___tmpJsonArrayToAdd.put(___jsonObject);
					___jsonObjectOfMongoCursorResult.add(___indexGenerateTableFromNoSQLQuery, ___tmpJsonArrayToAdd);
				}
				
			}
			
			for(int ___indexForCreatingTable = 0; ___indexForCreatingTable < ___featuresOfMongoCursorResult.size();
					++___indexForCreatingTable) {
				Table ___tableToReturn = Table.create();
				ArrayList <String> ___tmpFeaturesForColumns = ___featuresOfMongoCursorResult.get(___indexForCreatingTable);
				org.json.JSONArray ___tmpJsonArrayForThisTable = ___jsonObjectOfMongoCursorResult.get(___indexForCreatingTable);
				for(int ___indexForReadingFeatures = 0; 
						___indexForReadingFeatures < ___tmpFeaturesForColumns.size(); ___indexForReadingFeatures++) {
					String ___feature = ___tmpFeaturesForColumns.get(___indexForReadingFeatures);
					ArrayList <String> ___columnToAdd = new ArrayList <> ();
					boolean ___flag = true;
					Table ___extractTable = null;					
					for(int ___indexForReadingObject = 0; 
							___indexForReadingObject < ___tmpJsonArrayForThisTable.length(); ___indexForReadingObject++) {
						org.json.JSONObject ___tmpJsonForThisTable =
								___tmpJsonArrayForThisTable.getJSONObject(___indexForReadingObject);
						Object ___objectExtractFromQuery = ___tmpJsonForThisTable.get(___feature);
						if(___objectExtractFromQuery instanceof String) {
							___flag = true;
							___columnToAdd.add((String) ___objectExtractFromQuery);
						} else if(___objectExtractFromQuery instanceof Integer) { 
							___flag = true;
							___columnToAdd.add(___objectExtractFromQuery + "");
						} else if(___objectExtractFromQuery instanceof org.json.JSONObject) {
							if(___feature.equals("_id")) {
								___flag = true;
								___columnToAdd.add(((org.json.JSONObject) ___objectExtractFromQuery).getString("$oid"));
							} else {	
								___flag = false;
								Iterator <String> ___subIteratorFeatures = ((org.json.JSONObject) ___objectExtractFromQuery).keys();
								List <String> ___subFeatures = new ArrayList <> ();
								while(___subIteratorFeatures.hasNext())
									___subFeatures.add(___subIteratorFeatures.next());
								if(___extractTable == null)
									___extractTable = ___extractTableFromObject(
										___feature, ___subFeatures, (org.json.JSONObject) ___objectExtractFromQuery);
								else 
									___extractTable.append(___extractTableFromObject(
										___feature, ___subFeatures, (org.json.JSONObject) ___objectExtractFromQuery));
							}
						}
					}
					if(___flag)
						___tableToReturn.addColumns(
								StringColumn.create(
										___feature, ___columnToAdd));
					else
						for(Column c : ___extractTable.columns())
							___tableToReturn.addColumns(c);
				}
				___resultGenerateTableFromNoSQLQuery.add(___tableToReturn);
			}
			
			return 	___resultGenerateTableFromNoSQLQuery;
		
		}
		
		private static Table ___extractTableFromObject(
				String ___feature, List <String> ___features, org.json.JSONObject ___jsonObject) {
			Table ___tableToReturn = Table.create();		
			
			for(String ___f : ___features) {
				Object ___objectExtractFromQuery = ___jsonObject.get(___f);
				if(___objectExtractFromQuery instanceof String)
					___tableToReturn.addColumns(
						StringColumn.create(___feature + "_" + ___f, (String) ___objectExtractFromQuery));
				else if(___objectExtractFromQuery instanceof Integer)
					___tableToReturn.addColumns(
							StringColumn.create(___feature + "_" + ___f, "" + ___objectExtractFromQuery));
				else if(___objectExtractFromQuery instanceof org.json.JSONObject) {
					Iterator <String> ___subIteratorFeatures = ((org.json.JSONObject) ___objectExtractFromQuery).keys();
					List <String> ___subFeatures = new ArrayList <> ();
					while(___subIteratorFeatures.hasNext())
						___subFeatures.add(___subIteratorFeatures.next());
					for(Column c : ___extractTableFromObject(
							___feature + "_" + ___f, ___subFeatures, (org.json.JSONObject) ___objectExtractFromQuery).columns())
						___tableToReturn.addColumns(c);
				} else if(___objectExtractFromQuery instanceof org.json.JSONArray) {
					for(int ___indexJsonArray = 0; ___indexJsonArray < ((org.json.JSONArray) ___objectExtractFromQuery).length() ; ++___indexJsonArray) {
						Object ___tmpObjectAtIndex = ((org.json.JSONArray) ___objectExtractFromQuery).get(___indexJsonArray);
						if(___tmpObjectAtIndex instanceof org.json.JSONObject) {
							Iterator <String> ___subIteratorFeatures = ((org.json.JSONObject) ___tmpObjectAtIndex).keys();
							List <String> ___subFeatures = new ArrayList <> ();
							while(___subIteratorFeatures.hasNext())
								___subFeatures.add(___subIteratorFeatures.next());
							for(Column c :___extractTableFromObject(
										___feature + "_" + ___f + "_" + ___indexJsonArray,___subFeatures,
										((org.json.JSONArray) ___objectExtractFromQuery).getJSONObject(___indexJsonArray)).columns())
								___tableToReturn.addColumns(c);
						} else 
							___tableToReturn.addColumns(StringColumn.create(___feature + "_" + ___f + "_" + ___indexJsonArray, "" + ___tmpObjectAtIndex));
							
					}
				} 
			}
						
			return ___tableToReturn;
		}
