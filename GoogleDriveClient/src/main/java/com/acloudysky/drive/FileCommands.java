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
package com.acloudysky.drive;

import com.google.api.client.http.FileContent;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpResponseException;
import com.google.api.services.drive.Drive;

import com.google.api.services.drive.Drive.Files;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;

import com.google.api.services.drive.model.ParentReference;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;


/**
 * Contains the methods which issue Google Drive REST API calls 
 * for file operations.
 * @author Michael
 *
 */
public class FileCommands {

	/**
	 * Internal class that contains utility methods used during the
	 * processing of file commands.
	 * @author mmiele
	 *
	 */
	private static class Utilities {
		
		private static StringBuilder buffer;
		/*
		 * ****** Utilities ******
		 ****/
		
		/**
		 * Display information for the listed files.
		 * @param files The list of files for which to display information.
		 */
		private static void displayFilesInfo(List<File> files) {
			
			// Initialize the buffer to hold formatted information.
			buffer = new StringBuilder();
			buffer.append(String.format("%n================== " + "File Information" + " ================== %n"));	
			
			// Get the file list iterator.
			Iterator<File> iterator = files.iterator();
			
			// Iterate through the list of files and store 
			// formatted info for each file.
			while (iterator.hasNext()) {	
				File f = iterator.next();
				buffer.append(String.format("%nTitle:         %s", f.getTitle()));
				buffer.append(String.format("%n  ID:          %s", f.getId()));
				buffer.append(String.format("%n  Mime Type:   %s", f.getMimeType()));
				buffer.append(String.format("%n  Date:        %s", f.getCreatedDate()));
				buffer.append(String.format("%n  Permission:  %s", f.getUserPermission()));
				buffer.append(String.format("%n  Description: %s", f.getDescription()));
	        }
			
			// Display files information.
			System.out.println(buffer.toString());
		}

		/**
		 * Display information for the specified file.
		 * @param file The file for which to display information.
		 */
		private static void displayFileInfo(File file) {
			
			// Initialize the buffer to hold formatted information.
			buffer = new StringBuilder();
			buffer.append(String.format("%n================== " + "File Information" + " ================== %n"));	
			
			// Store formatted file info.
			buffer.append(String.format("%nTitle:         %s", file.getTitle()));
			buffer.append(String.format("%n  ID:          %s", file.getId()));
			buffer.append(String.format("%n  Mime Type:   %s", file.getMimeType()));
			buffer.append(String.format("%n  Date:        %s", file.getCreatedDate()));
			buffer.append(String.format("%n  Permission:  %s", file.getUserPermission()));
			buffer.append(String.format("%n  Description: %s", file.getDescription()));
			
			// Display files information.
			System.out.println(buffer.toString());
		}
		
	
		
		 /**
		   * Download a file's content.
		   *
		   * @param service Drive API service instance.
		   * @param file Drive File instance.
		   * @return InputStream containing the file's content if successful,
		   *         {@code null} otherwise.
		   */
		  private static InputStream downloadFile(Drive service, File file) {
		    if (file.getDownloadUrl() != null && file.getDownloadUrl().length() > 0) {
		      try {
		    	  
		    	  // Create HTTP request.
		    	  HttpRequest request =  
		    			  service.getRequestFactory().buildGetRequest(new GenericUrl(file.getDownloadUrl()));
		    	  
		    	  // Get the HTTP response.
		    	  HttpResponse response = request.execute();
		    	  
		    	// Display header information, if debug is enabled.
		        if (GeneralUtilities.isDEBUG()) {
			        GeneralUtilities.displayRequestHeaders(request);
			        GeneralUtilities.displayResponseHeaders(response);
				        	
		        }
		        
		        // Return content stream.
		        return response.getContent();
		        
		      } catch (IOException e) {
		        // An error occurred.
		        // e.printStackTrace();
		       	System.out.println(String.format("Error occured: %s", 
		       			e.getMessage()));

		        return null;
		      }
		    } else {
		      // The file doesn't have any content stored on Drive.
		      System.out.println(String.format("Error occured: %s does not have content.", 
		    		  file.getTitle()));

		      return null;
		    }
		  }
		
		
	}
	
	
	// The authorized Drive service.
	private Drive service;
	
	/**
	 * Class constructor. Initializes global variables.
	 * @param authorizedService The authenticated service 
	 * authorized to access Google Drive using its REST API. 
	 */
	public FileCommands(Drive authorizedService) {
		service = authorizedService;
	}
	
	  
	/******** File Commands *******/

	/**
	 * Gets a file metadata. Also copies content to a local file.
	 * @param fileID The ID of the file for which to get the metadata.
	 * @param dir The directory containing the copied file.
	 * @param localFile The file where to copy the content.
	 * @throws IOException An I/O error has been detected.
	 */
	public void getFile(String fileID, String dir, String localFile) throws IOException {
		
		try {
			// Get the file.
			File file = service.files().get(fileID).execute();
			
		    // Display files information.
			Utilities.displayFileInfo(file);
		    
		    // Download file
		    InputStream inStream = Utilities.downloadFile(service, file);
		    String msg = 
		    		String.format("%n================== " + "Reading content of file: %s" + " ================== %n", file.getTitle());
		    System.out.println(msg);	
		    
		    List<ParentReference> parents =  file.getParents();
		    // Get the file list iterator.
			Iterator<ParentReference> iterator = parents.iterator();
			while (iterator.hasNext()) {	
				ParentReference entry = iterator.next();
				String parentID = entry.getId();
				String value = entry.toPrettyString();
				System.out.println(String.format("Key: %s Value: %s", parentID, value));
			}
			
		    
		    // Copy to a local file.
			FileUtilities.copyFileFromInputStream(inStream, dir, localFile);
		    
		} 
		catch (IOException e) {System.out.println(
					   String.format("Error occurred: %s", e.getMessage()));
		}
		
	}
	
	
	 /**
	  * Retrieves the list of user's files.
	  * @throws IOException An I/O error has been detected.
	  ***/
	  public void listFiles() throws IOException {
		  
		  // Define a list to contain file objects.
		  List<File> fileList = new ArrayList<File>();
		  
		  Files.List request = service.files().list();
		  
		  
		  do {
		      try {
		        FileList files = request.execute();
	
		        fileList.addAll(files.getItems());
		        request.setPageToken(files.getNextPageToken());
		      } catch (IOException e) {
		        System.out.println("An error occurred: " + e);
		        request.setPageToken(null);
		      }
		      
		      // Display files information.
		      Utilities.displayFilesInfo(fileList);
		    
		  } while (request.getPageToken() != null &&
		             request.getPageToken().length() > 0);

	  }

	  
	  /**
	   * Inserts new file.
	   *
	   * @param title Title of the file to insert, including the extension.
	   * @param description Description of the file to insert.
	   * @param parentId Optional parent folder's ID.
	   * @param mimeType MIME type of the file to insert.
	   * @param filename Filename of the file to insert.
	   * @return Inserted file metadata if successful, {@code null} otherwise.
	   ***/
	  public File insertFile(String title, String description,
	      String parentId, String mimeType, String filename) {
	   
		// File's metadata.
	    File body = new File();
	    body.setTitle(title);
	    body.setDescription(description);
	    body.setMimeType(mimeType);

	    // Set the parent folder.
	    if (parentId != null && parentId.length() > 0) {
	      body.setParents(
	          Arrays.asList(new ParentReference().setId(parentId)));
	    }

	    // File's content.
	    java.io.File fileContent = new java.io.File(filename);
	    FileContent mediaContent = new FileContent(mimeType, fileContent);
	    try {
	    	// ParentReference parent = new ParentReference();
	    	// parent.setId(parentId);
	    	 
	    	File file = service.files().insert(body, mediaContent).execute();
	    	// service.parents().insert(file.getId(), parent).execute();  
	    	 // Uncomment the following line to print the File ID.
	    	 // System.out.println("File ID: " + file.getId());
	    	 // System.out.println("Mime type: " + file.getMimeType());
	    	 
	    	 // System.out.println("Parent ID: " + parent.getId());
	    	 
	    	 return file;
	    } catch (IOException e) {
	      System.out.println("An error occured: " + e);
	      return null;
	    }
	  }

	  

	 /******* Parents Commands *******/
	
	  /**
	   * Checks if a file is in a specific folder
	   * @param folderId ID of the folder.
	   * @param fileId ID of the file.
	   * @return Whether or not the file is in the folder.
	   * @throws IOException An I/O error has been detected.
	   */
	  public boolean isFileInFolder(String folderId,
	      String fileId) throws IOException {
	    try {
	    	ParentReference resp = service.parents().get(fileId, folderId).execute();
	    	
	    } catch (HttpResponseException e) {
	      if (e.getStatusCode() == 404) {
	        return false;
	      } else {
	        System.out.println("An error occured: " + e);
	        throw e;
	      }
	    } catch (IOException e) {
	      System.out.println("An error occured: " + e);
	      throw e;
	    }
	    return true;
	  }

	  /**
	   * Gets the parents of a file.
	   * @param fileID The ID of the file whose parents must be found.
	   * @throws IOException An I/O error has been detected.
	   ***/
	  public void getPsrents(String fileID) throws IOException {
			
		try {
			// Get the file.
			File file = service.files().get(fileID).execute();
			String fileName = file.getTitle();
		    String msg = 
		    		String.format("%n================== " + "Getting parents of file: %s" + " ================== %n", fileName);
		    System.out.println(msg);	
		    
		    // Get the parents list.
		    List<ParentReference> parents =  file.getParents();
		    // Get the file list iterator.
			Iterator<ParentReference> iterator = parents.iterator();
			
			while (iterator.hasNext()) {	
				ParentReference entry = iterator.next();
				String parentID = entry.getId();
				String value = entry.toPrettyString();
				System.out.println(String.format("Key: %s Value: %s", parentID, value));
			}
			
		} 
		catch (IOException e) {System.out.println(
					   String.format("Error occurred: %s", e.getMessage()));
		}
		
	}
		  
	
}
