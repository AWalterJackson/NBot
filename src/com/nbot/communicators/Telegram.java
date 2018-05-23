package com.nbot.communicators;

import com.nbot.core.CommandBuffer;
import com.nbot.core.Command;
import com.nbot.core.Response;
import com.nbot.utils.HttpsHandler;
import com.nbot.utils.JSONextension;
import com.nbot.utils.NBotlogger;

import java.io.IOException;
import java.util.ArrayList;

import org.json.*;

public class Telegram extends Thread {
	private final String CLIENT_NAME = "TELEGRAM";
	private String token;
	private CommandBuffer commandbuffer;
	private ArrayList<Response> responses;
	private volatile boolean stayalive;

	public Telegram(CommandBuffer cb, String token) {
		this.commandbuffer = cb;
		this.token = token;
		this.stayalive = true;
	}

	public void shutdown() {
		this.stayalive = false;
		return;
	}

	private Command buildCommand(JSONObject message) {
		String senderid;
		String messagecommand;
		String messagedetail;

		String[] messagetokens;

		assert (message.getInt("date") > 0);

		if (message.getJSONObject("chat").get("type").toString().equals("group")) {
			senderid = message.getJSONObject("chat").get("id").toString();
		} else {
			senderid = message.getJSONObject("from").get("id").toString();
		}

		messagetokens = message.getString("text").split(" ", 2);
		if (messagetokens.length > 1) {
			messagedetail = messagetokens[1];
		} else {
			messagedetail = "";
		}
		messagecommand = messagetokens[0].split("@",2)[0];

		return new Command(CLIENT_NAME, senderid, messagecommand, messagedetail);
	}

	private String buildResponse(Response message) {
		String res = "{\"chat_id\":" + message.getRecipient() + ",\"text\":\"" + message.getMessage() + "\"}";
		return res;
	}
	
	public String getClient(){
		return this.CLIENT_NAME;
	}

	public void run() {
		long offset = 0;
		JSONObject message;
		Command incoming;

		try {
			JSONObject response = new JSONObject(
					HttpsHandler.httpsget("https://api.telegram.org/bot"+token+"/getMe"));
			if (response.getBoolean("ok")) {
				NBotlogger.log(CLIENT_NAME, "Telegram API connection established");
			}
			response = response.getJSONObject("result");
			NBotlogger.log(CLIENT_NAME, "BOT ID: " + response.get("id").toString());
			NBotlogger.log(CLIENT_NAME, "BOT NAME: " + response.get("first_name").toString());

			while (stayalive) {
				response = new JSONObject(HttpsHandler.httpsget(
						"https://api.telegram.org/bot"+token+"/getupdates?offset="
								+ offset));
				if (response.getBoolean("ok")) {
					if (JSONextension.getOptionalField(response, "result") != "") {
						JSONArray updates = response.getJSONArray("result");

						for (int i = 0; i < updates.length(); i++) {
							message = updates.getJSONObject(i);
							offset = message.getInt("update_id") + 1;
							message = message.getJSONObject("message");
							if (JSONextension.getOptionalField(message, "entities") != "") {
								incoming = buildCommand(message);
								commandbuffer.writeIncoming(incoming);
							}
						}
					}
				}
				// System.out.println("Handling Responses");
				responses = commandbuffer.pullResponses(CLIENT_NAME);
				if (responses.size() > 0) {
					for (int i = 0; i < responses.size(); i++) {
						Response current = responses.get(i);
						HttpsHandler.httpspost(
								"https://api.telegram.org/bot"+token+"/sendMessage",
								"application/json", buildResponse(current));
					}
				}
				Thread.sleep(3000);
			}
		} catch (IOException e){
			NBotlogger.log(CLIENT_NAME, "Telegram API returned fail code.");
			this.commandbuffer.writeError(this, CLIENT_NAME);
			
		}
			catch (Exception e) {
			NBotlogger.log(CLIENT_NAME, "Exception raised in Telegram Communicator.");
			this.commandbuffer.writeError(this, CLIENT_NAME);
		}
	}
}
