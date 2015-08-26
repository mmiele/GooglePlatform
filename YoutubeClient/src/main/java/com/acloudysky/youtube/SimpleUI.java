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

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;



/**
 * Displays the menu of choices the user can select from. It processes the
 * user's input and calls the proper method based on the user's choice.
 * @see FileReader and @see FileWriter. 
 * 
 * @author Michael.
 *
 */
public class SimpleUI {

	private StringBuilder menu;
	public static final String newline = System.getProperty("line.separator");
	
	/**
	 * Initializes the menu that allows the user to make the allowed selections.
	 * <p>
	 * It uses a StringBuilder to create the formatted menu </p>
	 */
	SimpleUI() {
		
		menu = new StringBuilder();
		menu.append(String.format("Select one of the following options:%n"));
		menu.append(String.format("%n%s List uploaded videos.", "l1"));
		menu.append(String.format("%n%s Search videos.", "s1"));
		menu.append(String.format("%n%s Upload video.", "u1"));
		menu.append(String.format("%n%s Update video.", "u2"));

		menu.append(String.format("%n%s Toggle debug.", "d"));
		menu.append(String.format("%n%s Display menu.", "m"));
		menu.append(String.format("%n%s Exit application.", "x"));
		// menu.append(newline);		

		
		// Display menu.
		System.out.println(menu.toString());

	}

	/*
	 * Read user input.
	 */
	private static String readUserInput(String msg) {

		// Open standard input.
		BufferedReader br = new BufferedReader(new java.io.InputStreamReader(
				System.in));

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

			try {
				// Exit the application.
				if ("x".equals(selection))
					break;
				else if ("m".equals(selection)) {
					// Display menu
					System.out.println(menu.toString());
					continue;
				} else
					// Read the input string.
					in = selection.trim();

			} catch (Exception e) {
				// System.out.println(e.toString());
				System.out.println(String.format("Input %s is not allowed%n",
						selection));
				continue;
			}

			// Select action to perform.
			switch (in) {

			case "l1": {

				try {
					VideoInformation.listVideos();
				} catch (Exception e) {
					System.out.println(String.format("%s", e.getMessage()));
				}
				break;
			}

			case "s1": {
				try {
					VideoInformation.searchVideos();

				} catch (Exception e) {
					System.out.println(String.format("%s", e.getMessage()));
				}
				break;
			}
			
			case "u1": {
				try {
					VideoActions.performUpload();

				} catch (Exception e) {
					System.out.println(String.format("%s", e.getMessage()));
				}
				break;
			}
			
			case "u2": {
				try {
					VideoActions.performUpdate();

				} catch (Exception e) {
					System.out.println(String.format("%s", e.getMessage()));
				}
				break;
			}

			default: {
				System.out.println(String
						.format("%s is not allowed", selection));
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
