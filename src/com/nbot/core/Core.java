package com.nbot.core;

import com.nbot.core.CommandBuffer;
import com.nbot.core.Command;
import com.nbot.core.Response;

import java.util.ArrayList;

import com.nbot.externals.*;
import com.nbot.utils.NBotlogger;
import com.nbot.communicators.*;

public class Core extends Thread{

	private static final String CLIENT_NAME = "CORE";
	private static final boolean DEBUG_MODE = false;

	public static void main(String[] args) throws Exception {
		CommandBuffer commandbuffer = new CommandBuffer(DEBUG_MODE);
		ArrayList<Command> incoming;
		ArrayList<Thread> errs;
		Telegram tg;
		Patreon pt;

		// Load Config
		BotParams config = new BotParams();

		// Start communicator threads
		if (config.loadTelegram()) {
			tg = new Telegram(commandbuffer, config.getTelegramToken());
			tg.start();
		}
		
		if (config.loadPatreon()){
			pt = new Patreon(commandbuffer, config.getTrackedCreators(), config.getTelegramMaster());
			pt.start();
		}

		while (true) {
			//Handle thread restarts
			errs = commandbuffer.pullErrors(CLIENT_NAME);
			for(Thread err : errs){
				if(err.getClass().equals(Telegram.class)){
					tg = new Telegram(commandbuffer, config.getTelegramToken());
					tg.start();
					NBotlogger.log(CLIENT_NAME, "TELEGRAM thread reinitialised");
				}
				if(err.getClass().equals(Patreon.class)){
					pt = new Patreon(commandbuffer, config.getTrackedCreators(), config.getTelegramMaster());
					pt.start();
					NBotlogger.log(CLIENT_NAME, "PATREON thread reinitialised");
				}
			}
			//Handle command processing
			incoming = commandbuffer.pullCommands(CLIENT_NAME);

			for (int i = 0; i < incoming.size(); i++) {
				commandbuffer.writeOutgoing(process(incoming.get(i)));
			}
			Thread.sleep(3000);
		}
	}

	private static Response process(Command c) {
		String com = c.getCommand().toLowerCase();
		NBotlogger.log(CLIENT_NAME, com+" from "+c.getSender()+" via "+c.getClient());
		switch (com) {
		case "/commands":
			return new Response(c.getClient(), c.getSender(), "/commands - This list\n/intro - Learn why NBOT exists\n/amiright - You're always right");
		case "/intro":
			return new Response(c.getClient(), c.getSender(), "My father Nantang'Itan decided one day to see if he could create a chat bot where the core logic was separate from the communication logic. The idea being that a bot built like that could be implemented on new platforms with negligible difficulty.\nThe result is me! :D");
		case "/amiright":
			return new Response(c.getClient(), c.getSender(), "You're DAMN right!");
		case "/status":
			return new Response(c.getClient(), c.getSender(), "System running.");
		case "alert":
			return new Response("TELEGRAM", c.getSender(), c.getDetails());
		default:
			return new Response(c.getClient(), c.getSender(), "Unknown Command.");
		}
	}

}
