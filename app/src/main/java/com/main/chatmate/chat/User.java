package com.main.chatmate.chat;

import android.content.Context;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.main.chatmate.FirebaseHandler;
import com.main.chatmate.MyHelper;
import com.main.chatmate.MyLogger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class User {
	private static User user;
	private boolean logged = false;
	private String name = "", uid = "", info = "";
	private final ArrayList<Chat> chats = new ArrayList<>();
	private boolean chatsLoaded = false;
	
	private static final int NAME = 0, INFO = 1;
	
	private User() {
	
	}
	
	public String getName(){
		return name;
	}
	public final ArrayList<Chat> getChats() {
		return chats;
	}
	public boolean areChatsLoaded() {
		return chatsLoaded;
	}
	
	public void loadChats(Context context) {
		File[] files = context.getFilesDir().listFiles();
		if(files == null)
			return;
		for(File file : files){ // nome del file è l'UID
			DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference().child("users/" + file.getName());
			databaseRef.get().addOnCompleteListener(dataTask->{
				if (!dataTask.isSuccessful()) {
					// todo: avverti l'utente dell'errore
					return;
				}
				if(dataTask.getResult() == null) { // l'utente è stato eliminato e si mostra la chat con le informazioni dell'utente salvate in locale. // todo: aggiorna le info del chatmate ogni tot
					try {
						BufferedReader reader = new BufferedReader(new FileReader(file));
						String line = reader.readLine();
						String[] info = line.split("-");
						chats.add(new Chat(new ChatMate(info[0], info[1], info[2], file.getName())));
						
						while ((line = reader.readLine()) != null) {
							// TODO: send / recieve
							chats.get(chats.size() - 1).receiveMessage(line);
						}
					} catch (FileNotFoundException e) {
						MyLogger.log("Failed to open chat file: " + file.getName());
					} catch (IOException e) {
						MyLogger.log("Failed to read chat from file: " + file.getName());
					}
					return;
				}
				
				HashMap<String, Object> dati = (HashMap<String, Object>) dataTask.getResult().getValue();
				if(dati != null && dati.containsKey("name") && dati.containsKey("info") && dati.containsKey("phone")){
					chats.add(new Chat(new ChatMate(String.valueOf(dati.get("name")), String.valueOf(dati.get("info")), String.valueOf(dati.get("phone")), file.getName())));
					
					try {
						BufferedReader reader = new BufferedReader(new FileReader(file));
						String line = reader.readLine(); // prima linea contiene informazioni sull'utente
						while ((line = reader.readLine()) != null) {
							// TODO: send / recieve
							chats.get(chats.size() - 1).receiveMessage(line);
						}
					} catch (FileNotFoundException e) {
						MyLogger.log("Failed to open chat file: " + file.getName());
					} catch (IOException e) {
						MyLogger.log("Failed to read chat from file: " + file.getName());
					}
				}
			});
			/*
			FirebaseHandler.download(FirebaseStorage.getInstance().getReference().child(file.getName()), 1024 * 1024, bytes -> {
				chats.add(new Chat(new ChatMate(bytes, file.getName()))); // <- uid: nome del file, name: db
				
				try {
					BufferedReader reader = new BufferedReader(new FileReader(file));
					String line;
					while ((line = reader.readLine()) != null) {
						chats.get(chats.size() - 1).receiveMessage(line);
					}
				} catch (FileNotFoundException e) {
					MyLogger.log("Failed to open chat file: " + file.getName());
				} catch (IOException e) {
					MyLogger.log("Failed to read chat from file: " + file.getName());
				}
				
				if(file.equals(files[files.length - 1]))
					this.chatsLoaded = true;
			}, e -> MyLogger.log("Can't get the info of user: " + file.getName()));
			
			 */
		}
	}
	
	public boolean createChat(ChatMate chatmate, Context context) {
		// TODO: add to the chats list on screen (and create folder on db)
		
		try (FileOutputStream fos = context.openFileOutput(chatmate.getUid(), Context.MODE_PRIVATE)) {
			fos.write((chatmate.getName() + "-" + chatmate.getInfo() + "-" + chatmate.getPhone()).getBytes());
			chats.add(new Chat(chatmate));
			MyLogger.log("New chat created: " + chatmate.getUid());
		} catch (FileNotFoundException e) {
			MyLogger.log("CAN'T CREATE NEW CHAT FILE: " +  chatmate.getUid()); // todo: informa l'utente
			return false;
		} catch (IOException e) {
			MyLogger.log("CAN'T WRITE ON THE NEW CHAT FILE: " +  chatmate.getUid());
			return false;
		}
		
		return true;
	}
	
	/*
	public void addChat(File chat) {
		try {
			BufferedReader reader = new BufferedReader(new FileReader(chat));
			chats.add(new Chat(new ChatMate("test", "a", "3479978848", "123")));
			
			String line;
			while((line = reader.readLine()) != null){
				chats.get(chats.size() - 1).receiveMessage(line);
			}
		} catch (FileNotFoundException e) {
			MyLogger.log("Failed to open chat chat: " + chat.getName());
		} catch (IOException e) {
			MyLogger.log("Failed to read chat from chat: " + chat.getName());
		}
	}
	 */
	
	public static User get() {
		if(user == null)
			user = new User();
		
		return user;
	}
	
	/*
	public boolean logIn(byte[] data) {
		if (logged) {
			MyLogger.log("INTERNAL USER ALREADY LOGGED IN");
			return false;
		}
		
		
		String[] info = MyHelper.byteArrayToString(data).split("\n");
		this.name = info[NAME];
		this.info = info[INFO];
		
		this.uid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
		
		MyLogger.log("Internal user " + this.uid + " successfully logged in");
		
		return logged = true;
	}
	 */
	public boolean logIn(String nome, String info){
		if (logged) {
			MyLogger.log("INTERNAL USER ALREADY LOGGED IN");
			return false;
		}
		if(FirebaseAuth.getInstance().getCurrentUser() == null){
			MyLogger.log("NO USER LOGGED IN TO FIREBASE, CAN'T LOGIN");
			return false;
		}
		this.uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
		
		this.name = nome;
		this.info = info;
		
		MyLogger.log("Internal user " + this.uid + " logged in");
		
		return logged = true;
	}
	
	public void logOut() {
		name = "";
		chats.clear();
		chatsLoaded = false;
		MyLogger.log("Internal user " + this.uid + " logged out");
		logged = false;
	}
}


/*
		StorageReference storageRef = FirebaseStorage.getInstance().getReference().child(uid);
		
		Task<ListResult> task = storageRef.listAll();
		task.addOnSuccessListener(listResult -> {
			for(StorageReference dir : listResult.getPrefixes()) {
				// tutti gli uid degli user con la chat pazzazzazzazza
				
				FirebaseHandler.getAllFilesUnderReference(dir, chatResult -> {
					// chat con un utente
					if(chatResult.getItems().size() == 0) {
						// upload empty chat file
						
						FirebaseHandler.upload(dir.child("chat.chatmate"), new byte[]{},
								taskSnapshot -> MyLogger.log("Created chat file with user: " + dir.getName()),
								e -> MyLogger.log("FAILED TO CREATE A CHAT WITH USER: " + dir));
					}
					else{
						// read chats' files
						for(StorageReference item : chatResult.getItems()) {
							this.chats.add(new Chat());
						}
					}
					MyLogger.log("User's chats loaded: " + this.chats.size());
					}, chatException -> {
					MyLogger.log("FAILED TO READ THE CHAT WITH: " + dir.getName() + ": " + chatException.getMessage());
				});
			}
		});
		task.addOnFailureListener(exception -> {
			// Uh-oh, an error occurred!
			MyLogger.log("FAILED TO LOAD THE CHATS: " + exception.getMessage());
		});
		
		FirebaseHandler.getAllFilesUnderReference(storageRef, listResult -> {
			for(StorageReference dir : listResult.getPrefixes()) {
				// tutti gli uid degli user con la chat pazzazzazzazza
				
				FirebaseHandler.getAllFilesUnderReference(dir, chatResult -> {
					// chat con un utente
					if(chatResult.getItems().size() == 0) {
						// upload empty chat file
						
						FirebaseHandler.upload(dir.child("chat.chatmate"), new byte[]{},
								taskSnapshot -> MyLogger.log("Created chat file with user: " + dir.getName()),
								e -> MyLogger.log("FAILED TO CREATE A CHAT WITH USER: " + dir));
					}
					else{
						// read chats' files
						for(StorageReference item : chatResult.getItems()) {
							this.chats.add(new Chat());
						}
					}
					MyLogger.log("User's chats loaded: " + this.chats.size());
				}, chatException -> {
					MyLogger.log("FAILED TO READ THE CHAT WITH: " + dir.getName() + ": " + chatException.getMessage());
				});
			}
		}, exception -> {
			// Uh-oh, an error occurred!
			MyLogger.log("FAILED TO LOAD THE CHATS: " + exception.getMessage());
		});
		 */
