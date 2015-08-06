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


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;



/**
 * Displays the menu of choices the user can select from. It processes the
 * user's input and calls the proper method based on the user's selection.
 * Each method calls the related 
 * <a href="https://developers.google.com/api-client-library/java/apis/storage/v1beta2" target="_blank">Cloud Storage REST API</a>.
 * @author Michael.
 *
 */
public class SimpleUI {
	// 
	private StringBuilder menu;
	public static final String newline = System.getProperty("line.separator");

	/**
	 * Initializes the menu that allows the user to make the 
	 * allowed choices.
	 * <p>
	 * It uses a StringBuilder to create the formatted menu </p>
	 */
	SimpleUI() {
		menu = new StringBuilder();
		menu.append(String.format("Select one of the following options:%n"));	
		menu.append(String.format("%n%s List buckets.", "p1"));
		menu.append(String.format("%n%s Create new bucket.", "b0"));
		menu.append(String.format("%n%s Get specified bucket.", "b1"));
		menu.append(String.format("%n%s List objects in a specified bucket", "b2"));
		menu.append(String.format("%n%s Get specified object.", "o1"));
		menu.append(String.format("%n%s Upload object.", "o2"));
		
		menu.append(String.format("%n%s  Display menu.", "m"));
		menu.append(String.format("%n%s  Exit application.", "x"));
		// menu.append(newline);	
		
		// Display menu
		System.out.println(menu.toString());
	}
	
	/*
	 * Read user input.
	 */
	private static String readUserInput(String msg) {
		
		// Open standard input.
		BufferedReader br = new BufferedReader(new java.io.InputStreamReader(System.in));

		String selection = null;

		//  Read the selection from the command-line; need to use try/catch with the
		//  readLine() method
		try {
			if (msg == null)
				System.out.print("\n>>> ");
			else
				System.out.print("\n" + msg);
			selection = br.readLine();
		} catch (IOException e) {
			System.out.println("IO error trying to read your input!");
			System.out.println(String.format("%s", e.getMessage()));
			System.exit(1);
		}
		
		return selection;

	}
	/**
	 * Gets user selection and calls the related method.
	 */
	public void processUserInput() {
		
		String in = null;
		while (true) {
			
			// Get user input.
			String selection = readUserInput(null).toLowerCase();	
			
			try{
				// Exit the application.
				if ("x".equals(selection))
					break;
				else
					if ("m".equals(selection)) {
						// Display menu
						displayMenu();
						continue;
					}
					else 
						// Read the input string.
						in = selection.trim();
	
			}
			catch (Exception e){
				// System.out.println(e.toString());
				System.out.println(String.format("Input %s is not allowed%n", selection));
				continue;
			}
			
			// Select action to perform.
			switch(in) {
			
				case "p1": {
				
					try{
						// List the project buckets
						ProjectCommands.listBuckets();
					}
					catch (IOException e){
						System.out.println(String.format("%s", e.getMessage()));
					}
					break;
				}
				
				case "b0": {
					try{
						String bucketName = readUserInput("Bucket name or just enter for default: ").toLowerCase();	
						boolean created = BucketCommands.insertBucket(bucketName);
						if (created)
							BucketCommands.getBucket(bucketName);
					}
					catch (Exception e){
						System.out.println(String.format("%s", e.getMessage()));
					}
					break;
				}
				case "b1": {
					
					try{
						String bucketName = readUserInput("Bucket name or just enter for default: ").toLowerCase();	
						if (bucketName == null)
							BucketCommands.getBucket(null);
						else
							BucketCommands.getBucket(bucketName);
					}
					catch (Exception e){
						System.out.println(String.format("%s", e.getMessage()));
					}
					break;
				}
				case "b2": {
					
					try{
						
						String bucketName = 
								readUserInput("Bucket name or just enter for default: ").toLowerCase();	
						
						ObjectCommands.listObjects(bucketName);
						
					}
					catch (Exception e){
						System.out.println(String.format("%s", e.getMessage()));
					}
					break;
				}
				
				case "o1": {
					
					try{
						String bucketName, objectName;
						// Get bucket and object names from the user.
						bucketName = readUserInput("Bucket name or just enter for default: ").toLowerCase();	
						objectName = readUserInput("Bucket name or just enter for default: ").toLowerCase();	
						
						// Call the object method.
						ObjectCommands.getObject(bucketName, objectName);
					}
					catch (Exception e){
						System.out.println(String.format("%s", e.getMessage()));
					}
					break;
				}
				
				case "o2": {
					
					try{
						ObjectCommands.uploadObject(true);;
					}
					catch (Exception e){
						System.out.println(String.format("%s", e.getMessage()));
					}
					break;
				}
				
				
				default: {
					System.out.println(String.format("%s is not allowed", selection));
					break;
				}
			}
					
		}
		SimpleUI.Exit();
		
	}

	public void displayMenu() {
		
		System.out.println(menu.toString());
		
	}
	
	private static void Exit() {
		System.out.println("Bye!\n");
		return;
	}
}
