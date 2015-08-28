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

/**
 * This class contains utility methods to handle authorization tasks.
 * @author michael
 *
 */
public class OAuthUtilities {
	
	// serviceScopes = YouTubeScopes.YOUTUBE_READONLY;
	// currentServiceScopes = YouTubeScopes.YOUTUBE_UPLOAD;
	
	/***
	 * Authorizes the application to use Youtube API. 
	 * <p><b>Note</b>. The method assumes that you already have created a directory to 
	 * store the file with the client secrets. The directory is .store/youtube_sample. 
	 * The file containing the secrets is client_secrets.json.
	 * @param serviceScope - The scope for which to obtain the authorization.
	 * The values for the scope can be found at 
	 * <a href="https://developers.google.com/resources/api-libraries/documentation/youtube/v3/java/latest/com/google/api/services/youtube/YouTubeScopes.html" target="_blank">Class YouTubeScopes</a>.
	 * @return The object that represents the authorized service.
	 */
	public static YouTube getAuthorizedService(String serviceScope) {
		
		YouTube youtubeService = null;
				
		try {
			
			// Instantiate the Service class.
			// Important. This method assumes that you have created the 
			// .store/youtube_sample/client_secrets.json file.
			AuthorizedService service = 
					new AuthorizedService(".store", "youtube_sample", "client_secrets.json");
		
			// Get the authorized service so the application can use its API.
			youtubeService = (YouTube) service.getAuthorizedService("youtube", serviceScope);
			
		}
		catch (Exception e) {
			String.format("getAuthorizedService error %s.", e.toString());
		}
		return youtubeService;
		
	}

}
