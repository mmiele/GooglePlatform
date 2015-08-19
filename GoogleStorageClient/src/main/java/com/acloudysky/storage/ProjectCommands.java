/** 
 * LEGAL: Use and Disclaimer. 
 * This software belongs to the owner of the http://www.acloudysky.com site and supports the
 * examples described there. 
 * Unless required by applicable law or agreed to in writing, this software is distributed on 
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. 
 * Please, use the software accordingly and provide the proper acknowledgement to the author.
 * @author milexm@gmail.com  
 **/
package com.acloudysky.storage;

import java.io.IOException;

import com.google.api.services.storage.Storage;
import com.google.api.services.storage.model.Bucket;
import com.google.api.services.storage.model.Buckets;

import java.util.logging.*;

/**
 * Contains methods to perform Google Cloud Storage project operations.
 * A method calls the related REST API
 * @see com
 * @author Michaael
 *
 */
public class ProjectCommands {

	private static Logger httplogger;
	
	// Cloud Storage authenticated service.
	private static Storage storageService;
	
	// Settings applicable to the current application.
	private static DefaultSettings settings;
	
	/**
	 * Initializes the authenticated storage service and default settings. 
	 * @param authorizedService The object representing the authorized service.
	 * @param defaultSettings The default values to use for the project
	 */
	public static void initProjectCommands(Storage authorizedService, 
			DefaultSettings defaultSettings) {
		
		// Create authorized cloud storage service.
		storageService = authorizedService;
		
		// Initialize sample settings.
		settings = 	defaultSettings;
	}
	
	/*
	 * ******** Utility Methods   *********
	 */
	 static void displayMessageHeader(String name) {
	      System.out.println();
	      System.out.println("================== " + name + " ==================");
	      System.out.println();
	    }
	    
	
	 static void displayBucketInformation(Bucket bucket) {
	      System.out.println("name: " + bucket.getName());
	      System.out.println("location: " + bucket.getLocation());
	      System.out.println("timeCreated: " + bucket.getTimeCreated());
	      System.out.println("owner: " + bucket.getOwner());
	      System.out.println("acl: " + bucket.getAcl());
	      
	    }
	 

 	/**
	 * Retrieves the a list of buckets for a given project.
	 * Displays the metadata of the retrieved buckets.
	 * For more information, see 
	 * <a href="https://developers.google.com/resources/api-libraries/documentation/storage/v1beta2/java/latest/com/google/api/services/storage/Storage.Buckets.List.html" target="_blank">Storage.Buckets.List</a>.
	 * @throws IOException IO error
	 */
	public static void listBuckets() throws IOException {
	  
		// Get the list of the buckets in the projects.
		// Call the service REST API.
		Storage.Buckets.List listBuckets = 
				storageService.buckets().list(settings.getProject());
		// Java data model class that specifies how to parse/serialize 
		// into the JSON that is transmitted over HTTP when working 
		// with the Cloud Storage API. 
		Buckets buckets;
		// Loop to read buckets metadata.
		do {
			
			
			httplogger = Logger.getLogger("com.google.api.client.http.HttpRequest");
			httplogger.setLevel(Level.ALL);
			System.out.println(httplogger.toString());
		     
		     // Create a log handler which prints all log events to the console.
		     ConsoleHandler logHandler = new ConsoleHandler();
		     logHandler.setLevel(Level.ALL);
		     httplogger.addHandler(logHandler);
				   
		   
			// HttpRequest request = listBuckets.buildHttpRequest();
		
		
			buckets = listBuckets.execute();
			
		  
		  for (Bucket bucket : buckets.getItems()) {
			
			  displayMessageHeader("Getting bucket " + bucket.getName() + " metadata");
			  displayBucketInformation(bucket);
			  
		  }
		  listBuckets.setPageToken(buckets.getNextPageToken());
		} while (null != buckets.getNextPageToken());
		
	  }
 
}
