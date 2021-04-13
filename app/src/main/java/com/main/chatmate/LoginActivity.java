package com.main.chatmate;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class LoginActivity extends AppCompatActivity {
	private final PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
		@Override
		public void onVerificationCompleted(PhoneAuthCredential credential) {
			signInWithPhoneAuthCredential(credential);
		}
		
		@Override
		public void onVerificationFailed(FirebaseException e) {
			if (e instanceof FirebaseAuthInvalidCredentialsException) {
				// Invalid request
			} else if (e instanceof FirebaseTooManyRequestsException) {
				// The SMS quota for the project has been exceeded
			}
		}
		
		@Override
		public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken token) {
			String mVerificationId = verificationId;
			PhoneAuthProvider.ForceResendingToken mResendToken = token;
		}
	};
	
	
	@Override
	protected void onStart() {
		super.onStart();
		
		FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
		if (user != null) {
			Intent intent = new Intent(LoginActivity.this, MainActivity.class);
			startActivity(intent);
		}
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		
		EditText numero = findViewById(R.id.login_text_phonenumber);
		Button login = findViewById(R.id.login_button);
		
		login.setOnClickListener(v -> {
			// FORMATI SUPPORTATI CHE HO SCOPERTO:
			//+1 650-555-3434
			//+393479978847
			PhoneAuthOptions options = PhoneAuthOptions.newBuilder(FirebaseAuth.getInstance())
					.setPhoneNumber(numero.getText().toString())
					.setTimeout(10L, TimeUnit.MINUTES)
					.setActivity(this).setCallbacks(mCallbacks)
					.build();
			
			PhoneAuthProvider.verifyPhoneNumber(options);
		});
	}
	
	private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
		FirebaseAuth.getInstance().signInWithCredential(credential)
				.addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
					@Override
					public void onComplete(@NonNull Task<AuthResult> task) {
						if (task.isSuccessful()) {
							FirebaseUser user = Objects.requireNonNull(task.getResult()).getUser();
						} else {
							if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
								// The verification code entered was invalid
							}
						}
					}
				});
	}
	
}
