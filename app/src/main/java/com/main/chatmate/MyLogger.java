package com.main.chatmate;

import android.util.Log;


public interface MyLogger {
	
	static void log(Object text){
		Log.i("ChatMate", String.valueOf(text));
	}
	
	static void debug(String text){
		Log.i("MyDebug", text);
	}
}
