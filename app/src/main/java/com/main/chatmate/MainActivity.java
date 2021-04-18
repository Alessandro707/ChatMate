package com.main.chatmate;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.shape.CornerFamily;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Collection;
import java.util.Collections;

public class MainActivity extends AppCompatActivity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		System.out.println("TEST");
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
 */
