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
import com.google.api.services.youtube.model.VideoSnippet;
import com.google.api.services.youtube.model.VideoStatus;



import java.io.IOException;
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
public class UploadVideo {

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
    
    /**
     * Upload the user-selected video to the user's YouTube channel. The code
     * looks for the video in the application's project folder and uses OAuth
     * 2.0 to authorize the API request.
     *
     * @param args command line args (not used).
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
	                    UploadVideo.class.getResourceAsStream("/" + SAMPLE_VIDEO_FILENAME));
	
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
	            System.err.println("GoogleJsonResponseException code: " + httpError + " : "
	                    + e.getDetails().getMessage());
	            e.printStackTrace();
	            if (httpError == 403) {
	            	 System.err.println("Do someting to fix 403");
	            	// Re-authorize the request.
	             	youtubeService=	OAuthUtilities.getAuthorizedService(YouTubeScopes.YOUTUBE_UPLOAD);
	            }
            
        } catch (IOException e) {
            System.err.println("IOException: " + e.getMessage());
            e.printStackTrace();
        } catch (Throwable t) {
            System.err.println("Throwable: " + t.getMessage());
            t.printStackTrace();
        }
    }
}