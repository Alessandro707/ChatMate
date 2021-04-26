package com.main.chatmate.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.widget.TextView;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.ListResult;
import com.main.chatmate.MyLogger;
import com.main.chatmate.R;
import com.main.chatmate.User;

import java.io.FileOutputStream;
import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends AppCompatActivity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		MyLogger.log("Main activity started successfully");
		
		TextView chatcount = findViewById(R.id.main_username_textView);
		User.get().loadChats(getApplicationContext());
		chatcount.setText(User.get().getChats().size());
		
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
*/