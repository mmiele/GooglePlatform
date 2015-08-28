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
import com.google.api.client.googleapis.media.MediaHttpUploader;
import com.google.api.client.googleapis.media.MediaHttpUploaderProgressListener;
import com.google.api.client.http.InputStreamContent;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.YouTubeScopes;
import com.google.api.services.youtube.model.Video;
import com.google.api.services.youtube.model.VideoListResponse;
import com.google.api.services.youtube.model.VideoSnippet;
import com.google.api.services.youtube.model.VideoStatus;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Upload a video to the authenticated user's channel. Use OAuth 2.0 to
 * authorize the request. Note that you must add your video files to the
 * project folder to upload them with this application.
 *
 * @author milexm@gmail.com
 */
public class VideoActions {

	 /**
     * Define a global instance of a Youtube object, which will be used
     * to make YouTube Data API requests.
     */
    private static YouTube youtubeService;
    
    		
    /**
     * Define a global variable that specifies the MIME type of the video
     * being uploaded.
     */
    private static final String VIDEO_FILE_FORMAT = "video/*";

    // This file must be already present in the resources folder.
    // Change the name accordingly.
    private static final String SAMPLE_VIDEO_FILENAME = "slackingoff.mp4";

    
    /*
     *** Utilities  
     */
    
    /*
     *  Add extra information to the video before uploading.
     */
    private static Video addVideoInformation() {
    	 
        Video videoObjectDefiningMetadata = new Video();

        // Set the video to be publicly visible. This is the default
        // setting. Other supporting settings are "unlisted" and "private."
        VideoStatus status = new VideoStatus();
        status.setPrivacyStatus("public");
        videoObjectDefiningMetadata.setStatus(status);

        // Most of the video's metadata is set on the VideoSnippet object.
        VideoSnippet snippet = new VideoSnippet();

        // This code uses a Calendar instance to create a unique name and
        // description for test purposes so that you can easily upload
        // multiple files. You should remove this code from your project
        // and use your own standard names instead.
        Calendar cal = Calendar.getInstance();
        snippet.setTitle("Tinking of You uploaded on " + cal.getTime());
        snippet.setDescription(
                "Video uploaded via YouTube Data API V3 " + "on " + cal.getTime());

        // Set the keyword tags that you want to associate with the video.
        List<String> tags = new ArrayList<String>();
        tags.add("Testing API");
        tags.add("example");
        tags.add("java");
        tags.add("YouTube Data API V3");
        tags.add("erase me");
        snippet.setTags(tags);

        // Add the completed snippet object to the video resource.
        videoObjectDefiningMetadata.setSnippet(snippet);
    	
        return videoObjectDefiningMetadata;
    }
    
    /*
     * Display uploaded video information. 
     */
    private static void displayVideoInformation(Video uploadedVideo) {
    	StringBuffer buffer = new StringBuffer();
    
    	buffer.append(String.format("%n================== Returned Video ==================%n"));
    	buffer.append(String.format("  - Id:             %s %n", uploadedVideo.getId()));
    	buffer.append(String.format("  - Title:          %s %n", uploadedVideo.getSnippet().getTitle()));
    	buffer.append(String.format("  - Tags:           %s %n", uploadedVideo.getSnippet().getTags()));
    	buffer.append(String.format("  - Privacy Status: %s %n", uploadedVideo.getStatus().getPrivacyStatus()));
    	buffer.append(String.format("  - Video Count:    %s %n", uploadedVideo.getStatistics().getViewCount()));
    	
    	System.out.println(buffer.toString());
    }
    
    public static void initVideoActions(YouTube authorizedService) {
		
 		// Initialize authorized youtube service.
     	youtubeService = authorizedService;
 		
 	}
     
    
    /**
     * Upload the user-selected video to the user's YouTube channel. The code
     * looks for the video in the application's project folder and uses OAuth
     * 2.0 to authorize the API request.
     *
     */
    public static void performUpload() {

    	try {
        		// Get the authenticated service with the proper scope. This is needed because the
    			// application uses different scopes.
        		youtubeService=	OAuthUtilities.getAuthorizedService(YouTubeScopes.YOUTUBE_UPLOAD);
            
	            System.out.println("Uploading: " + SAMPLE_VIDEO_FILENAME);
	
	            // Add extra information to the video before uploading.
	            Video videoObjectDefiningMetadata = addVideoInformation();
	
	            // Handle the transfer of data from an input stream to an output stream. 
	            InputStreamContent mediaContent = new InputStreamContent(VIDEO_FILE_FORMAT,
	                    VideoActions.class.getResourceAsStream("/" + SAMPLE_VIDEO_FILENAME));
	
	            // Insert the video. The command sends three arguments. The first
	            // specifies which information the API request is setting and which
	            // information the API response should return. The second argument
	            // is the video resource that contains metadata about the new video.
	            // The third argument is the actual video content.
	            YouTube.Videos.Insert videoInsert = youtubeService.videos()
	                    .insert("snippet,statistics,status", videoObjectDefiningMetadata, mediaContent);
	
	            // Set the upload type and add an event listener.
	            MediaHttpUploader uploader = videoInsert.getMediaHttpUploader();
	
	            // Indicate whether direct media upload is enabled. A value of
	            // "True" indicates that direct media upload is enabled and that
	            // the entire media content will be uploaded in a single request.
	            // A value of "False," which is the default, indicates that the
	            // request will use the resumable media upload protocol, which
	            // supports the ability to resume an upload operation after a
	            // network interruption or other transmission failure, saving
	            // time and bandwidth in the event of network failures.
	            uploader.setDirectUploadEnabled(false);
	
	            MediaHttpUploaderProgressListener progressListener = new MediaHttpUploaderProgressListener() {
	                public void progressChanged(MediaHttpUploader uploader) throws IOException {
	                    switch (uploader.getUploadState()) {
	                        case INITIATION_STARTED:
	                            System.out.println("Initiation Started");
	                            break;
	                        case INITIATION_COMPLETE:
	                            System.out.println("Initiation Completed");
	                            break;
	                        case MEDIA_IN_PROGRESS:
	                            System.out.println("Upload in progress");
	                            System.out.println("Upload percentage: " + uploader.getProgress());
	                            break;
	                        case MEDIA_COMPLETE:
	                            System.out.println("Upload Completed!");
	                            break;
	                        case NOT_STARTED:
	                            System.out.println("Upload Not Started!");
	                            break;
	                    }
	                }
	            };
	            uploader.setProgressListener(progressListener);
	
	            // Call the API and upload the video.
	            Video uploadedVideo = videoInsert.execute();
	
	            // Print data about the newly inserted video from the API response.
	            displayVideoInformation(uploadedVideo);
	            
	
	        } catch (GoogleJsonResponseException e) {
	        		int httpError = e.getDetails().getCode();
	          
	        		if (httpError == 403) {
	        			System.err.println("Do someting to fix 403");
	        			// Usually this error is caused by the wrong scope contained in the StoredCredential file.
	                	// Get the authenticated service with the proper scope. This is needed because the
	        			// application uses different scopes. For more information, 
	                	// see <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html">access forbidden</a>
	                	youtubeService = OAuthUtilities.getAuthorizedService(YouTubeScopes.YOUTUBE_UPLOAD);
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
    
    /*
     *** Utilities ***
     */
    /*
     * Prompt the user to enter a keyword tag.
     */
    private static String getTagFromUser() throws IOException {

        String keyword = "";

        System.out.print("Please enter a tag for your video: ");
        BufferedReader bReader = new BufferedReader(new InputStreamReader(System.in));
        keyword = bReader.readLine();

        if (keyword.length() < 1) {
            // If the user doesn't enter a tag, use the default value.
            keyword = "SlackingOff";
        }
        return keyword;
    }

    /*
     * Prompt the user to enter a video ID.
     */
    private static String getVideoIdFromUser() throws IOException {

        String videoId = "";

        System.out.print("Please enter a video Id to update: ");
        BufferedReader bReader = new BufferedReader(new InputStreamReader(System.in));
        videoId = bReader.readLine();

        if (videoId.length() < 1) {
            // Set default value.
        	videoId = "x0h82NNvmrg"; 
//            System.out.print("Video Id can't be empty!");
//            System.exit(1);
        }

        return videoId;
    }
    /**
     * Update the user-selected video in the user's YouTube channel. The code
     * looks for the video in the application's project folder and uses OAuth
     * 2.0 to authorize the API request.
     *
     */
    public static void performUpdate() {
    	
        try {
	            // Prompt the user to enter the video ID of the video being updated.
	            String videoId = getVideoIdFromUser();
	            System.out.println("You chose " + videoId + " to update.");
	
	            // Prompt the user to enter a keyword tag to add to the video.
	            String tag = getTagFromUser();
	            System.out.println("You chose " + tag + " as a tag.");
	
	            // Call the YouTube Data API's youtube.videos.list method to
	            // retrieve the resource that represents the specified video.
	            YouTube.Videos.List listVideosRequest = youtubeService.videos().list("snippet").setId(videoId);
	            VideoListResponse listResponse = listVideosRequest.execute();
	
	            // Since the API request specified a unique video ID, the API
	            // response should return exactly one video. If the response does
	            // not contain a video, then the specified video ID was not found.
	            List<Video> videoList = listResponse.getItems();
	            if (videoList.isEmpty()) {
	                System.out.println("Can't find a video with ID: " + videoId);
	                return;
	            }
	
	            // Extract the snippet from the video resource.
	            Video video = videoList.get(0);
	            VideoSnippet snippet = video.getSnippet();
	
	            // Preserve any tags already associated with the video. If the
	            // video does not have any tags, create a new array. Append the
	            // provided tag to the list of tags associated with the video.
	            List<String> tags = snippet.getTags();
	            if (tags == null) {
	                tags = new ArrayList<String>(1);
	                snippet.setTags(tags);
	            }
	            tags.add(tag);
	
	            // Update the video resource by calling the videos.update() method.
	            YouTube.Videos.Update updateVideosRequest = youtubeService.videos().update("snippet", video);
	            Video videoResponse = updateVideosRequest.execute();
	
	            // Print information from the updated resource.
	            System.out.println("\n================== Returned Video ==================\n");
	            System.out.println("  - Title: " + videoResponse.getSnippet().getTitle());
	            System.out.println("  - Tags: " + videoResponse.getSnippet().getTags());

        } catch (GoogleJsonResponseException e) {
    		int httpError = e.getDetails().getCode();
	          
    		if (httpError == 403) {
    			System.err.println("Do someting to fix 403");
    			// Usually this error is caused by the wrong scope contained in the StoredCredential file.
            	// Get the authenticated service with the proper scope. This is needed because the
    			// application uses different scopes. For more information, 
            	// see <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html">access forbidden</a>
            	youtubeService = OAuthUtilities.getAuthorizedService(YouTubeScopes.YOUTUBE);
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
}
