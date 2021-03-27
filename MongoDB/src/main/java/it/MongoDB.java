package it;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bson.Document;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Projections;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;

public class MongoDB {

	public static void main(String[] args) {
		
		client = MongoClients.create();
		database = client.getDatabase("my-db");
		
		collection = database.getCollection("weatherHistory");
		
		if(collection.countDocuments() == 0) { // We have to populate it.
			try {
				collection.insertMany(getDocumentsFromCSV());
			} catch(Exception e) {
				System.out.println("Failed to parse CSV file into List <Document>");
				return;
			}
		}
		
		System.out.println("There are " + collection.countDocuments() + " element(s) insiede " + 
				database.getName() + " database.");
		
		cursor = collection.find().cursor();
		try {
			for(int i = 1; cursor.hasNext(); i++)
				System.out.println(i + ": " + cursor.next().toJson());
		} finally {
			cursor.close();
		}
		
		FindIterable <Document> query =	collection.find().
				projection(Projections.fields(Projections.include(features[3]), Projections.excludeId()));
		
		cursor = query.cursor();
		double temperatureAverage = 0;
		double temperatureMin = 100;
		double temperatureMax = -100;
		int numItems = 0;
		while(cursor.hasNext()) {
			double tmp = Double.parseDouble(cursor.next().getString(features[3]));
			if(tmp > temperatureMax) temperatureMax = tmp;
			if(tmp < temperatureMin) temperatureMin = tmp;
			temperatureAverage += tmp;
			++numItems;
		}
		temperatureAverage /= numItems;
		
		System.out.println("The average temperature is: " + temperatureAverage);
		System.out.println("The maximum temperature is: " + temperatureMax);
		System.out.println("The minimum temperature is: " + temperatureMin);
		
	}	
	
	public static List <Document> getDocumentsFromCSV() throws IOException, CsvException {
		List <Document> documents = new ArrayList <Document> ();
		
		 try (CSVReader reader = new CSVReader(new FileReader("target/classes/weatherHistory.csv"))) {
	            List <String[]> r = reader.readAll();
	            for(int i = 1; i <= MAX_ITEMS; i++) {
	            	Document tmp = new Document();
	            	for(int j = 0; j < features.length; j++) 
	            		tmp.append(features[j], r.get(i)[j]);
	            	documents.add(tmp);	            	
	            }
	        }
		 		
		return documents;
	}
	
	public static MongoClient client;
	public static MongoDatabase database;
	public static Document document;
	public static MongoCollection <Document> collection;
	public static MongoCursor <Document> cursor;
	
	public static final int MAX_ITEMS = 100;
	public static final String[] features = {"Formatted Date", "Summary", "Precip Type", "Temperature (C)", "Apparent Temperature (C)",
            "Humidity", "Wind Speed (km/h)", "Wind Bearing (degrees)", "Visibility (km),Loud Cover", 
            "Pressure (millibars)", "Daily Summary"};

}
