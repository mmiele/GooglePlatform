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

import com.acloudysky.oauth.AuthorizedService;
import com.google.api.services.youtube.YouTube;


public class OAuthUtilities {
	
	// serviceScopes = YouTubeScopes.YOUTUBE_READONLY;
	// currentServiceScopes = YouTubeScopes.YOUTUBE_UPLOAD;
	
	public static YouTube getAuthorizedService(String serviceScopes) {
		
		YouTube youtubeService = null;
				
		try {
			
			// Instantiate the Service class.
			AuthorizedService service = 
					new AuthorizedService(".store", "youtube_sample", "client_secrets.json");
		
			// Get the authorized service so the application can use its API.
			youtubeService = (YouTube) service.getAuthorizedService("youtube", serviceScopes);
			
		}
		catch (Exception e) {
			String.format("Error %s during service authorization.", e.toString());
		}
		return youtubeService;
		
	}

}
