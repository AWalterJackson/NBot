package com.nbot.core;

import com.nbot.core.CommandBuffer;
import com.nbot.core.Command;
import com.nbot.core.Response;

import com.nbot.communicators.*;

public class Core {

	private final String CLIENT_NAME = "CORE";
	
	public static void main(String[] args) throws Exception{
		CommandBuffer commandbuffer = new CommandBuffer();
		
		//Start communicator threads
		Telegram tg = new Telegram(commandbuffer);
		tg.start();
	}

}
