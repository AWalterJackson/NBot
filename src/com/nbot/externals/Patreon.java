package com.nbot.externals;

import com.nbot.utils.JSONextension;
import com.nbot.utils.NBotlogger;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

	public Patreon(CommandBuffer cb, ArrayList<PatreonCreator> creators, String receiver) {
		this.cb = cb;
		this.creators = creators;
		this.receiver = receiver;
		this.stayalive = true;
	}

	public void run() {
		String pagedata = "";
		ArrayList<JSONObject> rewards;
		try {
			NBotlogger.log(CLIENT_NAME, "Alert module loaded");
			while (stayalive) {
				for(PatreonCreator creator : this.creators){
					pagedata = getdata(creator.getname());
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
				Thread.sleep(300000);
			}
		} catch (IOException e) {
			NBotlogger.log(CLIENT_NAME, "Error scraping Patreon");
			this.cb.writeError(this, CLIENT_NAME);
		} catch (Exception e) {
			NBotlogger.log(CLIENT_NAME, "General Exception thrown in Patreon Module.");
			this.cb.writeError(this, CLIENT_NAME);
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
	
	private String getdata(String creator) throws IOException, InterruptedException{
		List<String> cmdAndArgs = Arrays.asList("C:\\Python27\\python.exe", "scraper.py", creator);
		File dir = new File(System.getProperty("user.dir"));
		ProcessBuilder pb = new ProcessBuilder(cmdAndArgs);
		pb.directory(dir);
		
		NBotlogger.log(CLIENT_NAME, "Getting data for: "+creator);
		Process p = pb.start();
		
		InputStream is = p.getInputStream();
		InputStreamReader isr = new InputStreamReader(is);
		BufferedReader br = new BufferedReader(isr);
		String line;
		StringBuffer response = new StringBuffer();
		while((line = br.readLine()) != null){
			response.append(line);
		}
		p.waitFor();
		return response.toString();
	}
	
	public String getClient(){
		return this.CLIENT_NAME;
	}

/*	private void generatescraper(ArrayList<PatreonCreator> creators) throws IOException {
		String script = "";
		File f = new File("scrape.bat");
		if (!f.createNewFile()) {
			f.delete();
			f.createNewFile();
		}
		PrintWriter printer = new PrintWriter(f);
		for (PatreonCreator creator : creators) {
			script = script + SCRIPT_BASE + creator.getname() + " > " + creator.getname() + ".txt\"\n";
		}
		printer.write(script);
		printer.close();
		return;
	}*/
}
