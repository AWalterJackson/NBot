package com.nbot.core;

import com.nbot.core.CommandBuffer;
import com.nbot.core.Command;
import com.nbot.core.Response;

import java.util.ArrayList;

import com.nbot.externals.*;
import com.nbot.communicators.*;

public class Core {

	private static final String CLIENT_NAME = "CORE";

	public static void main(String[] args) throws Exception {
		CommandBuffer commandbuffer = new CommandBuffer();
		ArrayList<Command> incoming;
		ArrayList<Response> outgoing;

		// Load Config
		BotParams config = new BotParams();

		// Start communicator threads
		if (config.loadTelegram()) {
			Telegram tg = new Telegram(commandbuffer, config.getTelegramToken());
			tg.start();
		}
		
		if (config.loadPatreon()){
			Patreon pt = new Patreon(commandbuffer, config.getTrackedCreators());
			pt.start();
		}

		while (true) {
			incoming = commandbuffer.pullCommands(CLIENT_NAME);
			outgoing = new ArrayList<Response>();

			for (int i = 0; i < incoming.size(); i++) {
				System.out.println("processing");
				commandbuffer.writeOutgoing(process(incoming.get(i)));
			}
			Thread.sleep(3000);
		}
	}

	private static Response process(Command c) {
		String com = c.getCommand().toLowerCase();
		System.out.println(com);
		switch (com) {
		case "/commands":
			return new Response(c.getClient(), c.getSender(), "/commands - This list\n/intro - Learn why NBOT exists");
		case "/intro":
			return new Response(c.getClient(), c.getSender(), "My father Nantang'Itan decided one day to see if he could create a chat bot where the core logic was separate from the communication logic. The idea being that a bot built like that could be implemented on new platforms with negligible difficulty.\nThe result is me! :D");
		default:
			return new Response(c.getClient(), c.getSender(), "Unknown Command.");
		}
	}

}
