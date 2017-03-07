package com.nbot.utils;

import java.util.Arrays;

public final class NBotlogger {

	private static final int MAX_NAME = 16;
	private static final String LOG = "LOG             ";
	
	public static void log(String name, String message){
		if(name.length() > 16){
			System.out.println(LOG+"> "+message);
		}
		else{
			char[] array = new char[16-name.length()];
			Arrays.fill(array, ' ');
			System.out.println(name+new String(array)+"> "+message);
		}
	}
}
