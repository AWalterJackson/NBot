package com.nbot.core;

public class Response {
	private String client;
	private String recipient;
	private String message;
	
	public Response(String cli, String rec, String mes){
		this.client = cli;
		this.recipient = rec;
		this.message = mes;
	}
	
	//Getter functions
	public String getClient(){
		return this.client;
	}
	
	public String getRecipient(){
		return this.recipient;
	}
	
	public String getMessage(){
		return this.message;
	}
	
	public boolean equals(Response other){
		if(this.client != other.getClient()){
			return false;
		}
		if(this.recipient != other.getRecipient()){
			return false;
		}
		if(this.message != other.getMessage()){
			return false;
		}
		return true;
	}
	
	public Response clone(){
		return new Response(this.client, this.recipient, this.message);
	}
}
