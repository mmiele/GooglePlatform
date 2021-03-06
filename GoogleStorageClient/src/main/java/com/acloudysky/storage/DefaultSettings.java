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

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import com.google.api.client.json.GenericJson;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.Key;


/*** 
 * Read the current example settings from the predefined JSON file.
 * The file contains information such as project ID, default bucket name and so on.
 * The following is an example of the JSON formatted information:
 * <pre>
 *{
 * "defaultbucket": "my_toycars",
 * "defaultobject": "luigi.jpg",
 * "prefix": "myself",
 * "email": "me@gmail.com",
 * "domain": "acloudysky.com"
 *}
 * </pre>
 * @author Michael
 *
 */

public  final class DefaultSettings extends GenericJson {
	
	// This is the Google Cloud Service project ID 
	// to use in the example.
	@Key("project")
	private String project;

	// The following declarations allows to the JSON information 
	// contained in the default settings file.
	@Key("defaultbucket")
	private String bucket;

	@Key("defaultobject")
	private String object;
	
	@Key("prefix")
	private String prefix;

	@Key("email")
	private String email;

	@Key("domain")
	private String domain;

	public String getProject() {
		return project;
	}

	public String getBucket() {
		return bucket;
	}

	public String getObject() {
		return object;
	}
	
	public String getPrefix() {
		return prefix;
	}

	public String getEmail() {
		return email;
	}

	public String getDomain() {
		return domain;
	}
  
	// Create an instance of the JSON factory. 
  	private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

  	// Current settings
  	private static DefaultSettings settings;
  	
  	// Directory and file where client secrets are kept. 
 	private  static String parent_dir, data_dir;
 	private  static String defaults_file;
 	
 	// Get the current computer OS and the user home directory. 
 	private  static String OS = System.getProperty("os.name");
    private  static String home_dir = System.getProperty("user.home");
  
	public static DefaultSettings getSettings() {
		return settings;
	}
  
	/***
	 * Default constructor to allow creation of an instance 
	 * of class com.acloudysky.drive.DefaultSettings in the creation of the settings object 
	 * through the jasonFactory.
	 * @see #readSettings() readSettings.
	 */
	public DefaultSettings() {
		
	}
	
	/***
	 * Create a DefaultSetings object.  
	 * @param parentDir The parent directory. 
	 * @param sampleDir The directory containing the default settings file.
	 * @param defaultsFile The default settings file.
	 */
	public DefaultSettings(String parentDir, String sampleDir, String defaultsFile) {
		
		parent_dir = parentDir;
		defaults_file = defaultsFile;
		
		// Determine data directory name.
		if (OS.startsWith("Windows"))
			data_dir = parent_dir.concat("\\" + sampleDir);
	    else 
	    	if (OS.startsWith("Mac"))
	    		data_dir = parent_dir.concat("/" + sampleDir);
		  
	}
	
	/**
	 * Calculate the absolute path of the default settings file.
	 * @param dir The name of the directory where the file resides.
	 * @param fileName The name of the file.
	 * @return The absolute path of the file.
	 */
	private static String getAbsoluteFilePath () {
	
		String filePath = null;
  
		if (OS.startsWith("Windows"))
			filePath = home_dir.concat("\\" + data_dir + "\\" + defaults_file);
		else 
			if (OS.startsWith("Mac"))
				filePath = home_dir.concat("/" + data_dir + "/" + defaults_file);
	
		// Test 
		/*System.out.println(String.format("Home dir: %s", home_dir));
		System.out.println(String.format("OS: %s", OS));
		System.out.println(String.format("File path: %s", filePath));*/
	 
		return filePath;
	}
	
	/***
	 * Read sample settings contained in the supporting <i>defaults_file</i>.
	 * <p>
	 * 	<b>Note</b>. This method uses {@link com.google.api.client.json.JsonFactory} to create
	 * a DefaultSettings object to read the JSON formatted information.
	 * </p>
	 */
	public void readSettings() {
		JsonFactory jsonFactory = JSON_FACTORY;
	
		// Load application default settings from the "sample_settings.json" file
		String filePath = getAbsoluteFilePath();
		
		try {
				InputStream inputStream = new FileInputStream(filePath);
				// Create settings object to access the default settings.
				settings = jsonFactory.fromInputStream(inputStream, DefaultSettings.class);
	      } catch (IOException e) {
	        String msg = String.format("Error occurred; %s", e.getMessage());
	        System.out.println(msg);
	      }
		if (settings.getProject().startsWith("Enter ") || settings.getProject().startsWith("Enter ")) {
			System.out.println("Enter sample settings into "
					+ defaults_file);
			System.exit(1);
		}
	} 
}

