package com.nbot.core;

import java.util.ArrayList;

public class CommandBuffer extends Thread{
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
	
	public void writeIncoming(Command inc){
		getIncomingLock(inc.getClient());
		this.incoming.add(inc.clone());
		releaseOutgoingLock(inc.getClient());
	}
	
	public void writeOutgoing(Response out){
		getOutgoingLock(out.getClient());
		this.outgoing.add(out);
		releaseOutgoingLock(out.getClient());
	}
	
	private boolean getIncomingLock(String client){
		while(this.incomeaccessor != "none"){}
		this.incomeaccessor = client;
		return true;
	}
	
	private boolean releaseIncomingLock(String client){
		if(this.incomeaccessor == client){
			this.incomeaccessor = "none";
			return true;
		}
		else{
			return false;
		}
	}
	
	private boolean getOutgoingLock(String client){
		while(this.outgoaccessor != "none"){}
		this.outgoaccessor = client;
		return true;
	}
	
	private boolean releaseOutgoingLock(String client){
		if(this.outgoaccessor == client){
			this.outgoaccessor = "none";
			return true;
		}
		else{
			return false;
		}
	}
}
