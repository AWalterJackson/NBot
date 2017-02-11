package com.nbot.core;

import com.nbot.core.CommandBuffer;
import com.nbot.core.Command;
import com.nbot.core.Response;

import java.util.ArrayList;

import com.nbot.communicators.*;

public class Core {

	private static final String CLIENT_NAME = "CORE";
	
	public static void main(String[] args) throws Exception{
		CommandBuffer commandbuffer = new CommandBuffer();
		ArrayList<Command> incoming;
		ArrayList<Response> outgoing;
		
		//Start communicator threads
		Telegram tg = new Telegram(commandbuffer);
		tg.start();
		
		while(true){
			incoming = commandbuffer.pullCommands(CLIENT_NAME);
			outgoing = new ArrayList<Response>();
			
			for(int i=0; i<incoming.size();i++){
				System.out.println("processing");
				commandbuffer.writeOutgoing(process(incoming.get(i)));
			}
			Thread.sleep(3000);	
		}
	}
	
	private static Response process(Command c){
		String com = c.getCommand();
		System.out.println(com);
		switch(com){
		default: return new Response(c.getClient(), c.getSender(), "Unknown Command.");
		}
	}

}
