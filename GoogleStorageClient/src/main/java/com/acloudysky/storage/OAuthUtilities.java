package com.acloudysky.storage;

import com.acloudysky.oauth.AuthorizedService;
import com.google.api.services.storage.Storage;

/**
 * This class contains utility methods to handle authorization tasks.
 * @author ichael
 *
 */
public class OAuthUtilities {
	
	/***
	 * Authorize the application to use Google Storage API. 
	 * <p><b>Note</b>. The method assumes that you already have created a directory to 
	 * store the file with the client secrets. The directory is <b>.store/storage_sample</b>. 
	 * The file containing the secrets is <b>client_secrets.json</b>.
	 * @param serviceScope - The scope for which to obtain the authorization.
	 * The values for the scope can be found at 
	 * <a href="https://developers.google.com/resources/api-libraries/documentation/storage/v1beta2/java/latest/com/google/api/services/storage/StorageScopes.html" target="_blank">Class StorageScopes</a>.
	 * @return The object that represents the authorized service.
	 */
	public static Storage getAuthorizedService(String serviceScope) {
		
		Storage storageService = null;
				
		try {
			
			// Instantiate the Service class.
			AuthorizedService service = 
					new AuthorizedService(".store", "storage_sample", "client_secrets.json");
		
			// Get the authorized service so the application can use its API.
			storageService = (Storage) service.getAuthorizedService("storage", serviceScope);
			
		}
		catch (Exception e) {
			String.format("Error %s during service authorization.", e.toString());
		}
		return storageService;
		
	}

}
