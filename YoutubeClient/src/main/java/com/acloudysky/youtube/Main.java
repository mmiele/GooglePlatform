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
package com.acloudysky.youtube;


import java.io.IOException;

import com.google.api.services.youtube.YouTubeScopes;
import com.google.api.services.youtube.YouTube;


import com.acloudysky.youtube.SimpleUI;
import com.acloudysky.youtube.OAuthUtilities;

/**
 * Contains the Main class for the YoutubeClient console application. 
 * @author Michael
 *
 */
public class Main {

	/***
     * Application entry point which displays the start greetings and performs the 
     * following main tasks:
     * <ul>
     *      <li>Gets authorization to access Youtube service.</li> 
     *		<li>Reads the default settings.</li>
     * 		<li>Instantiates the local classes.</li>
	 * 		<li>Delegates to the SimpleUI class the display of the selection 
	 *         menu and the processing of the user's input.</li>
	 * </ul>
	 * @see SimpleUI#SimpleUI()
	 * @see VideoActions#initVideoActions(YouTube)
	 * @see VideoInformation#initVideoInformation(YouTube)
	 * @param args; 
	 *  args[0] = Your name
	 *  args[1] = Your message in quotes
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
				topic = "Google Youtube Console Application";
			}
			else {
				System.out.println("IO error trying to read application input!");
				System.exit(1); 
			}
		}
		
		// Display greeting message.
		String startGreetings = String.format("Hello %s let's start %s", name, topic);
		System.out.println(startGreetings);	
		
		// Get the authenticated service with the default minimal scope. 
		YouTube youtubeService = 
				OAuthUtilities.getAuthorizedService(YouTubeScopes.YOUTUBE_READONLY);
		
		
		if (youtubeService != null) {
			
			/*
			 *  Instantiate the DefaultSettings class.
			 *	We assume that in the user home directory a parent directory exists called ".store".
			 * 	It contains a child directory called "drive_sample", which in turn contains a file 
			 *	"sample_settings.json". This file contains the applications default settings.
			 */
			// DefaultSettings defaults = new DefaultSettings(".store", "[servicename]_sample", "sample_settings.json");
			
			// Read current application default values.
			// defaults.readSettings();

			// Initializes the classes that contains methods to interact with Youtube service.
			VideoInformation.initVideoInformation(youtubeService);
			VideoActions.initVideoActions(youtubeService);
			
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
