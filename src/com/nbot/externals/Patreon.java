package com.nbot.externals;

import com.nbot.utils.HttpsHandler;
import com.nbot.utils.JSONextension;
import com.nbot.utils.NBotlogger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONObject;

import com.nbot.core.Command;
import com.nbot.core.CommandBuffer;

public class Patreon extends Thread {

	private final String CLIENT_NAME = "PATREON";

	private CommandBuffer cb;
	private ArrayList<PatreonCreator> creators;
	private String receiver;
	private boolean stayalive;
	private Pattern cidpattern;
	private Pattern numberpattern;

	public Patreon(CommandBuffer cb, ArrayList<PatreonCreator> creators, String receiver) {
		this.cb = cb;
		this.creators = creators;
		this.receiver = receiver;
		this.stayalive = true;
		this.cidpattern = Pattern.compile("\"creator_id\": \\d*");
		this.numberpattern = Pattern.compile("\\d+");
	}

	public void run() {
		String pagedata = "";
		ArrayList<JSONObject> rewards;

		try {
			NBotlogger.log(CLIENT_NAME, "Initializing");
			for(PatreonCreator creator : this.creators){
				creator.setid(retrieveid(creator.getname()));
				NBotlogger.log(CLIENT_NAME, creator.getname()+ " found with ID: " + creator.getid());
			}
			NBotlogger.log(CLIENT_NAME, "Alert module loaded");
			while (stayalive) {
				for(PatreonCreator creator : this.creators){
					pagedata = HttpsHandler.httpsget("https://api.patreon.com/user/"+creator.getid());
					rewards = getrewards(pagedata);
					for(int level : creator.getlevels()){
						for(JSONObject reward : rewards){
							if(reward.getJSONObject("attributes").getInt("amount_cents") == level){
								if(checkslots(reward.getJSONObject("attributes"))){
									String alert = "ALERT: Open slot for "+creator.getname().toUpperCase()+ " at "+level;
									NBotlogger.log(CLIENT_NAME, alert);
									this.cb.writeIncoming(new Command(CLIENT_NAME, this.receiver, "alert", alert));
								}
							}
						}
					}
				}
				Thread.sleep(120000);
			}
			
			
		} catch (IOException e) {
			e.printStackTrace();
			NBotlogger.log(CLIENT_NAME, "Error scraping Patreon");
			this.cb.writeError(this, CLIENT_NAME);
		} catch (Exception e) {
			NBotlogger.log(CLIENT_NAME, "General Exception thrown in Patreon Module.");
			e.printStackTrace();
			this.cb.writeError(this, CLIENT_NAME);
		} finally {
			try {
				Thread.sleep(120000);
			} catch (InterruptedException e) {
				return;
			}
		}
		return;
	}
	
	private boolean checkslots(JSONObject tier){
		if(tier.get("remaining").toString().equals("null")){
			NBotlogger.log(CLIENT_NAME, "Error: Checked unlimited reward.");
			return false;
		}
		else if(tier.getInt("remaining") > 0){
			return true;
		}
		return false;
	}
	
	private ArrayList<JSONObject> getrewards(String data){
		ArrayList<JSONObject> rewards = new ArrayList<JSONObject>();
		JSONArray jsondata = new JSONObject(data).getJSONArray("included");
		for(int i=0;i<jsondata.length();i++){
			JSONObject entry = jsondata.getJSONObject(i);
			if(JSONextension.hasOptionalField(entry, "type")){
				if(entry.getString("type").equals("reward")){
					rewards.add(entry);
				}
			}
		}
		return rewards;
	}
	
	private String retrieveid(String creator) throws Exception{
		String pagedata = HttpsHandler.httpsget("https://www.patreon.com/"+creator);
		Matcher cid = this.cidpattern.matcher(pagedata);
		if(cid.find()){
			Matcher value = this.numberpattern.matcher(cid.group(0));
			value.find();
			return value.group(0);
		}
		else{
			throw new Exception();
		}
	}
	
	public String getClient(){
		return this.CLIENT_NAME;
	}
}
