package com.nbot.externals;

import com.nbot.utils.JSONextension;
import com.nbot.utils.HttpsHandler;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import com.nbot.core.Command;
import com.nbot.core.CommandBuffer;

public class Patreon extends Thread {

	private final String CLIENT_NAME = "PATREON";
	private final String SCRIPT_BASE = "bash DISPLAY=:0 python scraper.py ";

	private CommandBuffer cb;
	private ArrayList<PatreonCreator> creators;
	private boolean stayalive;

	public Patreon(CommandBuffer cb, ArrayList<PatreonCreator> creators) {
		this.cb = cb;
		this.creators = creators;
		this.stayalive = true;
	}

	public void run() {
		String pagedata = "";
		try {
			generatescraper(creators);
			getdata();
			System.out.println("Patreon alert module loaded");
			while (stayalive) {
				return;
			}
		} catch (IOException e) {
			System.out.println("Unable to write to scraper.bat");
			e.printStackTrace();
		} catch (Exception e) {
			System.out.println("Exception thrown in Patreon Module.");
			e.printStackTrace();
		}
		return;
	}
	
	private void getdata() throws IOException, InterruptedException{
		System.out.println(System.getProperty("user.dir")+"\\scrape.bat");
		Process proc = new ProcessBuilder(System.getProperty("user.dir")+"\\scrape.bat").start();
		System.out.println("Waiting");
		proc.waitFor();
		System.out.println("Terminated");
	}

	private void generatescraper(ArrayList<PatreonCreator> creators) throws IOException {
		String script = "";
		File f = new File("scrape.bat");
		if (!f.createNewFile()) {
			f.delete();
			f.createNewFile();
		}
		PrintWriter printer = new PrintWriter(f);
		for (PatreonCreator creator : creators) {
			script = script + SCRIPT_BASE + creator.getname() + " > " + creator.getname() + ".txt\n";
		}
		printer.write(script);
		printer.close();
		return;
	}
}
