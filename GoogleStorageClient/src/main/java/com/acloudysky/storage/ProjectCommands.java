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

	// Cloud Storage authenticated service.
	private static Storage storageService;
	
	// Settings applicable to the current application.
	private static DefaultSettings settings;
	
	/**
	 * Initialize the authenticated storage service and default settings. 
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
	
	/*
	 * Display header information.
	 */
	 static private void displayMessageHeader(String name) {
		 StringBuffer header = new StringBuffer();
		 header.append(String.format("%n==================  %s ==================%n", name));
		 // Display header.
		 System.out.println(header.toString());
	 }
	    
	 /*
	  * Display bucket information. 
	  */
	 static private void displayBucketInformation(Bucket bucket) {
		 StringBuffer results = new StringBuffer();
		 results.append(String.format("%n name %s %n", bucket.getName()));
		 results.append(String.format(" location %s %n", bucket.getLocation()));
		 results.append(String.format(" timeCreated %s %n", bucket.getTimeCreated()));
		 results.append(String.format(" owner %s %n", bucket.getOwner()));
		 results.append(String.format(" acl %s %n", bucket.getAcl()));
	      
		 // Display header.
		 System.out.println(results.toString());
	 }
	 

 	/**
	 * Retrieve the list of buckets for the given project.
	 * Display the metadata of the retrieved buckets.
	 * For more information, see 
	 * <a href="https://developers.google.com/resources/api-libraries/documentation/storage/v1beta2/java/latest/com/google/api/services/storage/Storage.Buckets.List.html" target="_blank">Storage.Buckets.List</a>.
	 * @throws IOException IO error
	 */
	public static void listBuckets() throws IOException {
	  
		// Get the list of the buckets in the project.
		// Call the service REST API.
		Storage.Buckets.List listBuckets = 
				storageService.buckets().list(settings.getProject());
		// Java data model class that specifies how to parse/serialize 
		// HTTP wire traffic into JSON format.
		Buckets buckets;
		// Loop to read buckets metadata.
		do {
			buckets = listBuckets.execute();
			
			for (Bucket bucket : buckets.getItems()) {
			  displayMessageHeader("Getting bucket " + bucket.getName() + " metadata");
			  displayBucketInformation(bucket);
		  }
		  listBuckets.setPageToken(buckets.getNextPageToken());
		} while (buckets.getNextPageToken() != null);
		
	  }
 
}
