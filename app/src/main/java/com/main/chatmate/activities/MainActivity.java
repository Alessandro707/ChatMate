package com.main.chatmate.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.main.chatmate.MyLogger;
import com.main.chatmate.R;
import com.main.chatmate.chat.Chat;
import com.main.chatmate.chat.ChatMate;
import com.main.chatmate.chat.ChatsAdapter;
import com.main.chatmate.chat.User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

// https://github.com/Alessandro707/ChatMate
public class MainActivity extends AppCompatActivity {
	private boolean chatsLoaded = false;
	private ChatsAdapter chatsAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		MyLogger.log("Main activity created successfully");
		
		Button newChat = findViewById(R.id.main_newChat_button);
		Button delete = findViewById(R.id.main_deleteChats_button2);
		ListView chats = findViewById(R.id.main_chats_listView);
		
		if(!chatsLoaded) {
			User.get().loadChats(getApplicationContext());
			chatsLoaded = true;
		}
		
		chatsAdapter = new ChatsAdapter();
		chats.setAdapter(chatsAdapter);
		
		newChat.setOnClickListener(v -> {
			Intent contactsActivity = new Intent(MainActivity.this, ContactsActivity.class);
			startActivity(contactsActivity);
		});
		
		if (getIntent().getExtras() != null && getIntent().getExtras().get("newChatmatePhone") != null){
			createNewChat(String.valueOf(getIntent().getExtras().get("newChatmatePhone")));
		}
		
		delete.setOnClickListener(this::deleteAllChats);

		DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
		FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
		HashMap<String, Boolean> chatMap = new HashMap<>();
		ref.child("users/"+ user.getUid() + "/chats").addChildEventListener(new ChildEventListener() {
			@Override
			public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {
				Map<String, Object> data =  (Map<String, Object>)dataSnapshot.getValue();

				for (Map.Entry<String, Object> pepperoncino:data.entrySet()) {
					if(!chatMap.containsKey(pepperoncino.getKey())){
						chatMap.put(pepperoncino.getKey(), true);
						ref.child("users/"+ user.getUid() + "/chats/"+ pepperoncino.getKey()).addChildEventListener(new ChildEventListener() {
							@Override
							public void onChildAdded(DataSnapshot messageSnapshot, String prevChildKey) {
								Map<String, String> messages= (Map<String, String>)messageSnapshot.getValue();
								boolean esistelachiattona = false;
								for (Map.Entry<String, String > melanzoni:messages.entrySet()) {
									for (Chat chiattona:User.get().getChats()) {
										if(chiattona.getChatmate().getUid().equals(melanzoni.getKey())){
											chiattona.receiveMessage(melanzoni.getValue());
											esistelachiattona=true;
											break;
										}
									}
									if(!esistelachiattona){
										ref.child("users/"+pepperoncino.getKey()+"/name").get().addOnCompleteListener(nameTask -> {
											String name=nameTask.getResult().getValue(String.class);
											if(name != null) {
												ref.child("users/"+pepperoncino.getKey()+"/info").get().addOnCompleteListener(infoTask -> {
													String info = infoTask.getResult().getValue(String.class);
													if(info == null)
														info = "";
													String finalInfo = info;
													ref.child("users/"+pepperoncino.getKey()+"/info").get().addOnCompleteListener(phoneTask -> {
														String phone= phoneTask.getResult().getValue(String.class);
														User.get().createChat(new ChatMate(name, finalInfo,phone, pepperoncino.getKey()),getApplicationContext());
														User.get().getChats().get(User.get().getChats().size()-1).receiveMessage(melanzoni.getValue());
													});
												});
											}
										});
									}
								}
								ref.child("users/"+ user.getUid() + "/chats/"+ pepperoncino.getKey()).removeValue();
							}

							@Override
							public void onChildChanged(DataSnapshot dataSnapshot, String prevChildKey) {}

							@Override
							public void onChildRemoved(DataSnapshot dataSnapshot) {}

							@Override
							public void onChildMoved(DataSnapshot dataSnapshot, String prevChildKey) {}

							@Override
							public void onCancelled(DatabaseError databaseError) {}
						});
					}
				}
			}

			@Override
			public void onChildChanged(DataSnapshot dataSnapshot, String prevChildKey) {}

			@Override
			public void onChildRemoved(DataSnapshot dataSnapshot) {}

			@Override
			public void onChildMoved(DataSnapshot dataSnapshot, String prevChildKey) {}

			@Override
			public void onCancelled(DatabaseError databaseError) {}
		});
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
			if(phoneTask.getResult() == null) {
				// TODO: l'utente non esiste, non si può creare la chat, mandare invito a chatmate al contatto selezionato
				MyLogger.log("The contact selected doesn't have a chatmate account!");
				return;
			}
			
			String uid = phoneTask.getResult().getValue(String.class);
			
			if(uid == null){
				// TODO: l'utente non esiste, non si può creare la chat, mandare invito a chatmate al contatto selezionato
				MyLogger.log("The contact selected doesn't have a chatmate account!");
				return;
			}
			
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
				
				try {
					HashMap<String, Object> dati = (HashMap<String, Object>) mateTask.getResult().getValue();
					if (dati != null && dati.containsKey("name") && dati.containsKey("info")) {
						ChatMate chatmate = new ChatMate(String.valueOf(dati.get("name")), String.valueOf(dati.get("info")), phone, uid);
						if (User.get().createChat(chatmate, getApplicationContext()).equals(User.CreateResult.OK)) {
							chatsAdapter.notifyDataSetChanged();
						} else {
							// todo: informa l'utente
						}
					} else {// l'utente non dispone dei dati sufficienti, non è possibile creare la chat
						MyLogger.log("The contact selected has a chatmate account but without the necessary info");
					}
				}catch (ClassCastException e){
					MyLogger.log("WRONG FORMAT OF THE CHATMATE'S DATA FROM RTDB: " + e.getMessage());
				}
			});
		});
		
	}
	
	
	private void deleteAllChats(View v){
		User.get().deleteAllChats(getApplicationContext());
		chatsAdapter.notifyDataSetChanged();
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

50€ scaro
2€/h gio * 2

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
			MyLogger.log("Contact selected to create new chat doesn't have chatmate");
		}
		
		 */