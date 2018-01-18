package com.nbot.core;

import com.nbot.core.Command;
import com.nbot.core.Response;
import com.nbot.utils.HttpsHandler;
import com.nbot.utils.NBotlogger;

//The core message handler is a utility class that holds the specific
//functions for individual message types
public final class CoreMessageHandler {
	public static Response process(Command c) {
		String com = c.getCommand().toLowerCase();
		NBotlogger.log("CORE", com + " from " + c.getSender() + " via " + c.getClient());
		switch (com) {
		case "/commands":
			return new Response(c.getClient(), c.getSender(),
					"/commands - This list\n/intro - Learn why NBOT exists\n/amiright - You're always right");
		case "/intro":
			return new Response(c.getClient(), c.getSender(),
					"My father Nantang'Itan decided one day to see if he could create a chat bot where the core logic was separate from the communication logic. The idea being that a bot built like that could be implemented on new platforms with negligible difficulty.\nThe result is me! :D");
		case "/amiright":
			return new Response(c.getClient(), c.getSender(), "You're DAMN right!");
		case "/status":
			
			return new Response(c.getClient(), c.getSender(), "System running.");
		case "/getip":
			try {
				return new Response(c.getClient(), c.getSender(), HttpsHandler.httpget("http://checkip.amazonaws.com"));
			} catch (Exception e) {
				return new Response(c.getClient(), c.getSender(),
						"I'm sorry, an error occurred communicating with checkip.amazonaw.com, try again in a few minutes.");
			}
		case "alert":
			return new Response("TELEGRAM", c.getSender(), c.getDetails());
		default:
			return new Response(c.getClient(), c.getSender(), "Unknown Command.");
		}
	}
}
