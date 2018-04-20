package com.nbot.core;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONArray;
import org.json.JSONObject;

import com.nbot.externals.PatreonCreator;
import com.nbot.utils.NBotlogger;

public class BotParams {

	private static final String CLIENT_NAME = "CONFIG";
	private static final String FILENAME = "config.json";

	// Core chat modules
	private boolean telegram;
	private boolean discord;
	private boolean furaffinity;

	// External modules
	private boolean patreon;
	private ArrayList<PatreonCreator> trackedpatreon;

	private String fa_cookie;

	// Telegram Config
	private String telegramtoken;
	private String telegrammaster;

	// Generic responses
	private HashMap<String, String> generics;

	public BotParams() {
		NBotlogger.log(CLIENT_NAME, "Loading Configuration");
		JSONObject config;

		NBotlogger.log(CLIENT_NAME, "Working Directory = " + System.getProperty("user.dir"));

		try {
			BufferedReader br = new BufferedReader(new FileReader(FILENAME));
			StringBuilder sb = new StringBuilder();
			String line = br.readLine();
			while (line != null) {
				sb.append(line);
				line = br.readLine();
			}
			config = new JSONObject(sb.toString());
			br.close();
			// Communicators
			this.telegram = config.getJSONObject("load_clients").getBoolean("telegram");
			this.discord = config.getJSONObject("load_clients").getBoolean("discord");

			// External Modules
			this.patreon = config.getJSONObject("load_external_modules").getBoolean("patreon");

			JSONArray tcreators = config.getJSONObject("patreon_config").getJSONArray("creators");
			this.trackedpatreon = new ArrayList<PatreonCreator>();
			for (int i = 0; i < tcreators.length(); i++) {
				PatreonCreator patreoncreator = new PatreonCreator(tcreators.getJSONObject(i).getString("name"));
				JSONArray levels = tcreators.getJSONObject(i).getJSONArray("levels");
				for (int j = 0; j < levels.length(); j++) {
					patreoncreator.addLevel(levels.getInt(j));
				}
				this.trackedpatreon.add(patreoncreator);
			}

			//FURAFFINITY Configuration
			this.furaffinity = config.getJSONObject("load_external_modules").getBoolean("furaffinity");
			
			if (this.furaffinity) {
				this.fa_cookie = config.getJSONObject("furaffinity_config").getString("cookie");
			}

			// TELEGRAM Configuration
			this.telegramtoken = config.getJSONObject("telegram_config").getString("token");
			this.telegrammaster = config.getJSONObject("telegram_config").getString("master");

			// Generate Generics
			this.generics = new HashMap<String, String>();
			JSONArray gens = config.getJSONArray("generics");
			for (int i = 0; i < gens.length(); i++) {
				this.generics.put(gens.getJSONObject(i).getString("command"),
						gens.getJSONObject(i).getString("response"));
			}
		} catch (JSONException e) {
			NBotlogger.log(CLIENT_NAME, "Malformed JSON in Config");
			e.printStackTrace();
			System.exit(-1);

		} catch (IOException e) {
			NBotlogger.log(CLIENT_NAME, "Error reading config file, does it exist?");
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

	public String getTelegramMaster() {
		return this.telegrammaster;
	}

	public boolean loadPatreon() {
		return this.patreon;
	}

	public ArrayList<PatreonCreator> getTrackedCreators() {
		return this.trackedpatreon;
	}

	public boolean isGeneric(String obj) {
		if (this.generics.containsKey(obj)) {
			return true;
		}
		return false;
	}

	public String getGeneric(String obj) {
		return this.generics.get(obj).toString();
	}
	
	public boolean loadFuraffinity(){
		return this.furaffinity;
	}
	
	public String getFACookie(){
		return this.fa_cookie;
	}
}
