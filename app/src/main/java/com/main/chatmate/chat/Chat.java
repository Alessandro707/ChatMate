package com.main.chatmate.chat;

import android.content.Context;

import com.main.chatmate.MyLogger;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class Chat {
	private final ArrayList<Message> messages = new ArrayList<>();
	private final ChatMate chatmate;
	private BufferedWriter writer = null; // todo: close writers
	private File file;
	
	public Chat(ChatMate destinatario, File file){
		this.chatmate = destinatario;
		try {
			this.file = file;
			this.writer = new BufferedWriter(new FileWriter(file, true));
		} catch (IOException e) {
			MyLogger.log("Can't open writer to chat file: " + file.getName());
		}
	}
	
	public void sendMessage(String message){
		if(writer == null) {
			// todo: impedire all'utente di mandare messaggi
			return;
		}
		try {
			writer.write("$-" + message + "\n");
			writer.flush();
			
			messages.add(new Message(message, false));
			// TODO: fallo davvero però
			MyLogger.log("Message sent: " + message);
			// todo: riconosci ery e bannalo
		} catch (IOException ioException) {
			MyLogger.log("Can't write new message to internal file: " + ioException.getMessage());
		}
	}
	
	public void sendMessage(String message, Context context){
		try (FileOutputStream fos = context.openFileOutput(file.getName(), Context.MODE_APPEND)) {
			fos.write((message + "\n").getBytes());
			fos.close();
			messages.add(new Message(message, false));
			MyLogger.log("Message sent: " + message);
		} catch (IOException ioException) {
			MyLogger.log("Can't write new message to internal file: " + ioException.getMessage());
		}
	}
	
	public void receiveMessage(String message){
		if(writer == null) {
			return;
		}
		try {
			writer.write("&-" + message + "\n");
			MyLogger.log("Message sent: " + message);
			messages.add(new Message(message, true));
			// todo: rimuovi dal rtdb
		} catch (IOException ioException) {
			MyLogger.log("Can't write new message to internal file: " + ioException.getMessage());
		}
	}
	
	public void loadMessage(String message, boolean received){
		messages.add(new Message(message, received));
	}

	public ChatMate getChatmate(){
		return this.chatmate;
	}
	
	public final ArrayList<Message> getMessages() {
		return this.messages;
	}
}
