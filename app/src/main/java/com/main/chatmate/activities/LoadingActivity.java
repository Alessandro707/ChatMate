package com.main.chatmate.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ProgressBar;

import com.main.chatmate.MyLogger;
import com.main.chatmate.R;

import java.util.Timer;
import java.util.TimerTask;

public class LoadingActivity extends AppCompatActivity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_loading);
		MyLogger.log("Loading activity started successfully");
		
		ProgressBar progressBar = findViewById(R.id.loading_wheel_progressBar);
		
		Timer timerBar = new Timer();
		timerBar.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				progressBar.incrementProgressBy(10);
			}
		}, 100, 100);
	}
}