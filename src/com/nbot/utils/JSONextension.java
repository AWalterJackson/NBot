package com.nbot.utils;

import org.json.*;

public final class JSONextension {
	
	public String getOptionalField(JSONObject obj, String key){
		try{
			return obj.get(key).toString();
		}
		catch(JSONException e){
			return "";
		}
	}
	
	public String getOptionalField(String obj, String key){
		JSONObject json = new JSONObject(obj);
		try{
			return json.get(key).toString();
		}
		catch(JSONException e){
			return "";
		}
	}
	
	public JSONObject getOptionalObject(JSONObject obj, String key){
		try{
			return obj.getJSONObject(key);
		}
		catch(JSONException e){
			return new JSONObject("{\"nodata\":true");
		}
	}
	
	public JSONObject getOptionalObject(String obj, String key){
		JSONObject json = new JSONObject(obj);
		try{
			return json.getJSONObject(key);
		}
		catch(JSONException e){
			return new JSONObject("\"nodata\":true");
		}
	}
}
