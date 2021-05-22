package com.main.chatmate;

import android.util.Log;

import com.main.chatmate.activities.LogActivity;

public interface MyLogger {
	static void log(Object text){
		Log.i("ChatMate", String.valueOf(text));
		LogActivity.log.add(String.valueOf(text));
	}
	
	static void debug(Object text){
		Log.i("MyDebug", String.valueOf(text));
		LogActivity.debug.add(String.valueOf(text));
	}
}
