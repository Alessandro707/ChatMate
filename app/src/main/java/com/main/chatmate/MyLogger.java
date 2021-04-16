package com.main.chatmate;

import android.util.Log;


public interface MyLogger {
	String TAG = "ChatMate";
	
	static void log(String text){
		Log.i(TAG, text);
	}
}
