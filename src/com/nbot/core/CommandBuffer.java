package com.nbot.core;

import java.util.ArrayList;

import com.nbot.utils.NBotlogger;

public class CommandBuffer extends Thread{
	
	private static final String CLIENT_NAME = "COMMAND BUFFER";
	
	private volatile String incomeaccessor;
	private volatile String outgoaccessor;
	private ArrayList<Command> incoming;
	private ArrayList<Response> outgoing;
	
	public CommandBuffer(){
		this.incoming = new ArrayList<Command>();
		this.outgoing = new ArrayList<Response>();
		this.incomeaccessor = "none";
		this.outgoaccessor = "none";
	}
	
	public ArrayList<Response> pullResponses(String client){
		getOutgoingLock(client);
		ArrayList<Response> res = new ArrayList<Response>();
		Response current;
		for(int i = 0; i<outgoing.size(); i++){
			current = outgoing.get(i);
			if(current.getClient() == client){
				res.add(current);
				outgoing.remove(current);
				i--;
			}
		}
		releaseOutgoingLock(client);
		return res;
	}
	
	public ArrayList<Command> pullCommands(String client){
		getIncomingLock(client);
		ArrayList<Command> com = new ArrayList<Command>();
		Command current;
		for(int i = 0; i<incoming.size();i++){
			com.add(incoming.get(i));
		}
		incoming.clear();
		releaseIncomingLock(client);
		return com;
	}
	
	public void writeIncoming(Command inc){
		getIncomingLock(inc.getClient());
		this.incoming.add(inc.clone());
		releaseIncomingLock(inc.getClient());
	}
	
	public void writeOutgoing(Response out){
		getOutgoingLock(out.getClient());
		this.outgoing.add(out);
		releaseOutgoingLock(out.getClient());
	}
	
	public Command popIncoming(String client, int index){
		getIncomingLock(client);
		Command com = this.incoming.get(index);
		this.incoming.remove(index);
		releaseIncomingLock(client);
		return com;
	}
	
	public Response popOutgoing(String client, int index){
		getOutgoingLock(client);
		Response res = this.outgoing.get(index);
		this.outgoing.remove(index);
		releaseOutgoingLock(client);
		return res;
	}
	
	private synchronized boolean getIncomingLock(String client){
		while(this.incomeaccessor != "none" && this.incomeaccessor != client){}
		this.incomeaccessor = client;
		NBotlogger.log(CLIENT_NAME, "Incoming locked by "+client);
		return true;
	}
	
	private synchronized boolean releaseIncomingLock(String client){
		if(this.incomeaccessor == client){
			this.incomeaccessor = "none";
			return true;
		}
		else{
			return false;
		}
	}
	
	private synchronized boolean getOutgoingLock(String client){
		while(this.outgoaccessor != "none" && this.outgoaccessor != client){}
		this.outgoaccessor = client;
		NBotlogger.log(CLIENT_NAME, "Outgoing locked by "+client);
		return true;
	}
	
	private synchronized boolean releaseOutgoingLock(String client){
		if(this.outgoaccessor == client){
			this.outgoaccessor = "none";
			return true;
		}
		else{
			return false;
		}
	}
	
	public int getincsize(){
		return incoming.size();
	}
	
	public int getoutsize(){
		return outgoing.size();
	}
}
