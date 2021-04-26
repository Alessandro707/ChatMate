package com.main.chatmate.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.main.chatmate.FirebaseHandler;
import com.main.chatmate.MyLogger;
import com.main.chatmate.R;
import com.main.chatmate.User;

public class RegisterActivity extends AppCompatActivity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
		MyLogger.log("Register activity started successfully");
		
		TextView nome = findViewById(R.id.register_name_textView);
		Button register = findViewById(R.id.register_register_button);
		
		register.setOnClickListener(v -> {
			FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
			assert user != null;
			StorageReference storageRef = FirebaseStorage.getInstance().getReference().child(user.getUid());
			
			byte[] data = (nome.getText().toString() +"\n").getBytes();
			FirebaseHandler.upload(storageRef.child("info.chatmate"), data, taskSnapshot -> {
				MyLogger.log("New user data uploaded");
				
				// todo: prossima schermata di registrazione
				User.get().logIn(data);
				
				Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
				startActivity(intent);
			}, failureException -> {
				MyLogger.log("Can't upload new user info to database: " + failureException.getMessage());
			});
		});
	}
}