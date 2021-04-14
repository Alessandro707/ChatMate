package com.main.chatmate;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.io.BufferedWriter;
import java.util.concurrent.TimeUnit;

public class LoginActivity extends AppCompatActivity {
	private String mVerificationId;
	private boolean codeSent = false;
	private final PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
		@Override
		public void onVerificationCompleted(PhoneAuthCredential credential) {
			MyLogger.log("Verification completed");
			signInWithPhoneAuthCredential(credential); // credenziali inserite corrette, accesso
		}
		
		@Override
		public void onVerificationFailed(FirebaseException e) {
			MyLogger.log("Login verification failed:");
			if (e instanceof FirebaseAuthInvalidCredentialsException) {
				// Invalid request
				MyLogger.log("    invalid credentials");
			} else if (e instanceof FirebaseTooManyRequestsException) {
				// The SMS quota for the project has been exceeded
				MyLogger.log("    TO MANY REQUESTS!");
			}
		}
		
		@Override
		public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken token) {
			mVerificationId = verificationId;
			codeSent = true;
			MyLogger.log("Login verification code sent");
		}
	};
	
	@Override
	protected void onStart() {
		super.onStart();
		
		FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser(); // utente già loggato, easy
		if (user != null) {
			MyLogger.log("User logged in");
			Intent intent = new Intent(LoginActivity.this, MainActivity.class);
			startActivity(intent);
		}
		// utente non loggato, oh shiet
		MyLogger.log("Login activity started successfully");
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		
		EditText numero = findViewById(R.id.login_phoneNumber_editText);
		Button sendCode = findViewById(R.id.login_sendVerificationCode_Button);
		EditText code_field = findViewById(R.id.login_verificationCode_editText);
		Button login = findViewById(R.id.login_checkVerificationCode_button);
		TextView infoField = findViewById(R.id.login_info_textView);
		
		// FORMATI SUPPORTATI CHE HO SCOPERTO:
		//+1 650-555-3434
		//+393479978847
		sendCode.setOnClickListener(v -> {
			if(!numero.getText().toString().isEmpty()) {
				PhoneAuthOptions options = PhoneAuthOptions.newBuilder(FirebaseAuth.getInstance())
						.setPhoneNumber(numero.getText().toString())
						.setTimeout(120L, TimeUnit.SECONDS) // tempo dopo il quale il codice di verifica scade
						.setActivity(this).setCallbacks(mCallbacks)
						.build();
				
				PhoneAuthProvider.verifyPhoneNumber(options);
			}
			else {
				infoField.setText("Insert a phone number (e.g. +16505553434)");
			}
		});
		
		login.setOnClickListener(v -> {
			if(!codeSent)
				return;
			
			String code = code_field.getText().toString();
			if(!code.isEmpty() && code.length() >= 6) {
				PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, code);
				signInWithPhoneAuthCredential(credential);
			}
			else {
				infoField.setText("The verification code must be at least 6 characters long");
			}
		});
		
		MyLogger.log("Login activity created successfully");
	}
	
	private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
		FirebaseAuth.getInstance().signInWithCredential(credential).addOnCompleteListener(this, task -> {
			if (task.isSuccessful()) {
				// L'utente esiste yeee
				MyLogger.log("Login succeded");
				Intent intent = new Intent(LoginActivity.this, MainActivity.class);
				startActivity(intent);
			} else {
				if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
					// The verification code entered was invalid
					TextView infoField = findViewById(R.id.login_info_textView);
					infoField.setText("INVALID VERIFICATION CODE");
					EditText code_field = findViewById(R.id.login_verificationCode_editText);
					code_field.setText("");
					MyLogger.log("Invalid Code");
				}
			}
		});
	}
	
}
