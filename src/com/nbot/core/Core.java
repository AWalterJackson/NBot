package com.nbot.core;

import com.nbot.core.CommandBuffer;
import com.nbot.core.Command;
import com.nbot.core.Response;

import java.util.ArrayList;

import com.nbot.externals.*;
import com.nbot.utils.HttpsHandler;
import com.nbot.utils.NBotlogger;
import com.nbot.communicators.*;

public class Core extends Thread {

	private static final String CLIENT_NAME = "CORE";
	private static final boolean DEBUG_MODE = false;
	
	private static int telegramErrors;
	private static int patreonErrors;
	private static int furaffinityErrors;
	
	private static boolean loadedTG;
	private static boolean loadedPT;
	private static boolean loadedFA;
	
	private static BotParams config;

	public static void main(String[] args) throws Exception {
		CommandBuffer commandbuffer = new CommandBuffer(DEBUG_MODE);
		ArrayList<Command> incoming;
		ArrayList<Thread> errs;
		Telegram tg;
		Patreon pt;
		Furaffinity fa;
		
		//Error Logging
		telegramErrors = 0;
		patreonErrors = 0;
		furaffinityErrors = 0;
		
		loadedTG = false;
		loadedPT = false;
		loadedFA = false;

		// Load Config
		config = new BotParams();

		// Start communicator threads
		if (config.loadTelegram()) {
			tg = new Telegram(commandbuffer, config.getTelegramToken());
			tg.start();
			loadedTG = true;
		}

		if (config.loadPatreon()) {
			pt = new Patreon(commandbuffer, config.getTrackedCreators(), config.getTelegramMaster());
			pt.start();
			loadedPT = true;
		}
		
		if(config.loadFuraffinity()){
			fa = new Furaffinity(commandbuffer, config.getFACookie());
			fa.start();
			loadedFA = true;
		}

		while (true) {
			// Handle thread restarts
			errs = commandbuffer.pullErrors(CLIENT_NAME);
			for (Thread err : errs) {
				if (err.getClass().equals(Telegram.class)) {
					telegramErrors++;
					tg = new Telegram(commandbuffer, config.getTelegramToken());
					tg.start();
					NBotlogger.log(CLIENT_NAME, "TELEGRAM thread reinitialised");
				}
				if (err.getClass().equals(Patreon.class)) {
					patreonErrors++;
					pt = new Patreon(commandbuffer, config.getTrackedCreators(), config.getTelegramMaster());
					pt.start();
					NBotlogger.log(CLIENT_NAME, "PATREON thread reinitialised");
				}
			}
			// Handle command processing
			incoming = commandbuffer.pullCommands(CLIENT_NAME);

			for (int i = 0; i < incoming.size(); i++) {
				commandbuffer.writeOutgoing(process(incoming.get(i)));
			}
			Thread.sleep(3000);
		}
	}

	private static Response process(Command c) {
		String com = c.getCommand().toLowerCase();
		NBotlogger.log(CLIENT_NAME, com + " from " + c.getSender() + " via " + c.getClient());
		switch (com) {
		case "/status":
			String sysinfo = "OS: "+System.getProperty("os.name")+"\nVersion: "+System.getProperty("os.version");
			String modules = "";
			if(loadedTG){
				modules+="TELEGRAM,";
			}
			if(loadedPT){
				modules+="PATREON,";
			}
			
			String errs = "";
			if(loadedTG){
				errs+="Telegram: "+telegramErrors+"\n";
			}
			if(loadedPT){
				errs+="Patreon: "+patreonErrors+"\n";
			}
			
			modules = modules.substring(0,modules.length()-1);
			errs = errs.substring(0, errs.length()-1);
			
			return new Response(c.getClient(), c.getSender(), "System running.\nSystem Information:\n"+sysinfo+"\n\nLoaded Modules:\n"+modules+"\n\nErrors since last launch:\n"+errs);
		case "/getip":
			try {
				return new Response(c.getClient(), c.getSender(), HttpsHandler.httpget("http://checkip.amazonaws.com"));
			} catch (Exception e) {
				return new Response(c.getClient(), c.getSender(), "I'm sorry, an error occurred communicating with checkip.amazonaw.com, try again in a few minutes.");
			}
		case "alert":
			return new Response("TELEGRAM", c.getSender(), c.getDetails());
		default:
			if(config.isGeneric(com)){
				return new Response(c.getClient(), c.getSender(), config.getGeneric(com));
			}
			else {
				return new Response(c.getClient(), c.getSender(), "Unknown Command.");
			}
		}
	}

}
