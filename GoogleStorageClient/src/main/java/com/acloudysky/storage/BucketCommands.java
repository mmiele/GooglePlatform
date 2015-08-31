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

import static java.net.HttpURLConnection.HTTP_CONFLICT;

import java.io.IOException;

import com.google.api.client.googleapis.json.GoogleJsonError;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.services.storage.Storage;
import com.google.api.services.storage.model.Bucket;
import com.google.api.services.storage.model.ObjectAccessControl;
import com.google.common.collect.ImmutableList;

/***
 * Contains methods to perform Google Cloud Storage bucket operations.
 * @author Michael
 *
 */
public class BucketCommands {
	
	// Cloud Storage authenticated service.
	private static Storage storageService;
		
	// Settings applicable to the current application.
	private static DefaultSettings settings;
	
	/**
	 * Initialize authenticated storage service using default settings.
	 * @param authorizedService The storage authorized service
	 * @param defaultSettings The default settings in JSON format
	 */
	public static void initBucketCommands(Storage authorizedService, 
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
	 * Display header information for the user.
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
	
	/***
	 * Create a bucket with the specified name in the specified project.
	 * If successful, return the created bucket metadata.
	 * <p>
	 * For more information, see <a href="https://cloud.google.com/storage/docs/json_api/v1/buckets/insert" 
	 * target="_blank">Buckets:insert</a>
	 * </p>
	 * @param bucketName The name of the bucket to create.
	 * @throws IOException IO error
	 * @return true if the operation succeeded; otherwise, false
	 */
	public static boolean insertBucket(String bucketName) throws IOException {
	    displayMessageHeader("Create the bucket: " + bucketName);
	    
	    Storage.Buckets buckets = storageService.buckets();
	    boolean bucketCreated = false;
	    
	    // Set the bucket access control list.
	    ObjectAccessControl acl = new ObjectAccessControl();
	    acl.setEntity("allAuthenticatedUsers").setRole("READER");
	    
	    // Create bucket.
	    Bucket newBucket = new Bucket().setName(bucketName);
	    newBucket.setLocation("US");
	    newBucket.setDefaultObjectAcl(ImmutableList.of(acl));
	    
	    Storage.Buckets.Insert insertBucket = 
	    		buckets.insert(settings.getProject(), newBucket);
	   
	    try {
	      @SuppressWarnings("unused")
	      Bucket createdBucket = insertBucket.execute();
	      bucketCreated = true;
	      System.out.println(String.format("Bucket %s created", bucketName));
	    } catch (GoogleJsonResponseException e) {
	      GoogleJsonError error = e.getDetails();
	      if (error.getCode() == HTTP_CONFLICT
	          && error.getMessage().contains("You already own this bucket.")) {
	        System.out.println("already exists");
	      } else {
	        throw e;
	      }
	    }
	    return bucketCreated;
	  }

	 /***
	  * Retrieve the specified bucket metadata.
	  * <p>
	  * For more information, see <a href="https://cloud.google.com/storage/docs/json_api/v1/buckets/get" 
	  * target="_blank">Buckets:get</a>
	  * </p>
	  * @param bucketName The name of the bucket
	  * @throws IOException IO error
	  */
	 public static void getBucket(String bucketName) throws IOException {
	   
		// Obtain getBucket method from the Buckets method collection.
		Storage.Buckets.Get getBucket;
		String selectedBucketName = "";
		
		if (bucketName.isEmpty()) {
			// Use the default bucket.
			selectedBucketName = settings.getBucket();
		}
		else {
			// Use the passed bucket.
			selectedBucketName = bucketName;
		}
		
		displayMessageHeader("Getting bucket " + selectedBucketName + " metadata");
		getBucket = storageService.buckets().get(selectedBucketName);
		
		getBucket.setProjection("full");
	    Bucket bucket = getBucket.execute();
	    displayBucketInformation(bucket);
	   
	  }
	

}
