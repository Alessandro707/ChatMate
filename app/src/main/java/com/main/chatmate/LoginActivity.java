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
	private boolean codeSent = false;
	private String mVerificationId;
	private PhoneAuthProvider.ForceResendingToken mResendToken;
	private final PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
		@Override
		public void onVerificationCompleted(PhoneAuthCredential credential) {
			System.out.println("VERIFICATION COMPLETED");
			signInWithPhoneAuthCredential(credential); // credenziali inserite corrette, accesso
		}
		
		@Override
		public void onVerificationFailed(FirebaseException e) {
			System.out.println("VERIFICATION FAILED");
			if (e instanceof FirebaseAuthInvalidCredentialsException) {
				// Invalid request
			} else if (e instanceof FirebaseTooManyRequestsException) {
				// The SMS quota for the project has been exceeded
			}
		}
		
		@Override
		public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken token) {
			mVerificationId = verificationId;
			mResendToken = token;
			System.out.println("CODE SENT");
			codeSent = true;
		}
	};
	
	@Override
	protected void onStart() {
		super.onStart();
		
		FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser(); // utente già loggato, easy
		if (user != null) {
			Intent intent = new Intent(LoginActivity.this, MainActivity.class);
			startActivity(intent);
		}
		// utente non loggato, oh shiet
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		
		EditText numero = findViewById(R.id.login_text_phone_number);
		Button login = findViewById(R.id.login_button);
		EditText code_field = findViewById(R.id.login_verification_code);
		
		// FORMATI SUPPORTATI CHE HO SCOPERTO:
		//+1 650-555-3434
		//+393479978847
		login.setOnClickListener(v -> {
			if(!codeSent && !numero.getText().toString().isEmpty()) {
				PhoneAuthOptions options = PhoneAuthOptions.newBuilder(FirebaseAuth.getInstance())
						.setPhoneNumber(numero.getText().toString())
						.setTimeout(120L, TimeUnit.SECONDS) // tempo dopo il quale il codice di verifica scade
						.setActivity(this).setCallbacks(mCallbacks)
						.build();
				
				PhoneAuthProvider.verifyPhoneNumber(options);
			}
			else{
				codeSent = false;
				String code = code_field.getText().toString();
				if(!code.isEmpty() && code.length() >= 6){
					PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, code);
					signInWithPhoneAuthCredential(credential);
				}
			}
		});
	}
	
	private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
		FirebaseAuth.getInstance().signInWithCredential(credential).addOnCompleteListener(this, task -> {
			if (task.isSuccessful()) {
				System.out.println("TASK SUCCESSATA");
				// L'utente esiste yeee
			} else {
				if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
					// The verification code entered was invalid
					System.out.println("TASK FALLATA");
				}
			}
		});
	}
	
}
