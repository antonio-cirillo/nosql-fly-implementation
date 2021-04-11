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
import tech.tablesaw.api.IntColumn;
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
import org.json.JSONArray;

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
//			String ___insertStudentStatement = "{'name': 'Antonio', 'surname': 'Cirillo', 'age': 21}";
//			if(___insertStudentStatement.charAt(0) != '[')
//				___insertStudentStatement = "[" + ___insertStudentStatement + "]";
//													
//			org.json.JSONArray ___insertStudentJsonArrayQuery = new org.json.JSONArray(___insertStudentStatement);
//			List <Document> insertStudent = new ArrayList <Document> ();
//			
//			for(int ___indexForJsonArray = 0; ___indexForJsonArray < ___insertStudentJsonArrayQuery.length(); ___indexForJsonArray++) 
//				insertStudent.add(Document.parse(___insertStudentJsonArrayQuery.get(___indexForJsonArray).toString()));
//			
//			
//			nosql.insertMany(insertStudent);
//			String ___insertStudentsStatement = "[ { 'name': 'Andrea', surname': 'Di Pierno', 'age': 21 }, { 'name': 'Giovanni', 'surname': 'Rapa', 'age': 21 } ]";
//			if(___insertStudentsStatement.charAt(0) != '[')
//				___insertStudentsStatement = "[" + ___insertStudentsStatement + "]";
//													
//			org.json.JSONArray ___insertStudentsJsonArrayQuery = new org.json.JSONArray(___insertStudentsStatement);
//			List <Document> insertStudents = new ArrayList <Document> ();
//			
//			for(int ___indexForJsonArray = 0; ___indexForJsonArray < ___insertStudentsJsonArrayQuery.length(); ___indexForJsonArray++) 
//				insertStudents.add(Document.parse(___insertStudentsJsonArrayQuery.get(___indexForJsonArray).toString()));
			
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
						
			org.json.JSONObject ___jsonObject; // L'oggetto corrente che andiamo a leggere.
			ArrayList <ArrayList <String>> ___listFeatures = new ArrayList <> (); // La liste di tutte le features.
			ArrayList <org.json.JSONArray> ___listObjects = new ArrayList <> (); // La corrispondente lista di oggetti per quel tipo di features.
										
			while(___mongoCursor.hasNext()) {
				___jsonObject = new org.json.JSONObject(___mongoCursor.next().toJson()); // Leggiamo un oggetto.
				Iterator <String> ___iteratorJsonObjectKeys = ___jsonObject.keys(); // Otteniamo le features dell'oggetto appena letto.		
				ArrayList <String> ___listFeaturesCurrent = new ArrayList <String> (); // La liste delle features dell'oggetto che stiamo leggendo.
				
				while(___iteratorJsonObjectKeys.hasNext())
					___listFeaturesCurrent.add(___iteratorJsonObjectKeys.next());
				// Ora abbiamo la lista completa delle features che compongo l'oggetto appena letto.				
				
				
				int ___i = 0;			
				// Andiamo a contraollare se la struttura dell'oggetto che stiamo leggendo corrisponde ad una struttura già letta.
				for(; ___i < ___listFeatures.size(); ++___i)
					if(___listFeatures.get(___i).equals(___listFeaturesCurrent))
						break;
				
				if(___i >= ___listFeatures.size()) { // Se non abbiamo ancora letto un oggetto con questa struttura, allora... 
					___listFeatures.add(___listFeaturesCurrent); // Aggiungiamo questa struttura alla lista delle strutture,
					org.json.JSONArray ___listObjectForThisFeatures = new org.json.JSONArray(); // Creaiamo la lista di oggetti corrispondenti per questa struttura,
					___listObjectForThisFeatures.put(___jsonObject); // Aggiungiamo l'oggetto che abbiamo letto alla lista di oggetti,
					___listObjects.add(___listObjectForThisFeatures); // Aggiungiamo la lista di oggetti per questa struttura alla lista di tutti gli oggetti.
				} else  // Se abbiamo letto già un oggetto con questa struttura, allora...
					___listObjects.get(___i).put(___jsonObject); // Aggiungiamo l'oggetto alla lista degli oggetti con questa struttura.			
			}
			
			for(int ___i = 0; ___i < ___listFeatures.size(); ++___i) { // Ora per ogni lista di features... 
				Table ___table = Table.create(); // Creiamo una tabella,
				ArrayList <String> ___features = ___listFeatures.get(___i); // Otteniamo le features,
				org.json.JSONArray ___objects = ___listObjects.get(___i); // Otteniamo gli oggetti che hanno queste features,
				
				for(Column <?> ___colum : ___generateColumns("", ___features, ___objects)) 
						___table.addColumns(___colum); // Creiamo la colonna
									
				
				___resultGenerateTableFromNoSQLQuery.add(___table);
			}
			
			return 	___resultGenerateTableFromNoSQLQuery;
		
		}
		
		private static List <Column <?>> ___generateColumns(String ___nameColumn, List <String> ___features, org.json.JSONArray ___objects) {
			List <Column <?>> ___columns = new ArrayList <> ();
			int ___j = 0;
			
			for(String ___feature : ___features) {				
				org.json.JSONObject ___object = ___objects.getJSONObject(0);
				Object ___value = ___object.get(___feature);
				
				if(___value instanceof org.json.JSONObject) {
					Iterator <String> ___subKeys = ((org.json.JSONObject) ___value).keys();
					List <String> ___subFeatures = new ArrayList <> ();
					while(___subKeys.hasNext())
						___subFeatures.add(___subKeys.next());
					
					org.json.JSONArray ___subObjects = new org.json.JSONArray().put(___value);
					for(int ___i = 1; ___i < ___objects.length(); ++___i) {
						___object = ___objects.getJSONObject(___i);
						___value = ___object.get(___feature);
						___subObjects.put(___value);
					}
					
					for(Column <?> ___column : ___generateColumns(___feature + "_", ___subFeatures, ___subObjects)) {
						___columns.add(___column);
						++___j;
					}
						
					
				} else {
				
					if(___value instanceof Integer)
						___columns.add(IntColumn.create(___nameColumn + ___feature, (int) ___value));
					else				
						___columns.add(StringColumn.create(___nameColumn +___feature, "" + ___value));
					
					for(int ___i = 1; ___i < ___objects.length(); ++___i) {					
						___object = ___objects.getJSONObject(___i);
						___value = ___object.get(___feature);
						
						if(___value instanceof Integer)
							((IntColumn) ___columns.get(___j)).append((int) ___value);
						else
							((StringColumn) ___columns.get(___j)).append("" + ___value);					
					}
				
					++___j;
					
				}
								
			}
					
			return ___columns;
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
