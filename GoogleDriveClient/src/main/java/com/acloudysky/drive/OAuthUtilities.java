package com.acloudysky.drive;

import com.acloudysky.oauth.AuthorizedService;
import com.google.api.services.drive.Drive;


public class OAuthUtilities {
	
	public static Drive getAuthorizedService(String serviceScopes) {
		
		Drive driveService = null;
				
		try {
			// Instantiate the Service class.
			AuthorizedService service = 
				new AuthorizedService(".store", "drive_sample", "client_secrets.json");
		
			// Get the authorized service so the application can use its  API.
			driveService = (Drive) service.getAuthorizedService("drive", serviceScopes);
			
		}
		catch (Exception e) {
			String.format("Error %s during service authorization.", e.toString());
		}
		return driveService;
		
	}

}
