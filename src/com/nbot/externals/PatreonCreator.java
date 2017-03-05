package com.nbot.externals;

import java.util.ArrayList;

public class PatreonCreator {
	private String cname;
	private ArrayList<Integer> levels;
	
	public PatreonCreator(String n){
		this.cname = n;
		this.levels = new ArrayList<Integer>();
	}
	
	public ArrayList<Integer> getlevels(){
		return this.levels;
	}
	
	public String getname(){
		return this.cname;
	}
	
	public void addLevel(int i){
		this.levels.add(i);
	}
}
