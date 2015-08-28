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

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.YouTubeScopes;
import com.google.api.services.youtube.model.Channel;
import com.google.api.services.youtube.model.ChannelListResponse;
import com.google.api.services.youtube.model.PlaylistItem;
import com.google.api.services.youtube.model.PlaylistItemListResponse;
import com.google.api.services.youtube.model.ResourceId;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import com.google.api.services.youtube.model.Thumbnail;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import com.acloudysky.oauth.AuthorizedService;

/**
 * Perform tasks on videos uploaded to the authenticated 
 * user's YouTube channel.
 */
public class VideoInformation {

    /**
     * Define a global instance of a Youtube object, which will be used
     * to make YouTube Data API requests.
     */
    private static YouTube youtubeService;
    

    /**
     * Define a global variable that identifies the name of a file that
     * contains the developer's API key.
     */
    private static final String PROPERTIES_FILENAME = "youtube.properties";

    private static final long NUMBER_OF_VIDEOS_RETURNED = 25;

 
    public static void initVideoInformation(YouTube authorizedService) {
		
		// Initialize authorized youtube service.
    	youtubeService = authorizedService;
		
	}
    
    /*
     *** Utilities ***
     */
    
    /*
     * Print information about the items in the playlist.
     * @param size size of list
     * @param iterator of Playlist Items from uploaded Playlist
     */
    private static void prettyPrint(int size, Iterator<PlaylistItem> playlistEntries) {
    	
    	StringBuffer header = new StringBuffer();
    	StringBuffer results = new StringBuffer();
    	
    	header.append(String.format("=============================================================%n"));
    	header.append(String.format("\t\tTotal Videos Uploaded: %d %n", size));
    	header.append(String.format("=============================================================%n"));
    	System.out.println(header.toString());

        while (playlistEntries.hasNext()) {
            PlaylistItem playlistItem = playlistEntries.next();
            results.append(String.format(" Video name:  %s %n", playlistItem.getSnippet().getTitle()));
            results.append(String.format(" Video ID:    %s %n", playlistItem.getContentDetails().getVideoId()));
            results.append(String.format(" Upload date: %s %n", playlistItem.getSnippet().getPublishedAt().toString()));
            results.append(String.format("-------------------------------------------------------------%n"));
        }
        System.out.println(results.toString());
    }
    
    
    /**
     * Calls the <a href="https://developers.google.com/youtube/v3/docs/channels/list" target="_blank">channels.list</a> 
     * method to retrieve the playlist ID for the list of videos uploaded to the user's channel,
     * Then calls the <a href="https://developers.google.com/youtube/v3/docs/playlistItems/list" target="_blank">playlistItems.list</a> 
     * method to retrieve the list of videos in that playlist.
     */
    public static void listVideos() {

        try {
        	
            // Call the API's channels.list method to retrieve the
            // resource that represents the authenticated user's channel.
            // In the API response, only include channel information needed for
            // this use case. The channel's contentDetails part contains
            // playlist IDs relevant to the channel, including the ID for the
            // list that contains videos uploaded to the channel.
            YouTube.Channels.List channelRequest = youtubeService.channels().list("contentDetails");
            channelRequest.setMine(true);
            channelRequest.setFields("items/contentDetails,nextPageToken,pageInfo");
            ChannelListResponse channelResult = channelRequest.execute();

            List<Channel> channelsList = channelResult.getItems();

            if (channelsList != null) {
                // The user's default channel is the first item in the list.
                // Extract the playlist ID for the channel's videos from the
                // API response.
                String uploadPlaylistId =
                        channelsList.get(0).getContentDetails().getRelatedPlaylists().getUploads();

                // Define a list to store items in the list of uploaded videos.
                List<PlaylistItem> playlistItemList = new ArrayList<PlaylistItem>();

                // Retrieve the playlist of the channel's uploaded videos.
                YouTube.PlaylistItems.List playlistItemRequest =
                		youtubeService.playlistItems().list("id,contentDetails,snippet");
                playlistItemRequest.setPlaylistId(uploadPlaylistId);

                // Only retrieve data used in this application, thereby making
                // the application more efficient. See:
                // https://developers.google.com/youtube/v3/getting-started#partial
                playlistItemRequest.setFields(
                        "items(contentDetails/videoId,snippet/title,snippet/publishedAt),nextPageToken,pageInfo");

                String nextToken = "";

                // Call the API one or more times to retrieve all items in the
                // list. As long as the API response returns a nextPageToken,
                // there are still more items to retrieve.
                do {
                    playlistItemRequest.setPageToken(nextToken);
                    PlaylistItemListResponse playlistItemResult = playlistItemRequest.execute();

                    playlistItemList.addAll(playlistItemResult.getItems());

                    nextToken = playlistItemResult.getNextPageToken();
                } while (nextToken != null);

                // Prints information about the results.
                prettyPrint(playlistItemList.size(), playlistItemList.iterator());
                
                //Test 
                AuthorizedService.getStoredCredentialFileAbsolutePath();
            }

        }  catch (GoogleJsonResponseException e) 
		{
    		int httpError = e.getDetails().getCode();
      
    		if (httpError == 403) {
    			System.err.println("Do someting to fix 403");
    			// Usually this error is caused by the wrong scope contained in the StoredCredential file.
            	// Get the authenticated service with the proper scope. This is needed because the
    			// application uses different scopes. For more information, 
            	// see <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html">access forbidden</a>
            	youtubeService = OAuthUtilities.getAuthorizedService(YouTubeScopes.YOUTUBE_READONLY);
    		}
    		else  
        	 System.err.println("GoogleJsonResponseException code: " + httpError + " : "
	                    + e.getDetails().getMessage());
	            e.printStackTrace();
        }
    	catch (IOException e) {
    		System.err.println("IOException: " + e.getMessage());
    		e.printStackTrace();
    	} 
		catch (Throwable t) {
			System.err.println("Throwable: " + t.getMessage());
			t.printStackTrace();
    	}
    }

    /**
     * Initialize a YouTube object to search for videos on YouTube. Then
     * display the name of each video in the result set.
     */
    public static void searchVideos() {
        // Read the developer key from the properties file.
        Properties properties = new Properties();
        try {
            InputStream in = VideoInformation.class.getResourceAsStream("/" + PROPERTIES_FILENAME);
            properties.load(in);

        } catch (IOException e) {
            System.err.println("There was an error reading " + PROPERTIES_FILENAME + ": " + e.getCause()
                    + " : " + e.getMessage());
            System.exit(1);
        }

        try {
            
	            // Prompt the user to enter a query term.
	            String queryTerm = getInputQuery();
	
	            // Define the API request for retrieving search results.
	            YouTube.Search.List search = youtubeService.search().list("id,snippet");
	
	            // Set your developer key from the {{ Google Cloud Console }} for
	            // non-authenticated requests. See:
	            // {{ https://cloud.google.com/console }}
	            String apiKey = properties.getProperty("youtube.apikey");
	            search.setKey(apiKey);
	            search.setQ(queryTerm);
	
	            // Restrict the search results to only include videos. See:
	            // https://developers.google.com/youtube/v3/docs/search/list#type
	            search.setType("video");
	
	            // To increase efficiency, only retrieve the fields that the
	            // application uses.
	            search.setFields("items(id/kind,id/videoId,snippet/title,snippet/thumbnails/default/url)");
	            search.setMaxResults(NUMBER_OF_VIDEOS_RETURNED);
	
	            // Call the API and print results.
	            SearchListResponse searchResponse = search.execute();
	            List<SearchResult> searchResultList = searchResponse.getItems();
	            if (searchResultList != null) {
	                prettyPrint(searchResultList.iterator(), queryTerm);
	            }
        } catch (GoogleJsonResponseException e) {
            System.err.println("There was a service error: " + e.getDetails().getCode() + " : "
                    + e.getDetails().getMessage());
        } catch (IOException e) {
            System.err.println("There was an IO error: " + e.getCause() + " : " + e.getMessage());
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    /*
     * Prompt the user to enter a query term and return the user-specified term.
     */
    private static String getInputQuery() throws IOException {

        String inputQuery = "";

        System.out.print("Please enter a search term: ");
        BufferedReader bReader = new BufferedReader(new InputStreamReader(System.in));
        inputQuery = bReader.readLine();

        if (inputQuery.length() < 1) {
            // Use the string "YouTube Developers Live" as a default.
            inputQuery = "YouTube Developers Live";
        }
        return inputQuery;
    }

    /*
     * Prints out all results in the Iterator. For each result, print the
     * title, video ID, and thumbnail.
     *
     * @param iteratorSearchResults Iterator of SearchResults to print
     *
     * @param query Search query (String)
     */
    private static void prettyPrint(Iterator<SearchResult> iteratorSearchResults, String query) {

        System.out.println("\n=============================================================");
        System.out.println(
                "   First " + NUMBER_OF_VIDEOS_RETURNED + " videos for search on \"" + query + "\".");
        System.out.println("=============================================================\n");

        if (!iteratorSearchResults.hasNext()) {
            System.out.println(" There aren't any results for your query.");
        }

        while (iteratorSearchResults.hasNext()) {

            SearchResult singleVideo = iteratorSearchResults.next();
            ResourceId rId = singleVideo.getId();

            // Confirm that the result represents a video. Otherwise, the
            // item will not contain a video ID.
            if (rId.getKind().equals("youtube#video")) {
                Thumbnail thumbnail = singleVideo.getSnippet().getThumbnails().getDefault();

                System.out.println(" Video Id" + rId.getVideoId());
                System.out.println(" Title: " + singleVideo.getSnippet().getTitle());
                System.out.println(" Thumbnail: " + thumbnail.getUrl());
                System.out.println("\n-------------------------------------------------------------\n");
            }
        }
    }    
    
    
    
}
