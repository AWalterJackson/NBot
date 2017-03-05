package com.nbot.externals;

import com.nbot.utils.JSONextension;
import com.nbot.utils.HttpsHandler;

import java.util.ArrayList;

import com.nbot.core.Command;
import com.nbot.core.CommandBuffer;

public class Patreon extends Thread {

	private final String CLIENT_NAME = "PATREON";

	private CommandBuffer cb;
	private ArrayList<PatreonCreator> creators;
	private boolean stayalive;

	public Patreon(CommandBuffer cb, ArrayList<PatreonCreator> creators) {
		this.cb = cb;
		this.creators = creators;
		this.stayalive = true;
	}

	public void Run() {
		String pagedata = "";
		try {
			while (stayalive) {
				for (PatreonCreator creator : this.creators) {
					//TODO Pull data from Python script
					return;
				}
			}
		} catch (Exception e) {
			System.out.println("Exception thrown in Patreon Module.");
		}
		return;
	}
}
