package com.acloudysky.drive;

import java.util.Iterator;
import java.util.Map.Entry;

import com.google.api.client.http.HttpHeaders;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpResponse;

/**
 * Define general utilities to help with housekeeping and routine tasks.
 * @author mmiele
 *
 */
public class GeneralUtilities {

	private static boolean DEBUG = false;
	
	
	public static boolean isDEBUG() {
		return DEBUG;
	}

	public static void setDEBUG(boolean dEBUG) {
		DEBUG = dEBUG;
	}

	/**
	 * Display an HTTP request header information.
	 * @param request The request whose information must be displayed.
	 */
	public static void displayRequestHeaders(HttpRequest request) {
		   
		HttpHeaders headers = request.getHeaders();
		
		// Get the file list iterator.
		Iterator<Entry<String, Object>> iterator = headers.entrySet().iterator();
		
		System.out.println(String.format("%n================== " + "Request Headers" + " ================== %n"));	
		
		while (iterator.hasNext()) {	
			Entry<String, Object> entry = iterator.next();
			String key = entry.getKey();
			String value = entry.getValue().toString();
			System.out.println(String.format("Key: %s Value: %s", key, value));
		}
		
	}
	
	/**
	 * Display an HTTP response header information.
	 * @param response The response whose information must be displayed.
	 */
	public static void displayResponseHeaders(HttpResponse response) {
		   
		HttpHeaders headers = response.getHeaders();
		
		
		// Get the file list iterator.
		Iterator<Entry<String, Object>> iterator = headers.entrySet().iterator();
		
		System.out.println(String.format("%n================== " + "Response Headers" + " ================== %n"));	
		
		while (iterator.hasNext()) {	
			Entry<String, Object> entry = iterator.next();
			String key = entry.getKey();
			String value = entry.getValue().toString();
			System.out.println(String.format("Key: %s Value: %s", key, value));
		}
		
	}



}
