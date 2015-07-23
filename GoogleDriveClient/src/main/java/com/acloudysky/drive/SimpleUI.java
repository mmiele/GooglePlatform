package com.acloudysky.drive;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import java.net.URL;


import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;



/**
 * Displays the menu of choices the user can select from. It processes the
 * user's input and calls the proper method based on the user's choice.
 * @see FileReader and @see FileWriter. 
 * 
 * @author Michael.
 *
 */
public class SimpleUI {
	// 
	private StringBuilder menu;
	public static final String newline = System.getProperty("line.separator");
	private static FileCommands files;

	/**
	 * Initializes the menu that allows the user to make the 
	 * allowed choices.
	 * <p>
	 * It uses a StringBuilder to create the formatted menu </p>
	 */
	SimpleUI(Drive authorizedService) {
		menu = new StringBuilder();
		menu.append(String.format("Select one of the following options:%n"));	
		menu.append(String.format("%n%s List user's files.", "f1"));
		menu.append(String.format("%n%s Get file.", "f2"));
		menu.append(String.format("%n%s Insert  file.", "f3"));
		menu.append(String.format("%n%s Check if a file is in a folder.", "p1"));
		menu.append(String.format("%n%s Get a file's parents.", "p2"));
		menu.append(String.format("%n%s Toggle debug.", "d"));
		menu.append(String.format("%n%s Display menu.", "m"));
		menu.append(String.format("%n%s Exit application.", "x"));
		// menu.append(newline);		
		
		
		// Instantiate the FileCommands class to handle
		// Google Drive File API calls.
		files = new FileCommands(authorizedService);
		
		// Display menu.
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
						System.out.println(menu.toString());
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
			
				case "f1": {
				
					try{
						files.listFiles();
					}
					catch (Exception e){
						System.out.println(String.format("%s", e.getMessage()));
					}
					break;
				}
				
				case "f2": {
					try{
						String fileID = readUserInput("Enter File ID (enter, to use default): ");
						if (fileID.isEmpty())
							// Assign default value.
							fileID = DefaultSettings.getSettings().getSourceFileID();
						
						// Get the name of the target file.
						String targetFileName = 
								DefaultSettings.getSettings().getTextTargetfileName();
						
						// Get the file metadata and copy it into the local tmp directory.
						files.getFile(fileID, "tmp", targetFileName);
						
					}
					catch (Exception e){
						System.out.println(String.format("%s", e.getMessage()));
					}
					break;
				}
				case "d": {
					
					try{
						// Toggle debug flag.
						boolean debug = !GeneralUtilities.isDEBUG();
						GeneralUtilities.setDEBUG(debug);
						System.out.println(String.format("Debug is %b", GeneralUtilities.isDEBUG()));
					}
					catch (Exception e){
						System.out.println(String.format("%s", e.getMessage()));
					}
					break;
				}
				case "f3": {
					
					try{
						
						String parentId = DefaultSettings.getSettings().getFolderID();
					
						
						// Insert  a jpeg file.
						String title = "luigi";
						String description = "Toy car";
						String mimeType = "image/jpeg";
						
						String filePath = null;
						URL fileUrl = null;
						
						// Get the path of the jpeg file to insert.
						try {
							fileUrl = this.getClass().getResource("/luigi.jpeg");
							filePath = fileUrl.getPath();
						} 
						catch (Exception e) {
							System.out.println(String.format("fileUrl error: %s", e.toString()));
						}
							
						
						// Insert the file.
						File file = files.insertFile(title, description, parentId, mimeType, filePath);
						System.out.println(String.format("%s %s", file.getTitle(), " inserted."));
						
						// Insert a text file.
						title = "midsummereve";
						description = "Poem";
						mimeType = "text/plain";
						
						
						// Get the path of the text file to insert.
						try {
							fileUrl = this.getClass().getResource("/midsummereve.txt");
							filePath = fileUrl.getPath();
						} 
						catch (Exception e) {
							System.out.println(String.format("fileUrl error: %s", e.toString()));
						}
						
						// Insert the file.
						file = files.insertFile(title, description, parentId, mimeType, filePath);
						System.out.println(String.format("%s %s", file.getTitle(), " inserted."));
						
					}
					catch (Exception e){
						System.out.println(String.format("%s", e.getMessage()));
					}
					break;
				}
				
				case "p1": {
						
					try{
						String fileID = readUserInput("Enter File ID (enter, to use default): ");
						
						if (fileID.isEmpty())
							// Assign default value.
							fileID = DefaultSettings.getSettings().getSourceFileID();
						String folderID = readUserInput("Enter Folder ID (enter, to use default): ");
						
						if (folderID.isEmpty())
							// Assign default value.
							fileID = DefaultSettings.getSettings().getFolderID();
						boolean fileinFolder = files.isFileInFolder(folderID, fileID);
						
						if (fileinFolder) {
							
							System.out.println(String.format("%s", "file is in the folder."));
						}
						else {
							System.out.println(String.format("%s", "file is not in the folder."));
						}
						
					}
					catch (Exception e){
						System.out.println(String.format("%s", e.getMessage()));
					}
					break;
				}
				
				case "p2": {
					
					try{
						String fileID = readUserInput("Enter File ID (enter, to use default): ");
						
						if (fileID.isEmpty())
							// Assign default value.
							fileID = DefaultSettings.getSettings().getSourceFileID();
					
						// Get the file parents.
						files.getPsrents(fileID);
						
					}
					catch (Exception e){
						System.out.println(String.format("%s", e.getMessage()));
					}
					break;
				}
				
				case "o2": {
					
					try{
						
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
		
	//	System.out.println(menu.toString());
		
	}
	
	private static void Exit() {
		System.out.println("Bye!\n");
		return;
	}
}
