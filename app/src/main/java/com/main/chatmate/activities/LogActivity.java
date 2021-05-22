package com.main.chatmate.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ListView;

import com.main.chatmate.MyLogger;
import com.main.chatmate.R;
import com.main.chatmate.adapters.LogAdapter;

import java.util.ArrayList;

public class LogActivity extends AppCompatActivity {
	public static ArrayList<String> log = new ArrayList<>();
	public static ArrayList<String> debug = new ArrayList<>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_log);
		MyLogger.log("Log activity started successfully");
		
		ListView log = findViewById(R.id.log_log_listView);
		log.setAdapter(new LogAdapter());
	}
}