package com.main.chatmate.chat;

import android.content.Context;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.main.chatmate.FirebaseHandler;
import com.main.chatmate.MyHelper;
import com.main.chatmate.MyLogger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
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
		for(File file : files){
			FirebaseHandler.download(FirebaseStorage.getInstance().getReference().child(file.getName()), 1024 * 1024, bytes -> {
				chats.add(new Chat(new ChatMate(bytes, file.getName()))); // <- uid: nome del file, name: db
				
				try {
					BufferedReader reader = new BufferedReader(new FileReader(file));
					String line;
					while ((line = reader.readLine()) != null) {
						// TODO: send / recieve
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
		}
	}
	
	public void createChat(ChatMate chatmate) {
		// TODO: create file and add to the chats list (and create folder on db)
	}
	
	public void addChat(File chat) {
		try {
			BufferedReader reader = new BufferedReader(new FileReader(chat));
			chats.add(new Chat(new ChatMate("test".getBytes(), "123")));
			
			String line;
			while((line = reader.readLine()) != null){
				// TODO: send / recieve
				chats.get(chats.size() - 1).receiveMessage(line);
			}
		} catch (FileNotFoundException e) {
			MyLogger.log("Failed to open chat chat: " + chat.getName());
		} catch (IOException e) {
			MyLogger.log("Failed to read chat from chat: " + chat.getName());
		}
	}
	
	public static User get() {
		if(user == null)
			user = new User();
		
		return user;
	}
	
	public byte[] toDatabase (){
		return (name+"\n"+info+"\n").getBytes();
	}
	
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
