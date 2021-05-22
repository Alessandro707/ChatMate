package com.main.chatmate.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
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
import com.google.firebase.database.ValueEventListener;
import com.main.chatmate.MyLogger;
import com.main.chatmate.R;
import com.main.chatmate.chat.Chat;
import com.main.chatmate.chat.ChatMate;
import com.main.chatmate.chat.ChatsAdapter;
import com.main.chatmate.chat.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

// https://github.com/Alessandro707/ChatMate

/**
 * Credits:<br>
 *      BioActivity.java:       LordTimotay<br>
 *      ChatActivity.java:      LordTimotay<br>
 *      ContactAdapter.java:    LordTimotay<br>
 *      ContactsActivity.java:  LordTimotay<br>
 *      LoadingActivity.java:   LordTimotay<br>
 *      LoginActivity.java:     LordTimotay<br>
 *      LoadingActivity.java:   LordTimotay<br>
 *      MainActivity.java:      LordTimotay<br>
 *      Chat.java:              LordTimotay<br>
 *      ChatMate.java:          LordTimotay<br>
 *      ChatsAdapter.java:      LordTimotay<br>
 *      ChatsAdapter.java:      LordTimotay<br>
 *      Message.java:           LordTimotay<br>
 *      MessagesAdapter.java:   LordTimotay<br>
 *      User.java:              LordTimotay<br>
 *      Contact.java:           LordTimotay<br>
 *      FirebaseHandler.java:   LordTimotay<br>
 *      MyHelper.java:          LordTimotay<br>
 *      MyLogger.java:          LordTimotay<br>
 *      activity_bio.xml:       LordTimotay<br>
 *      activity_chat.xml:      LordTimotay<br>
 *      activity_contacts.xml:  LordTimotay<br>
 *      activity_loading.xml:   LordTimotay<br>
 *      activity_login.xml:     LordTimotay<br>
 *      activity_main.xml:      LordTimotay<br>
 *      activity_register.xml:  LordTimotay<br>
 *      chat_layout.xml:        LordTimotay<br>
 *      contact_layout.xml:     LordTimotay<br>
 *      message_layout.xml:     LordTimotay<br><br>
 *
 *      Bugs in tutti i file:   ANUBISGRAVATO BAEH
 */

public class MainActivity extends AppCompatActivity {
	private ChatsAdapter chatsAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		MyLogger.log("Main activity created successfully");
		
		Button newChat = findViewById(R.id.main_newChat_button);
		Button delete = findViewById(R.id.main_deleteChats_button2);
		ListView chats = findViewById(R.id.main_chats_listView);
		Button logout = findViewById(R.id.main_logout_button);
		Button log = findViewById(R.id.main_log_button);
		
		User.get().loadChats(getApplicationContext());
		
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
		logout.setOnClickListener(this::logout);
		log.setOnClickListener(this::log);
		
		DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
		FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
		if(user == null)
			getApplication().onTerminate(); // cattivo utente, smettila di essere nullo
		else {
			//HashMap<String, Boolean> chatMap = new HashMap<>();
			ref.child("users/" + user.getUid()).addValueEventListener(new ValueEventListener() {
				@Override
				public void onDataChange(@NonNull DataSnapshot data) {
					MyLogger.debug("CHANGE");
					if(data.getChildrenCount() == 0)
						return;
					for (DataSnapshot chat : data.child("chats").getChildren()) {
						if (chat == null)
							return;
						
						Chat chattopazzo = null;
						boolean esistelachiattona = false;
						for (DataSnapshot msg : chat.getChildren()) {
							String messagio = msg.getValue(String.class);
							if (messagio == null) {
								MyLogger.debug("null");
								continue;
							}
							if (chattopazzo != null) {
								chattopazzo.receiveMessage(messagio);
								MyLogger.debug(messagio + " gia pazzo");
								continue;
							}
							for (Chat c : User.get().getChats()) { // TODO: sostituisci con mappa di chat
								if (c.getChatmate().getUid().equals(chat.getKey())) {
									chattopazzo = c;
									c.receiveMessage(messagio);
									MyLogger.debug(messagio + " trovato!");
									esistelachiattona = true;
									break;
								}
							}
							if (esistelachiattona)
								continue;
							
							MyLogger.debug("non esiste la chiattona");
							createChat(messagio, chat);
						}
					}
					ref.child("users/" + user.getUid() + "/chats").removeValue();
				}
				
				@Override
				public void onCancelled(@NonNull DatabaseError databaseError) {
					MyLogger.log("ERROR WHILE READING CHATS' INFO FROM DATABASE: " + databaseError.getMessage());
				}
			});
		}
	}
	
	
	private void createChat(String msg, DataSnapshot chat){
		User myUser = User.get();
		DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
		ref.child("users/" + chat.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
			@Override
			public void onDataChange(@NonNull DataSnapshot data) {
				String name = (String) data.child("name").getValue();
				String info = (String) data.child("info").getValue();
				String phone = (String) data.child("phone").getValue();
				if(info == null)
					info = "";
				
				User.CreateResult result = myUser.createChat(new ChatMate(name, info, phone, chat.getKey()), getApplicationContext());
				if (result.equals(User.CreateResult.OK)) {
					myUser.getChats().get(myUser.getChats().size() - 1).receiveMessage(String.valueOf(msg));
					chatsAdapter.notifyDataSetChanged();
					MyLogger.debug("Creata");
				}
				else if(result.equals(User.CreateResult.ERROR)){
					MyLogger.debug("Erroro");
				}
				else{
					MyLogger.debug("Esiste");
				}
			}
			
			@Override
			public void onCancelled(@NonNull DatabaseError error) {
				// si ma che palle tutti sti onCancelled, non so più cosa loggare
			}
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
					@SuppressWarnings("unchecked cast")
					HashMap<String, Object> dati = (HashMap<String, Object>) mateTask.getResult().getValue();
					if (dati != null && dati.containsKey("name") && dati.containsKey("info")) {
						ChatMate chatmate = new ChatMate(String.valueOf(dati.get("name")), String.valueOf(dati.get("info")), phone, uid);
						
						User.CreateResult result = User.get().createChat(chatmate, getApplicationContext());
						if (result.equals(User.CreateResult.OK)) {
							chatsAdapter.notifyDataSetChanged();
						} else if(result.equals(User.CreateResult.EXISTS)) {
							int position = -1;
							for(int i = 0; i < User.get().getChats().size(); i++){
								if (User.get().getChats().get(i).getChatmate().getUid().equals(uid)){
									position = i;
									break;
								}
							}
							
							if(position >= 0) {
								Intent chat = new Intent(MainActivity.this, ChatActivity.class);
								chat.putExtra("chat", position);
								startActivity(chat);
							}
						} else {
							MyLogger.log("");
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
	
	private void logout(View v){
		User.get().logOut();
		FirebaseAuth.getInstance().signOut();
		
		Intent login = new Intent(MainActivity.this, LoginActivity.class);
		startActivity(login);
	}
	
	private void log(View v){
		Intent log = new Intent(MainActivity.this, LogActivity.class);
		startActivity(log);
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
6€/h gio

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

// todo: chat col bot all'inizio come tutorial, punti chat con cui sbloccare emote che si ottengono completando obbbiettivi
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


/*
				Map<String, Object> data =  (Map<String, Object>)dataSnapshot.getValue(); // ArrayList<Object>

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
				 */

/*
		DatabaseReference chatmateRef = ref.child("users/" + chat.getKey() + "/");
		chatmateRef.child("name").get().addOnCompleteListener(nameTask -> {
			if (nameTask.getResult() == null)
				return;
			
			String name = nameTask.getResult().getValue(String.class);
			if(name == null)
				return;
			
			chatmateRef.child("info").get().addOnCompleteListener(infoTask -> {
				if (infoTask.getResult() == null)
					return;
				
				String info = infoTask.getResult().getValue(String.class);
				if (info == null)
					info = "";
				
				String finalInfo = info;
				chatmateRef.child("phone").get().addOnCompleteListener(phoneTask -> {
					if (phoneTask.getResult() == null)
						return;
					
					String phone = phoneTask.getResult().getValue(String.class);
					if(phone == null)
						return;
					
					User.CreateResult result = myUser.createChat(new ChatMate(name, finalInfo, phone, chat.getKey()), getApplicationContext());
					if (result.equals(User.CreateResult.OK)) {
						myUser.getChats().get(myUser.getChats().size() - 1).receiveMessage(String.valueOf(m));
						chatsAdapter.notifyDataSetChanged();
						MyLogger.debug("Creata");
					}
					else if(result.equals(User.CreateResult.ERROR)){
						MyLogger.debug("Erroro");
					}
					else{
						MyLogger.debug("Esiste");
					}
				});
			});
		});
		
		 */

/*
	private void parseChat(DataSnapshot chat, DatabaseReference ref, FirebaseUser user){
		User myUser = User.get();
		ref.child("users/" + user.getUid() + "/chats/" + chat.getKey()).addValueEventListener(new ValueEventListener() {
			@Override
			@SuppressWarnings("unchecked cast")
			public void onDataChange(@NonNull DataSnapshot messageSnapshot) {
				MyLogger.debug("messageSnapshot: " + messageSnapshot.getKey() + " " + messageSnapshot.getChildrenCount());
				ArrayList<Object> messaggi = (ArrayList<Object>) messageSnapshot.getValue();
				if (messaggi == null)
					return;
				MyLogger.debug("messaggi: " + messaggi.size());
				
				Chat chattopazzo = null;
				boolean esistelachiattona = false;
				for (Object m : messaggi) {
					if(m == null) {
						MyLogger.debug("null");
						continue;
					}
					if(chattopazzo != null) {
						chattopazzo.receiveMessage(String.valueOf(m));
						MyLogger.debug(String.valueOf(m) + " gia pazzo");
						continue;
					}
					for (Chat c : myUser.getChats()) { // TODO: sostituisci con mappa di chat
						if (c.getChatmate().getUid().equals(chat.getKey())) {
							chattopazzo = c;
							
							c.receiveMessage(String.valueOf(m));
							MyLogger.debug(String.valueOf(m) + " trovato!");
							esistelachiattona = true;
							break;
						}
					}
					if (esistelachiattona)
						continue;
					
					MyLogger.debug("non esiste la chiattona");
					createChat(String.valueOf(m), ref, chat);
				}
				ref.child("users/" + user.getUid() + "/chats/" + chat.getKey()).removeValue();
				MyLogger.log("Updated chat from database with user " + chat.getKey());
			}
			
			@Override
			public void onCancelled(@NonNull DatabaseError databaseError) {
				MyLogger.log("ERROR WHILE READING CHAT'S INFO FROM DATABASE: " + databaseError.getMessage());
			}
		});
	}
	*/

/*
			ref.child("users/" + user.getUid() + "/chats").addValueEventListener(new ValueEventListener() {
				@Override
				public void onDataChange(@NonNull DataSnapshot chats) {
					MyLogger.debug("CHANGE");
					if(chats.getChildrenCount() == 0)
						return;
					for (DataSnapshot chat : chats.getChildren()) {
						if (chat == null)
							return;
						
						Chat chattopazzo = null;
						boolean esistelachiattona = false;
						for (DataSnapshot msg : chat.getChildren()) {
							String messagio = msg.getValue(String.class);
							if (messagio == null) {
								MyLogger.debug("null");
								continue;
							}
							if (chattopazzo != null) {
								chattopazzo.receiveMessage(messagio);
								MyLogger.debug(messagio + " gia pazzo");
								continue;
							}
							for (Chat c : User.get().getChats()) { // TODO: sostituisci con mappa di chat
								if (c.getChatmate().getUid().equals(chat.getKey())) {
									chattopazzo = c;
									c.receiveMessage(messagio);
									MyLogger.debug(messagio + " trovato!");
									esistelachiattona = true;
									break;
								}
							}
							if (esistelachiattona)
								continue;
							
							MyLogger.debug("non esiste la chiattona");
							createChat(messagio, ref, chat);
						}
					}
					ref.child("users/" + user.getUid() + "/chats").removeValue();
				}

				@Override
				public void onCancelled(@NonNull DatabaseError databaseError) {
					MyLogger.log("ERROR WHILE READING CHATS' INFO FROM DATABASE: " + databaseError.getMessage());
				}
			});
			 */