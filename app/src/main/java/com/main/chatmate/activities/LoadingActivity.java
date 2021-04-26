package com.main.chatmate.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.main.chatmate.FirebaseHandler;
import com.main.chatmate.MyLogger;
import com.main.chatmate.R;
import com.main.chatmate.User;

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