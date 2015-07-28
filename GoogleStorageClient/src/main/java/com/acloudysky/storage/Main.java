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
package com.acloudysky.storage;

import com.acloudysky.storage.DefaultSettings;
import com.acloudysky.storage.SimpleUI;
import com.acloudysky.oauth.AuthorizedService;
import com.google.api.services.storage.Storage;

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
	 * 				args[1] = "Google Storage Console Application"
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
		
		// Get authorization to access Google Storage service.
		Storage storageService = null;
		
		try {
			// Instantiate the Service class.
			AuthorizedService service = 
					new AuthorizedService(".store", "storage_sample", "client_secrets.json");
			
			// Get the authorized service so the application can use its  API.
			storageService = (Storage) service.getAuthorizedService("storage");
			
		}
		catch (Exception e) {
			String.format("Error %s during service authorization.", e.toString());
		}
		

		if (storageService != null) {
			
			/*
			 *  Instantiate the DefaultSettings class.
			 *	We assume that in the user home directory a parent directory exists called ".store".
			 * 	It contains a child directory called "storage_sample", which in turn contains a file 
			 *	"sample_settings.json". This file contains the applications default settings.
			 */
			DefaultSettings defaults = 
					new DefaultSettings(".store", "storage_sample", "sample_settings.json");
			
			// Read current application default values from the JSON file.
			// This amounts to store the default values in memory.
			defaults.readSettings();
			
			// Initialize Storage commands classes
			ProjectCommands.initProjectCommands(storageService, DefaultSettings.getSettings());
			BucketCommands.initBucketCommands(storageService, DefaultSettings.getSettings());
			ObjectCommands.initObjectCommands(storageService, DefaultSettings.getSettings());
			
			// Initialize simple UI and display menu.
			// Pass the authorized service.
			SimpleUI sui = new SimpleUI();
	
			// Process user's input.
			sui.processUserInput();
		}
		else 
			String.format("Error %s", "service object is null.");
				
	}

}

