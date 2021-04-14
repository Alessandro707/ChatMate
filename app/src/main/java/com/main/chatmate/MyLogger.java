package com.main.chatmate;

import android.util.Log;


public interface MyLogger {
	String TAG = "ChatMate";
	
	static void log(String text){
		Log.i(TAG, text);
	}
}

/*

package com.main.chatmate;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class MyLogger {
	private static MyLogger instance;
	private BufferedWriter writer;
	private Logger logger;
	
	private MyLogger(AppCompatActivity activity) {
		try {
			writer = new BufferedWriter(new FileWriter("E:\\AAA-AndroidStudio\\ChatMate\\app\\src\\main\\java\\com\\main\\chatmate\\log.txt"));
		} catch (IOException e) {
			System.out.println("FILE log.txt DOES'T EXIST");
			e.printStackTrace();
		}
	}
	
	public static MyLogger getInstance(AppCompatActivity activity){
		if(instance == null)
			instance = new MyLogger(activity);
		return instance;
	}
	
	public void log(String text){
		try {
			writer.write(text);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

 */