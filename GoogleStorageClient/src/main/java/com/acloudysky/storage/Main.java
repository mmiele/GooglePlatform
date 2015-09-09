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

import com.acloudysky.storage.DefaultSettings;
import com.acloudysky.storage.SimpleUI;
import com.google.api.services.storage.Storage;
import com.google.api.services.storage.StorageScopes;

/**
 * Contains the Main class for the GoogleStorageClient console application. 
 * @author Michael
 *
 */
public class Main {

	 
	/***
     * Application entry point which displays the start greetings and performs the 
     * following main tasks:
     * <ul>
     *      <li>Gets authorization to access Google Storage service.</li> 
     *		<li>Reads the default settings.</li>
     * 		<li>Instantiates the command classes.</li>
	 * 		<li>Delegates to the SimpleUI class the display of the selection 
	 *         menu and the processing of the user's input.</li>
	 * </ul>
	 * @see DefaultSettings#DefaultSettings(String, String, String) 
	 * @see ProjectCommands#initProjectCommands(Storage, DefaultSettings)
     * @see BucketCommands#initBucketCommands(Storage, DefaultSettings)
     * @see ObjectCommands#initObjectCommands(Storage, DefaultSettings)
     * @see SimpleUI#SimpleUI()
	 * @param args; args[0] = Your name
	 *  			args[1] = Your message in quotes
	 * 
	 */
	public static void main(String[] args) {
	
		String name = null, topic = null;
		
		// Read input parameters.
		try {
				name = args[0];
				topic = args[1];
		}
		catch (Exception e) {
			System.out.println("IO error trying to read application input! Assigning default values.");
			// Assign default values if none are passed.
			if (args.length==0) {
				name = "Michael";
				topic = "Google Storage Console Application";
			}
			else {
				System.out.println("IO error trying to read application input!");
				System.exit(1); 
			}
		}
		
		String startGreetings = String.format("Hello %s let's start %s", name, topic);
		System.out.println(startGreetings);	
		
		
		// Get authorization to access Google Storage service.
		Storage storageService = 
				OAuthUtilities.getAuthorizedService(StorageScopes.DEVSTORAGE_FULL_CONTROL);
		

		if (storageService != null) {
			
			/*
			 *  Instantiate the DefaultSettings class.
			 *	We assume that a parent directory called ".store" exists in the user home directory.
			 * 	This directory contains a child directory called "storage_sample", which in turn contains a file 
			 *	"sample_settings.json". This file contains the application default settings.
			 */
			DefaultSettings defaults = 
					new DefaultSettings(".store", "storage_sample", "sample_settings.json");
			
			// Read application default values from the JSON file.and store them in memory.
			defaults.readSettings();
			
			// Initialize Storage commands classes.
			ProjectCommands.initProjectCommands(storageService, DefaultSettings.getSettings());
			BucketCommands.initBucketCommands(storageService, DefaultSettings.getSettings());
			ObjectCommands.initObjectCommands(storageService, DefaultSettings.getSettings());
			
			// Initialize simple UI and display menu.
			SimpleUI sui = new SimpleUI();
	
			// Process user's input.
			sui.processUserInput();
		}
		else 
			String.format("Error %s", "service object is null.");
				
	}

}

