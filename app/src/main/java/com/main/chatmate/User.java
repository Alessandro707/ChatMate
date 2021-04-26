package com.main.chatmate;

import android.content.Context;

import com.google.firebase.auth.FirebaseAuth;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class User {
	private static User user;
	private boolean logged = false;
	private String name = "";
	private String uid = "";
	private final ArrayList<Chat> chats = new ArrayList<>();
	
	private static final int NAME = 0;
	
	private User() {
	
	}
	
	public String getName(){
		return name;
	}
	
	public ArrayList<Chat> getChats() {
		return chats;
	}
	
	public void loadChats(Context context) {
		File[] files = context.getFilesDir().listFiles();
		if(files == null)
			return;
		for(File file : files){
			try {
				BufferedReader reader = new BufferedReader(new FileReader(file));
				
				String line = "";
				while((line = reader.readLine()) != null){
				
				}
			} catch (FileNotFoundException e) {
				MyLogger.log("Failed to open chat file: " + file.getName());
			} catch (IOException e) {
				MyLogger.log("Failed to read chat from file: " + file.getName());
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
	}
	
	public static User get(){
		if(user == null)
			user = new User();
		
		return user;
	}
	
	
	public boolean logIn(byte[] data) {
		if (logged) {
			MyLogger.log("INTERNAL USER ALREADY LOGGED IN");
			return false;
		}
		
		
		String[] info = MyHelper.byteArrayToString(data).split("\n");
		this.name = info[NAME];
		
		this.uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
		
		MyLogger.log("Internal user " + this.uid + " successfully logged in");
		
		return logged = true;
	}
	
	public void logOut() {
		name = "";
		chats.clear();
		MyLogger.log("Internal user " + this.uid + " logged out");
		logged = false;
	}
}
