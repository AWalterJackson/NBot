package com.nbot.communicators;

import com.nbot.core.CommandBuffer;
import com.nbot.core.Command;
import com.nbot.utils.HttpsHandler;

import org.json.*;

public class Telegram extends Thread{
	private final String CLIENT_NAME = "TELEGRAM";
	private CommandBuffer commandbuffer;
	private HttpsHandler https;
	private volatile boolean stayalive;
	
	public Telegram(CommandBuffer cb){
		this.commandbuffer = cb;
		this.https = new HttpsHandler();
		this.stayalive = true;
	}
	
	public void shutdown(){
		this.stayalive = false;
		return;
	}
	
	private Command buildCommand(JSONObject message){
		String senderid;
		String messagecommand;
		String messagedetail;
				
		String[] messagetokens;
		
		assert(message.getInt("date") > 0);
		
		senderid = message.getJSONObject("from").get("id").toString();
		
		messagetokens = message.getString("text").split(" ", 2);
		messagecommand = messagetokens[0];
		messagedetail = messagetokens[1];
		
		return new Command(CLIENT_NAME, senderid, messagecommand, messagedetail);
	}
	
	public void run(){
		long offset = 0;
		JSONObject message;
		Command incoming;

		try{
			JSONObject response = new JSONObject(https.httpsget("https://api.telegram.org/bot306018202:AAF45bD-TJn3g9rtf_pv7yIvmQLvi8sFJGU/getMe"));
			if(response.getBoolean("ok")){
				System.out.println("Telegram API connection established");
			}
			response = response.getJSONObject("result");
			System.out.println("BOT ID: " + response.get("id").toString());
			System.out.println("BOT NAME: " + response.get("first_name").toString());

			response = new JSONObject(https.httpsget("https://api.telegram.org/bot306018202:AAF45bD-TJn3g9rtf_pv7yIvmQLvi8sFJGU/getupdates"));
			System.out.println(response.get("result").toString());
			if(response.getBoolean("ok")){
				JSONArray updates = response.getJSONArray("results");
				
				for(int i = 0; i< updates.length(); i++){
					message = updates.getJSONObject(i);
					offset = message.getInt("update_id")+1;
					message = message.getJSONObject("message");
					incoming = buildCommand(message);
				}
			}
			
			while(stayalive){
				https.httpsget("https://api.telegram.org/bot306018202:AAF45bD-TJn3g9rtf_pv7yIvmQLvi8sFJGU/getupdates?offset="+offset);
				Thread.sleep(3000);
			}
		}
		catch(Exception e){
			e.printStackTrace();
			System.out.println("Exception raised in Telegram Communicator.\n");
		}
	}
}
