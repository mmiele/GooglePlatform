package com.acloudysky.drive;

import com.google.api.services.drive.Drive;
import com.acloudysky.oauth.AuthorizedService;

/**
 * Contains the main entry for the console application. 
 * @author Michael
 *
 */
public class Main {

	 
	/***
	 * Main entry point which displays the start greetings.
	 * It delegates the execution of method calls, displays the selection menu 
	 * and process user input. 
	 * @see SimpleUI
	 * @param args; args[0] = "Michael"
	 * 				args[1] = "Google Drive Console Application"
	 * 
	 */
	public static void main(String[] args) {
		
		String name, topic;
		
		try {
				name = args[0];
				topic = args[1];
				
				String startGreetings = String.format("Hello %s let's start %s", name, topic);
				System.out.println(startGreetings);
			
		}catch (Exception e) {
			System.out.println("IO error trying to read application input!");
			System.exit(1);
		}
		
		Drive driveService = null;
		
		try {
			// Instantiate the Service class.
			AuthorizedService service = 
					new AuthorizedService(".store", "drive_sample", "client_secrets.json");
			
			// Get the authorized service so the application can use its  API.
			driveService = (Drive) service.getAuthorizedService("drive");
			
		}
		catch (Exception e) {
			String.format("Error %s during service authorization.", e.toString());
		}
		

		if (driveService != null) {
			
			/*
			 *  Instantiate the DefaultSettings class.
			 *	We assume that in the user home directory a parent directory exists called ".store".
			 * 	It contains a child directory called "drive_sample", which in turn contains a file 
			 *	"sample_settings.json". This file contains the applications default settings.
			 */
			DefaultSettings defaults = new DefaultSettings(".store", "drive_sample", "sample_settings.json");
			
			// Read current application default values.
			defaults.readSettings();
			
			// Initialize simple UI and display menu.
			// Pass the authorized service.
			SimpleUI sui = new SimpleUI(driveService);
	
			// Process user's input.
			sui.processUserInput();
		}
		else 
			String.format("Error %s", "service object is null.");
	}
	
}

