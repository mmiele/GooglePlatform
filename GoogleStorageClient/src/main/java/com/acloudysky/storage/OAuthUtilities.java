package com.acloudysky.storage;

import com.acloudysky.oauth.AuthorizedService;
import com.google.api.services.storage.Storage;


public class OAuthUtilities {
	
	public static Storage getAuthorizedService(String serviceScopes) {
		
		Storage storageService = null;
				
		try {
			
			// Instantiate the Service class.
			AuthorizedService service = 
					new AuthorizedService(".store", "storage_sample", "client_secrets.json");
		
			// Get the authorized service so the application can use its API.
			storageService = (Storage) service.getAuthorizedService("storage", serviceScopes);
			
		}
		catch (Exception e) {
			String.format("Error %s during service authorization.", e.toString());
		}
		return storageService;
		
	}

}
