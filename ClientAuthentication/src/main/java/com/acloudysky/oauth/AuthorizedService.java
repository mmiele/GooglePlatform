
 /** 
 * Legal Use and Disclaimer 
 * This software belongs to the owner of the http://www.acloudysky.com site and supports the
 * examples described there. 
 * Unless required by applicable law or agreed to in writing, this software is distributed on 
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. 
 * Please, use the software accordingly and provide the proper acknowledgement to the author.
 * @author mielem@gmail.com  
 **/
 package com.acloudysky.oauth;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;


import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.java6.auth.oauth2.VerificationCodeReceiver;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.json.GoogleJsonError;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpResponseException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.storage.Storage;
import com.google.api.services.storage.StorageScopes;


/**
 * The AuthorizedService class creates a service object that is authorized to 
 * access the selected Google service API. 
 * <ul>
 * 	<li> It applies to client applications that are of
 * the <b>installed application</b> type. For more information, see 
 * <a href="http://acloudysky.com/?p=1043#usingoauth" target="_blank">
 * Using OAuth2.0 to Access Google APIs</a> </li>
 * 	<li> Based on the caller' selection, it allows the creation of an authorized 
 * 	service to access service APIs cuch as <a href="https://cloud.google.com/storage/docs/overview" target="_blank">Google Cloud Storage API</a> or 
 * <a href="https://developers.google.com/drive/web/about-sdk" target="_blank">Google Drive API</a>.</li>
 * </ul>
 * @author Michael Miele
 *
 */
public class AuthorizedService {

	/**
	   * Be sure to specify the name of your application. If the application name is {@code null} or
	   * blank, the application will log a warning. Suggested format is "MyCompany-ProductName/1.0".
	   */
	  private  final String APPLICATION_NAME = "acloudysky.com-authorizedservice/1.0";
	
	  // Directory and file where client secrets are kept. 
	  private  static String base_dir, data_dir;
	  private  static String clientSecretsFile;
	
	  // Get the current computer OS name and the user home directory. 
	  private  static String OS = System.getProperty("os.name");
      private  static String home_dir = System.getProperty("user.home");
	 
      // Directory to store user credentials. 
	  private java.io.File DATA_STORE_DIR;
	  
	  // Contains the authorized service.
	  private Object service;
	  
	  public Object getService() {
		return service;
	}

	
	/**
	   * Global instance of the {@link DataStoreFactory}. The best practice is to make it a single
	   * globally shared instance across your application.
	   */
	  private static FileDataStoreFactory dataStoreFactory;
	
	  /** Global instance of the HTTP transport. */
		  private  HttpTransport httpTransport;
	 
		  // Key fields.....
	  
	  /** Global instance of the JSON factory. */
	  private  final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
	  

	  /**
	   * Class constructor.
	   * @param baseDir The parent directory
	   * @param sampleDir The directory containing the file with the client secrets.
	   * @param secretsFile The file containing the client secrets.
	   * <p><b>Note</b>, The client secrets are:
	   * <ol>
	   * 	<li><b>Client ID</b></li>
	   * 	<li><b>Client secrets</b></li>
	   * 	<li><b>Other...</b>
	   * </ol>
	   * You obtain this information by downloading the JSON format of the 
	   * <i>Client ID for native application</i> of your project at: 
	   * <a href="https://console.developers.google.com/project" target="_blank">Google console</a>.
	   * Then copy this information in the <i>secretsFile</i> such as client_secrets.json. 
	   * <b>Keep this file in a safe place</b>.
	   * <p>Usage Example: Service(".store", "store_sample", "client_secrets.json"); </p>
	   */
	  public AuthorizedService (String baseDir, String sampleDir, String secretsFile) {
		 
		  // Store base and sample directories.
		  base_dir = baseDir;
		  
		  // Store client secrets file name.
		  clientSecretsFile = secretsFile;
		  
		  // Determine data directory name.
		  if (OS.startsWith("Windows"))
			  data_dir = base_dir.concat("\\" + sampleDir);
	        else 
	        	if (OS.startsWith("Mac"))
	        		data_dir = base_dir.concat("/" + sampleDir);
	        	
		  // Create a new File instance from a parent pathname string and a child pathname string.
		  DATA_STORE_DIR =
				  new java.io.File(home_dir, data_dir);
	  }
	  
	 
	  /**
	   * Calculate the absolute path of the client secrets file.
	   * @param dir The name of the directory where the file resides.
	   * @param fileName The name of the file.
	   * @return The absolute path of the file.
	   */
	  private static String getAbsoluteFilePath () {
		
        String filePath = null;
        
        if (OS.startsWith("Windows"))
        	filePath = home_dir.concat("\\" + data_dir + "\\" + clientSecretsFile);
        else 
        	 if (OS.startsWith("Mac"))
        		 filePath = home_dir.concat("/" + data_dir + "/" + clientSecretsFile);
        
        // Test 
        /*System.out.println(String.format("Home dir: %s", home_dir));
        System.out.println(String.format("OS: %s", OS));
        System.out.println(String.format("File path: %s", filePath));*/
       
        return filePath;
        
	}
		
	  
	/**
	 * Authorize the installed application to access user's protected data. 
	 * The authorization is based on the user's ID and secret associated
	 * with the project enabled to use the Google Drive REST API.
	 * @param dir The directory that contains the file with the client secrets.
	 * @param clientSecretsFile The json file that contains the client secrets.
	 * @return appCredentials The credential for the application.
	 * @throws Exception
	 ***/
	private  Credential authorize(String serviceScopes) throws Exception {
	   
		// Get client secrets absolute file path.
		String filePath = getAbsoluteFilePath();
		
		// Load client secrets from the file where they are stored.
		InputStream inputStream = new FileInputStream(filePath);
		
		InputStreamReader secrets = new InputStreamReader(inputStream);
		
		GoogleClientSecrets clientSecrets = null;
		
	    try {
	    	clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, secrets);
		} catch (Exception e) {
			String msg = String.format("Error occurred: %s", e.getMessage());
			System.out.println(msg);
		}
	    
	    if (clientSecrets.getDetails().getClientId().startsWith("Enter")
	        || clientSecrets.getDetails().getClientSecret().startsWith("Enter ")) {
	      System.out.println(
	          "Enter Client ID and Secret from https://code.google.com/apis/console/?api=storage_api "
	          + "into client_secrets.json");
	      System.exit(1);
	    }
	    
	    GoogleAuthorizationCodeFlow flow = null;
	    Credential appCredentials = null;
			
		try{
			// Set authorization flow. 
		    flow = new GoogleAuthorizationCodeFlow.Builder(
		    		httpTransport, JSON_FACTORY, clientSecrets,
			        Collections.singleton(serviceScopes)).setDataStoreFactory(
			        dataStoreFactory).build();
		    if (flow != null) {
				// Set authorization flow. 
				flow = new GoogleAuthorizationCodeFlow.Builder(
			    		httpTransport, JSON_FACTORY, clientSecrets,
				        Collections.singleton(serviceScopes)).setDataStoreFactory(
				        dataStoreFactory).build();
		
				// Authorize
				VerificationCodeReceiver rcv = new LocalServerReceiver();
				appCredentials = 
					new AuthorizationCodeInstalledApp(flow, rcv).authorize("user");
		    
		    }
		}
		catch (IOException e){
			System.out.println(String.format("%s", e.getMessage()));
		}
		
		// Return the application credentials.
		return appCredentials;	    
	
	}
	

	/**
	 * Create authorized service to allow the application to use the
	 * Google service REST API.
	 * @param serviceType The service name such as: storage, drive. 
	 * @return The authorized service object.
	 */
	 public Object getAuthorizedService(String serviceName) {
		 
		  Object service = null;
		  
		  try {
				 
				/* Returns a new instance of {@link NetHttpTransport} 
			  	 * which uses GoogleUtils.getCertificateTrustStore() 
				 * for the trusted certificates using NetHttpTransport.Builder.trustCertificates(KeyStore).
				 ***/
				httpTransport = GoogleNetHttpTransport.newTrustedTransport();
				 
				 // For security purposes, the file's permissions are set to be accessible only 
				 // by the file's owner.				
				dataStoreFactory = new FileDataStoreFactory(DATA_STORE_DIR);
				
				
				
				// Create authorized service instance.
				Credential appCredential = null;
				service = null;
				String serviceScopes = "";
				
				switch(serviceName.toLowerCase()) {
				
					case "storage": {
						// Obtain the credential for the application
						serviceScopes = StorageScopes.DEVSTORAGE_FULL_CONTROL;
						appCredential = authorize(serviceScopes);
						service = new Storage.Builder(httpTransport, JSON_FACTORY, appCredential).setApplicationName(
						    		APPLICATION_NAME).build();
						break;
					}
					case "drive": {
						// Obtain the credential for the application
						serviceScopes = DriveScopes.DRIVE;
						appCredential = authorize(serviceScopes);
						service = new Drive.Builder(httpTransport, JSON_FACTORY, appCredential).setApplicationName(
						    		APPLICATION_NAME).build();
						break;
					}

					default: {
						System.out.println(String.format("Service %s is not allowed.", serviceName));
						System.out.println(String.format("Allowed services are %s, %s", "storage", "drive"));
						break;
					}
				}
					
			} 
			catch (GoogleJsonResponseException e) {
				// An error came back from the API.
			      GoogleJsonError error = e.getDetails();
			      System.err.println(error.getMessage());
			      // More error information can be retrieved with error.getErrors().
			} 
			catch (HttpResponseException e) {
			      // No JSON body was returned by the API.
			      System.err.println(e.getHeaders());
			      System.err.println(e.getMessage());
			} 
			catch (IOException e) {
			      // Error formulating a HTTP request or reaching the HTTP service.
			      System.err.println(e.getMessage());
			} 
			catch (Throwable t) {
			      t.printStackTrace();
			}
		  
		  return service;
	  }
	 		 
	 
}
