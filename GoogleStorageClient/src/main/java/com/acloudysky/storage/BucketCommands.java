package com.acloudysky.storage;

import static java.net.HttpURLConnection.HTTP_CONFLICT;

import java.io.IOException;

import com.google.api.client.googleapis.json.GoogleJsonError;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.services.storage.Storage;
import com.google.api.services.storage.model.Bucket;
import com.google.api.services.storage.model.ObjectAccessControl;
import com.google.common.collect.ImmutableList;

public class BucketCommands {
	
	// Cloud Storage authenticated service.
	private static Storage storageService;
		
	// Settings applicable to the current application.
	private static DefaultSettings settings;
	
	/**
	 * Initialize authenticated storage service and
	 * settings applicable to the application.
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
	 * Create a bucket with the specified name in the specified project.
	 * If successful, return the created bucket metadata.
	 * <p>
	 * For more information, see <a href="https://cloud.google.com/storage/docs/json_api/v1/buckets/insert" 
	 * target="_blank">Buckets:insert</>
	 * </p>
	 * @param bucketName The name of the bucket to create.
	 * @throws IOException
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

	 /**
	  * Retrieve the specified bucket metadata.
	  * <p>
	  * For more information, see <a href="https://cloud.google.com/storage/docs/json_api/v1/buckets/get" 
	  * target="_blank">Buckets:get</>
	  * </p>
	  * @throws IOException
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