package com.nbot.core;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONArray;
import org.json.JSONObject;

import com.nbot.externals.PatreonCreator;

public class BotParams {

	private static final String FILENAME = "config.json";

	// Core chat modules
	private boolean telegram;
	private boolean discord;

	// External modules
	private boolean patreon;
	private ArrayList<PatreonCreator> trackedpatreon;

	// Telegram Config
	private String telegramtoken;
	private String telegrammaster;

	public BotParams() {
		System.out.println("Loading Configuration.");
		JSONObject config;

		System.out.println("Working Directory = " + System.getProperty("user.dir"));

		try {
			BufferedReader br = new BufferedReader(new FileReader("config.json"));
			StringBuilder sb = new StringBuilder();
			String line = br.readLine();
			while (line != null) {
				sb.append(line);
				line = br.readLine();
			}
			config = new JSONObject(sb.toString());
			// Communicators
			this.telegram = config.getJSONObject("load_clients").getBoolean("telegram");
			this.discord = config.getJSONObject("load_clients").getBoolean("discord");

			// External Modules
			this.patreon = config.getJSONObject("load_external_modules").getBoolean("patreon");
			
			JSONArray tcreators = config.getJSONObject("patreon_config").getJSONArray("creators");
			this.trackedpatreon = new ArrayList<PatreonCreator>();
			for (int i = 0; i<tcreators.length(); i++) {
				PatreonCreator patreoncreator = new PatreonCreator(tcreators.getJSONObject(i).getString("name"));
				JSONArray levels = tcreators.getJSONObject(i).getJSONArray("levels");
				for(int j=0; j<levels.length();j++){
					patreoncreator.addLevel(levels.getInt(j));
				}
				this.trackedpatreon.add(patreoncreator);
			}

			// TELEGRAM Config
			this.telegramtoken = config.getJSONObject("telegram_config").getString("token");
			this.telegrammaster = config.getJSONObject("telegram_config").getString("master");
		} catch (JSONException e) {
			System.out.println("Malformed JSON in Config");
			e.printStackTrace();
			System.exit(-1);

		} catch (IOException e) {
			System.out.println("Error reading config file, does it exist?");
			e.printStackTrace();
			System.exit(-1);
		}
	}

	public String getTelegramToken() {
		return this.telegramtoken;
	}

	public boolean loadTelegram() {
		return this.telegram;
	}

	public boolean loadPatreon() {
		return this.patreon;
	}

	public ArrayList<PatreonCreator> getTrackedCreators() {
		return this.trackedpatreon;
	}
}
