package com.nbot.externals;

import java.util.ArrayList;

public class PatreonCreator {
	private String cname;
	private String patreonID;
	private ArrayList<Integer> levels;
	
	public PatreonCreator(String n){
		this.cname = n;
		this.patreonID = "";
		this.levels = new ArrayList<Integer>();
	}
	
	public ArrayList<Integer> getlevels(){
		return this.levels;
	}
	
	public String getname(){
		return this.cname;
	}
	
	public void setid(String id){
		this.patreonID = id;
	}
	
	public String getid(){
		return this.patreonID;
	}
	
	public void addLevel(int i){
		this.levels.add(i);
	}
}
