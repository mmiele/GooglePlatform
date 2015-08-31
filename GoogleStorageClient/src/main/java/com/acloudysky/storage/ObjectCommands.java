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
import java.util.List;

import com.google.api.client.http.InputStreamContent;
import com.google.api.client.util.Lists;
import com.google.api.services.storage.Storage;
import com.google.api.services.storage.model.Bucket;
import com.google.api.services.storage.model.ObjectAccessControl;
import com.google.api.services.storage.model.Objects;
import com.google.api.services.storage.model.StorageObject;
import com.google.common.collect.ImmutableMap;

/**
 * Contains methods to perform Google Cloud Storage object operations.
 * @author Michael
 *
 */
public class ObjectCommands {
	

	// Cloud Storage authenticated service.
	private static Storage storageService;
			
	// Settings applicable to the current application.
	private static DefaultSettings settings;
		
	/***
	 * Initialize authenticated storage service and
	 * settings applicable to the application.
	 * @param defaultSettings Default settings in JSON format
	 * @param authorizedService Storage authorized service
	 */
	public static void initObjectCommands(Storage authorizedService, 
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
	
	 static private void displayObjectInformation(StorageObject object) {
		 StringBuffer results = new StringBuffer();
		 results.append(String.format("%n name %s %n", object.getName()));
		 results.append(String.format(" size %d %n", object.getSize()));
		 results.append(String.format(" content type %s %n", object.getContentType()));
		 results.append(String.format(" owner %s %n", object.getOwner()));
		 results.append(String.format(" updated %s %n", object.getUpdated()));
	      
		 // Display header.
		 System.out.println(results.toString());
	 }
	 
	 static private void displayMessageHeader2(String name) {
		 StringBuffer header = new StringBuffer();
		 header.append(String.format("%n~~~~~~~~~~~~~~~~~~  %s ~~~~~~~~~~~~~~~~~~%n", name));
		 // Display header.
		 System.out.println(header.toString());
	 }
	 
	 
	 static private void displaySeparator() {
		 StringBuffer buffer = new StringBuffer();
		 buffer.append(String.format("------------------------------------------------------"));
		 // Display header.
		 System.out.println(buffer.toString());
	 }
	 
	
	/***
	 * Retrieve a list of objects contained by a bucket. 
	 * Display metadata for all the objects contained in the bucket.
	 * <p>
	 * For more information, see <a href="https://cloud.google.com/storage/docs/json_api/v1/objects/list" 
	 * target="_blank">Objects:list</a> 
	 * </p>
	 * @param bucketName The name of the bucket
	 * @throws IOException IO error
	 */
	public static void listObjects(String bucketName) throws IOException {
		
		String selectedBucketName = "";
		
		if (bucketName.isEmpty()) {
			// Use the default bucket.
			selectedBucketName = settings.getBucket();
		}
		else {
			// Use the passed bucket.
			selectedBucketName = bucketName;
		}
		
		displayMessageHeader("Listing objects in bucket " + selectedBucketName);
		
		// Call the Storage Object list method.
	    Storage.Objects.List listObjects = 
	    		storageService.objects().list(selectedBucketName);
	    // Set maximum value to return.
	    listObjects.setMaxResults(5L);
	    // Execute the command.
	    Objects objects = listObjects.execute();
	   
	    /*Keep track of the page number in case we're listing objects
	     * for a bucket with thousands of objects. We'll limit ourselves
	     * to 5 pages
	     ***/
	    int currentPageNumber = 0;
	    while (objects.getItems() != null && !objects.getItems().isEmpty()
	        && ++currentPageNumber <= 5) {
	      for (StorageObject object : objects.getItems()) {
	        displayObjectInformation(object);
	        displaySeparator();
	      }
	      // Fetch the next page
	      String nextPageToken = objects.getNextPageToken();
	      if (nextPageToken == null) {
	        break;
	      }
	      listObjects.setPageToken(nextPageToken);
	      displayMessageHeader2("New page of objects");
	      objects = listObjects.execute();
	    }
	  }
	
	/***
	 * Display an object metadata.
	 * <p>
	 * For more information, see <a href="https://cloud.google.com/storage/docs/json_api/v1/objects/get" 
	 * target="_blank">Objects:get</a>
	 * </p>
	 * @param bucketName The name of the bucket containing the object
	 * @param objectName The name of the object
	 * @throws IOException IO error
 	*/
	public static void getObject(String bucketName, 
			String objectName) throws IOException {
		
		String selectedBucketName = "";
		String selecteObjectName = "";
		
		if (bucketName.isEmpty()) {
			// Use the default bucket.
			selectedBucketName = settings.getBucket();
		}
		else {
			// Use the passed bucket.
			selectedBucketName = bucketName;
		}
		
		if (objectName.isEmpty()) {
			// Use the default object.
			selecteObjectName = settings.getObject();
		}
		else {
			// Use the passed object.
			selecteObjectName = objectName;
		}
		displayMessageHeader("Getting object " + objectName + " metadata");
		
		
		// Obtain the specified object from the collection
		Storage.Objects.Get getObject = 
				storageService.objects().get(selectedBucketName, selecteObjectName);
		// Access the object
	    StorageObject object = getObject.execute();
	    displayObjectInformation(object);
	  }
	
	/***
	 * Upload an object.
	 * @param useCustomMetadata true if to sue cuetom metadata; otherwise, false
	 * @throws IOException IO error
	 */
	public static void uploadObject(boolean useCustomMetadata) throws IOException {
		displayMessageHeader("Uploading object.");
	    final long objectSize = 100 * 1000 * 1000 /* 100 MB */;
	    InputStreamContent mediaContent = new InputStreamContent(
	        "application/octet-stream", new ObjectLoaderUtility.RandomDataBlockInputStream(objectSize, 1024));
	    // Not strictly necessary, but allows optimization in the cloud.
	    // mediaContent.setLength(OBJECT_SIZE);

	    StorageObject objectMetadata = null;

	    if (useCustomMetadata) {
	      // If you have custom settings for metadata on the object you want to set
	      // then you can allocate a StorageObject and set the values here. You can
	      // leave out setBucket(), since the bucket is in the insert command's
	      // parameters.
	      List<ObjectAccessControl> acl = Lists.newArrayList();
	      if (settings.getEmail() != null && !settings.getEmail().isEmpty()) {
	        acl.add(
	            new ObjectAccessControl().setEntity("user-" + settings.getEmail()).setRole("OWNER"));
	      }
	      if (settings.getDomain() != null && !settings.getDomain().isEmpty()) {
	        acl.add(new ObjectAccessControl().setEntity("domain-" + settings.getDomain())
	            .setRole("READER"));
	      }
	      objectMetadata = new StorageObject().setName(settings.getPrefix() + "myobject")
	          .setMetadata(ImmutableMap.of("key1", "value1", "key2", "value2")).setAcl(acl)
	          .setContentDisposition("attachment");
	    }

	    Storage.Objects.Insert insertObject =
	        storageService.objects().insert(settings.getBucket(), objectMetadata, mediaContent);

	    if (!useCustomMetadata) {
	      // If you don't provide metadata, you will have specify the object
	      // name by parameter. You will probably also want to ensure that your
	      // default object ACLs (a bucket property) are set appropriately:
	      // https://developers.google.com/storage/docs/json_api/v1/buckets#defaultObjectAcl
	      insertObject.setName(settings.getPrefix() + "myobject");
	    }

	    insertObject.getMediaHttpUploader()
	        .setProgressListener(new ObjectLoaderUtility.CustomUploadProgressListener()).setDisableGZipContent(true);
	    // For small files, you may wish to call setDirectUploadEnabled(true), to
	    // reduce the number of HTTP requests made to the server.
	    if (mediaContent.getLength() > 0 && mediaContent.getLength() <= 2 * 1000 * 1000 /* 2MB */) {
	      insertObject.getMediaHttpUploader().setDirectUploadEnabled(true);
	    }
	    insertObject.execute();
	  }

	

}
