package com.main.chatmate.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.main.chatmate.chat.ChatAdapter;
import com.main.chatmate.MyLogger;
import com.main.chatmate.R;
import com.main.chatmate.chat.ChatMate;
import com.main.chatmate.chat.User;

import java.util.HashMap;

import com.fasterxml.jackson.core.async.ByteArrayFeeder;
import com.fasterxml.jackson.core.async.ByteBufferFeeder;
import com.fasterxml.jackson.core.async.NonBlockingInputFeeder;

import com.firebase.ui.auth.ui.phone.CheckPhoneHandler;
import com.firebase.ui.auth.ui.phone.CheckPhoneNumberFragment;
import com.firebase.ui.auth.ui.phone.CountryListSpinner;
import com.firebase.ui.auth.ui.phone.PhoneActivity;
import com.firebase.ui.auth.ui.phone.PhoneVerification;
import com.firebase.ui.auth.ui.phone.PhoneNumberVerificationHandler;
import com.firebase.ui.auth.ui.phone.SpacedEditText;
import com.firebase.ui.auth.ui.phone.SubmitConfirmationCodeFragment;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.main.chatmate.MyLogger;
import com.main.chatmate.R;
import com.main.chatmate.chat.User;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.main.chatmate.FirebaseHandler;
import com.main.chatmate.MyHelper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.List;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;



public class MainActivity extends AppCompatActivity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		MyLogger.log("Main activity created successfully");
		
		
		TextView chatcount = findViewById(R.id.main_username_textView);
		Button newChat = findViewById(R.id.main_newChat_button);
		ListView chats = findViewById(R.id.main_chats_listView);
		
		// TODO: show initial chats
		if(!User.get().areChatsLoaded()) {
			User.get().loadChats(getApplicationContext());
			
			ChatAdapter adapter = new ChatAdapter();
			chats.setAdapter(adapter);
		}
		chatcount.setText(User.get().getChats().size() + "");
		
		newChat.setOnClickListener(this::selectNewChatmate);
		
		if (getIntent().getExtras() != null && getIntent().getExtras().get("newChatmatePhone") != null){
			createNewChat(String.valueOf(getIntent().getExtras().get("newChatmatePhone")));
		}
	}
	
	private void selectNewChatmate(View view) {
		Intent contactsActivity = new Intent(MainActivity.this, ContactsActivity.class);
		startActivity(contactsActivity);
		
	}
	
	private void createNewChat(String phone) {
		MyLogger.log("Creating new chat with: " + phone);
		
		DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference();
		databaseRef.child("numbers/" + phone).get().addOnCompleteListener(phoneTask -> {
			if (!phoneTask.isSuccessful()) {
				// todo: avverti l'utente dell'errore
				MyLogger.log("Creating new chat failed: " + phoneTask.getException());
				return;
			}
			if(phoneTask.getResult() == null){
				// TODO: l'utente non esiste, non si può creare la chat, mandare invito a chatmate al contatto selezionato
				MyLogger.log("The contact selected doesn't have a chatmate account!");
				return;
			}
			
			String uid = phoneTask.getResult().getValue(String.class);
			databaseRef.child("users/" + uid).get().addOnCompleteListener(mateTask -> {
				if (!mateTask.isSuccessful()) {
					// todo: avverti l'utente dell'errore
					
					MyLogger.log("Failed to get the contact's chatmate info from the rtdb: " + mateTask.getException());
					return;
				}
				if(mateTask.getResult() == null) {
					// TODO: l'utente non esiste, non si può creare la chat, mandare invito a chatmate al contatto selezionato
					MyLogger.log("The contact selected doesn't have a chatmate account!");
					return;
				}
				
				HashMap<String, Object> dati = (HashMap<String, Object>) mateTask.getResult().getValue();
				if(dati != null && dati.containsKey("name") && dati.containsKey("info") && dati.containsKey("phone")){
					User.get().createChat(new ChatMate(String.valueOf(dati.get("name")), String.valueOf(dati.get("info")), String.valueOf(dati.get("phone")), uid), getApplicationContext());
				}
				else {// l'utente non dispone dei dati sufficienti, non è possibile creare la chat
					MyLogger.log("The contact selected has a chatmate account but without the necessary info");
				}
			});
		});
		/*
		final String[] filename = new String[1]; // user.uid
	 	DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child(phone);
		reference.addChildEventListener(new ChildEventListener() {
			@Override
			public void onChildAdded(@NonNull DataSnapshot dataSnapshot, String prevChildKey) {
				filename[0] = dataSnapshot.getValue(String.class);
				reference.removeEventListener(this);
				
			}
			
			@Override
			public void onCancelled(@NonNull DatabaseError databaseError) {
				MyLogger.log("Failed to get the uid from the database of " + phone + ": " + databaseError.getMessage());
			}
			
			@Override
			public void onChildChanged(@NonNull DataSnapshot dataSnapshot, String prevChildKey) {}
			@Override
			public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {}
			@Override
			public void onChildMoved(@NonNull DataSnapshot dataSnapshot, String prevChildKey) {}
		});
		
		if(filename[0] != null) {
			String fileContents = "";
			try (FileOutputStream fos = openFileOutput(filename[0], Context.MODE_PRIVATE)) {
				fos.write(fileContents.getBytes());
				fos.close();
				MyLogger.log("Created new chat file: " + filename[0]);
			} catch (FileNotFoundException e) {
				MyLogger.log("Non è possibile creare il file: " + filename[0]);
			} catch (IOException e) {
				MyLogger.log("Non è possibile scrivere sul file: " + filename[0]);
			}
		}
		else {
			// todo: mandare invito a quel contatto per downloaddare chattomatto
			MyLogger.log("Contact selected to create new chat doesn't have chatmate");
		}
		
		 */
	}
}

/*
rtdb/
	users/
		joidoh49898nogvsef/
			name:"badinelli"
			info:null
			number:3582095810
		02rwhvwieu34dad09u/
			name:"badinelli"
			info:null
			number:3582095810
	numbers/
		3582095810:joidoh49898nogvsef
	chats/
		joidoh49898nogvsef/
			02rwhvwieu34dad09u/
			
	
Quando un utente viene creato, si crea anche una cartella in Firebase Storage con il suo id (i guess)
Quella cartella conterrà tutti i file (chat.chatmate per la chat) che l'altro utente deve ANCORA ricevere
al momento della ricezione devono essere eliminati.


FORMATO CHAT
- = testo
+ = documento
& = altro utente nella chat
id: = altri utenti in un gruppo
$ = io

esempio:
&-ciao
$-send nudes
&+hentai.png <- nome del file da scaricare sul server, poi convertito in percorso nel file system del ricevente
$+baeh.mp3

info.chatmate:
nome\n
lunghezza del membro\n


https://firebase.google.com/docs/storage/android/download-files per i download nel filesystem del dispositivo
FirebaseUI per download di immagini

// todo: chat col bot all'inizio come tutorial, punti chat con cui sbloccare emote che si ottengono completando obiettivi
// todo: app incrociata con quella di yaya
*/