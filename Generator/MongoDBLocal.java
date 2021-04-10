import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.StandardOpenOption;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.time.LocalDate;
import tech.tablesaw.api.Table;
import tech.tablesaw.io.csv.CsvReadOptions;
import tech.tablesaw.io.csv.CsvWriteOptions;
import tech.tablesaw.columns.Column;
import tech.tablesaw.selection.Selection;
import tech.tablesaw.table.Rows;
import tech.tablesaw.api.Row;
import java.util.concurrent.LinkedTransferQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutionException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.Random;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.Scanner;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FileUtils;
import java.sql.*;
import com.mongodb.client.*;
import org.bson.*;
import java.io.FileReader;
import com.opencsv.CSVReader;
import java.util.Properties;
import org.apache.log4j.PropertyConfigurator;
import java.util.Iterator;
import tech.tablesaw.api.StringColumn;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
	
	public class MongoDBLocal {
		
		static HashMap<String,HashMap<String, Object>> __fly_environment = new HashMap<String,HashMap<String,Object>>();
		static HashMap<String,HashMap<String,Integer>> __fly_async_invocation_id = new HashMap<String,HashMap<String,Integer>>();
		static final String __environment = "smp";
		static long  __id_execution =  System.currentTimeMillis();
		
		public static void main(String[] args) throws Exception{
			
			try {
				Properties props = new Properties();
				props.put("log4j.rootLogger", "INFO, stdout");
				props.put("log4j.appender.stdout", "org.apache.log4j.ConsoleAppender");
				props.put("log4j.appender.stdout.Target", "System.out");
				props.put("log4j.appender.stdout.layout", "org.apache.log4j.PatternLayout");
				props.put("log4j.appender.stdout.layout.ConversionPattern", "%d{yy/MM/dd HH:mm:ss} %p %c{2}: %m%n");
				FileOutputStream outputStream = new FileOutputStream("log4j.properties");
				props.store(outputStream, "This is a default properties file");
				System.out.println("Default properties file created.");
			} catch (IOException e) {
				System.out.println("Default properties file not created.");
				e.printStackTrace();
			}
									
			String log4jConfPath = "log4j.properties";
			PropertyConfigurator.configure(log4jConfPath);
			
			MongoCollection <Document> nosql = MongoClients.create().getDatabase("mydb").getCollection("weather");
			
			String pathOfCSV = "/home/devcirillo/Documenti/GitHub/nosql-fly-implementation/FLY-MongoDB/weatherHistory.csv";
			
			File weatherCSV = new File(pathOfCSV);
			
			List <Document> insertWeatherCSV = new ArrayList <Document> ();
			try (CSVReader ___CSVReader = new CSVReader(new FileReader(weatherCSV))) {
				List <String[]> ___listOfArrayString = ___CSVReader.readAll();
				String[] ___nosqlfeatures = ___listOfArrayString.get(0);
				for(int ___indexOfReadingFromCSV = 0 + 1; ___indexOfReadingFromCSV < 150 + 1; ++___indexOfReadingFromCSV) {
					Document ___dcmnt_tmp = new Document();
					for(int ___indexOfReadingFromCSV2 = 0; ___indexOfReadingFromCSV2 < ___nosqlfeatures.length; ___indexOfReadingFromCSV2++) 
						___dcmnt_tmp.append(___nosqlfeatures[___indexOfReadingFromCSV2], ___listOfArrayString.get(___indexOfReadingFromCSV)[___indexOfReadingFromCSV2]); 
					insertWeatherCSV.add(___dcmnt_tmp);
				}
			}
			
			
			nosql.insertMany(insertWeatherCSV);
			String ___insertStudentStatement = "{'name': 'Antonio', 'surname': 'Cirillo', 'age': 21}";
			if(___insertStudentStatement.charAt(0) != '[')
				___insertStudentStatement = "[" + ___insertStudentStatement + "]";
													
			org.json.JSONArray ___insertStudentJsonArrayQuery = new org.json.JSONArray(___insertStudentStatement);
			List <Document> insertStudent = new ArrayList <Document> ();
			
			for(int ___indexForJsonArray = 0; ___indexForJsonArray < ___insertStudentJsonArrayQuery.length(); ___indexForJsonArray++) 
				insertStudent.add(Document.parse(___insertStudentJsonArrayQuery.get(___indexForJsonArray).toString()));
			
			
			nosql.insertMany(insertStudent);
			String ___insertStudentsStatement = "[ { 'name': 'Andrea', surname': 'Di Pierno', 'age': 21 }, { 'name': 'Giovanni', 'surname': 'Rapa', 'age': 21 } ]";
			if(___insertStudentsStatement.charAt(0) != '[')
				___insertStudentsStatement = "[" + ___insertStudentsStatement + "]";
													
			org.json.JSONArray ___insertStudentsJsonArrayQuery = new org.json.JSONArray(___insertStudentsStatement);
			List <Document> insertStudents = new ArrayList <Document> ();
			
			for(int ___indexForJsonArray = 0; ___indexForJsonArray < ___insertStudentsJsonArrayQuery.length(); ___indexForJsonArray++) 
				insertStudents.add(Document.parse(___insertStudentsJsonArrayQuery.get(___indexForJsonArray).toString()));
			
			BsonDocument query = Document.parse("{ }").toBsonDocument();
			
			List <Table> result = __generateTableFromNoSQLQuery(nosql.find(query).cursor());;
			
			
			for(Table table : result) {
				
				System.out.println("\n" + table);
			}
			BsonDocument query_2 = Document.parse("{ }").toBsonDocument();
			
			boolean result_2 = (nosql.deleteMany(query_2).getDeletedCount() > 0) ? true : false;;
			
			
			if(result_2)
				
				System.out.println("\nCollection dropped");
			System.exit(0);
		}
			
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
		
	private static String __generateString(Table t,int id) {
			StringBuilder b = new StringBuilder();
			b.append("{\"id\":\""+id+"\",\"data\":");
			b.append("[");
			int i_r = t.rowCount();
			for(Row r : t) {
				b.append('{');
				for (int i=0;i< r.columnCount();i++) {
					b.append("\""+ r.columnNames().get(i) +"\":\""+r.getObject(i)+ ((i<r.columnCount()-1)?"\",":""));
				}
				b.append("\"}"+(((i_r != 1 ))?",":""));
				i_r--;
			}
			b.append("]}");
			return b.toString();
		}
		
		private static String __generateString(String s,int id) {
			StringBuilder b = new StringBuilder();
			b.append("{\"id\":"+id+",\"data\":");
			b.append("[");
			String[] tmp = s.split("\n");
			for(String t: tmp){
				b.append(t);
				if(t != tmp[tmp.length-1]){
					b.append(",");
				} 
			}
			b.append("]}");
			return b.toString();
		}
	
}
