package com.main.chatmate.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.main.chatmate.chat.ChatAdapter;
import com.main.chatmate.MyLogger;
import com.main.chatmate.R;
import com.main.chatmate.chat.User;


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
		
		if(getIntent().getExtras() != null && getIntent().getExtras().get("newChatmatePhone") != null){
			createNewChat(String.valueOf(getIntent().getExtras().get("newChatmatePhone")));
		}
	}
	
	private void selectNewChatmate(View view) {
		Intent contactsActivity = new Intent(MainActivity.this, ContactsActivity.class);
		startActivity(contactsActivity);
	}
	
	private void createNewChat(String phone) {
		// TODO: implement
		// create new file, load into the User.get().loadChat(), show on screen
		
		MyLogger.log("Creating new chat with: " + phone);
		
		
	}
}

/*
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
*/