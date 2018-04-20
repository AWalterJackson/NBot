package com.nbot.externals;

import com.nbot.utils.HttpsHandler;
import com.nbot.core.CommandBuffer;
import com.nbot.core.Command;
import com.nbot.core.Response;
import com.nbot.utils.NBotlogger;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.IOException;

public class Furaffinity extends Thread {
	private final String CLIENT_NAME = "FURAFFINITY";

	private CommandBuffer cb;
	private boolean stayalive;
	private String cookie;
	
	private Pattern journalpattern;
	private Pattern commentpattern;

	public Furaffinity(CommandBuffer cb, String cookie) {
		this.cb = cb;
		this.cookie = cookie;
		this.commentpattern = Pattern.compile("<li>.*name=\"comments-submissions\\[\\]\".*</li>");
		this.journalpattern = Pattern.compile("<li>.*name=\"journals\\[\\]\".*</li>");
	}

	public void run() {
		String pagedata = "";
		try {
			pagedata = HttpsHandler.httpsgetcookie("https://furaffinity.net/msg/others", cookie);
			
			
		} catch (IOException e) {
			e.printStackTrace();
			NBotlogger.log(CLIENT_NAME, "Error scraping Furaffinity");
		} catch (Exception e){
			e.printStackTrace();
			NBotlogger.log(CLIENT_NAME, "General Exception thrown in FURAFFINITY module");
		}
		
		try {
			Thread.sleep(300000);
		}
		catch (InterruptedException e){
			e.printStackTrace();
			NBotlogger.log(CLIENT_NAME, "Thread was awoken unexpectedly");
		}
	}
}
