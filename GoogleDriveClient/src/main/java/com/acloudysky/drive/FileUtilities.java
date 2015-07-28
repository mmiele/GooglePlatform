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
package com.acloudysky.drive;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;

public class FileUtilities {
	
	/*
	 * Gets the absolute path of the files used in the 
	 * I/O stream examples.
	 * The files directory is assumed to be /test/.
	 * @param fileName - the name of the file for which to get the absolute path.
	 * @return - the absolute path of the file.
	 */
	/**
	 * Calculate the absolute path of a file.
	 * @param dir The name of the directory where the file resides.
	 * @param fileName The name of the file.
	 * @return The absolute path of the file.
	 */
	public static String getAbsoluteFilePath (String dir, String fileName) {
		
		// Get the current user home directory.
        String home_dir = System.getProperty("user.home");
        String OS = System.getProperty("os.name");
        
    	String filePath = null;
    	  
		if (OS.startsWith("Windows"))
			filePath = home_dir.concat("\\" + dir + "\\" + fileName);
		else 
			if (OS.startsWith("Mac"))
				filePath = home_dir.concat("/" + dir + "/" + fileName);
	
       
        // Test 
        // System.out.println(String.format("File path: %s", filePath));
       
        return filePath;
        
	}

	
	/**
	 * Copy an input stream to a file.
	 * @param inStream The input stream to read from.
	 * @param dir The name of the file directory.
	 * @param fileName The name of the file to write to.
	 * @throws IOException An I/O error has been detected.
	 */
	public static void copyFileFromInputStream (InputStream inStream, 
			String dir, String fileName) throws IOException {
		   
		String outFilePath = getAbsoluteFilePath(dir, fileName);
		BufferedReader in = new BufferedReader(new InputStreamReader(inStream));
	    
		PrintWriter outputStream = null;
        
        try {
			
			/* Instantiate the output buffer line stream. */
			outputStream = new PrintWriter(new FileWriter(outFilePath));
			 String line;
		        while ((line = in.readLine()) != null) {
		        	outputStream.println(line);
		        	// Test
		            System.out.println((line));
		        }
		} 
        catch (IOException e) {
        	System.out.println(String.format("Error occured: %s", e.getMessage()));
		}
		finally {
			if (outputStream != null) {
				outputStream.close();
			}
		}	
	}


	/**
	 * Copy an existing file to a new file.
	 * @param dir The name of the directory where the files reside.
	 * @param inFileName The name of the file to copy from.
	 * @param outFileName The name of the file to copy to.
	 * @throws IOException An I/O error has been detected.
	 */
	public static void copyFile (String dir, String inFileName, 
			String outFileName) throws IOException {
		   
		BufferedReader inputStream = null;
		PrintWriter outputStream = null;
		
		String inFilePath = getAbsoluteFilePath(dir, inFileName);
	    String outFilePath = getAbsoluteFilePath(dir, outFileName);
	
		try {
			
			/* Instantiate the input buffer line stream. */
			inputStream = new BufferedReader(new FileReader(inFilePath));
	
			/* Instantiate the output buffer line stream. */
			outputStream = new PrintWriter(new FileWriter(outFilePath));
	
			String l;
			while ((l = inputStream.readLine()) != null) {
				outputStream.println(l);
	     }
		} 
		catch (IOException e) {
	        	System.out.println(String.format("Error occured: %s", e.getMessage()));
		}  
		finally {
			if (inputStream != null) {
				inputStream.close();
			}
			if (outputStream != null) {
				outputStream.close();
			}
		}	
	}



}
