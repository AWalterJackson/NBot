package com.nbot.utils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class HttpsHandler {
	private final String USER_AGENT = "Mozilla/5.0";
	private final boolean debug = false;
	
	public HttpsHandler(){
	}
	
	public String httpsget(String url) throws Exception {
		//Open connection
		URL resourcelocator = new URL(url);
		HttpsURLConnection con = (HttpsURLConnection) resourcelocator.openConnection();
		
		//Set Request Type
		con.setRequestMethod("GET");
		
		//Set Header Details
		con.setRequestProperty("User-Agent", USER_AGENT);
		
		int responsecode = con.getResponseCode();
		if(debug){
			System.out.println("GET sent to: " + url);
			System.out.println("Response code: " + responsecode);
		}
		
		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String inputline;
		StringBuffer response = new StringBuffer();
		
		while((inputline = in.readLine()) != null){
			response.append(inputline);
		}
		in.close();

		return response.toString();
	}
	
	public void httpspost(String url){
		return;
	}
}
