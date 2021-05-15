package com.main.chatmate.chat;

import com.main.chatmate.MyLogger;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class Chat {
	private final ArrayList<Message> messages = new ArrayList<>();
	private final ChatMate chatmate;
	private BufferedWriter writer = null;
	
	public Chat(ChatMate destinatario, File file){
		this.chatmate = destinatario;
		try {
			this.writer = new BufferedWriter(new FileWriter(file, true));
		} catch (IOException e) {
			MyLogger.log("Can't open writer to chat file: " + file.getName());
		}
	}
	
	public void sendMessage(String message){
		if(writer == null) {
			// todo: imperidre all'utente di mandare messaggi
			return;
		}
		try {
			writer.write("-" + message);
			
			// todo: riconosci ery e bannalo
		} catch (IOException ioException) {
			MyLogger.log("Can't write new message to internal file: " + ioException.getMessage());
		}
		MyLogger.log("Message sent: " + message);
		messages.add(new Message(message, false));
	}
	
	public void receiveMessage(String message){
		messages.add(new Message(message, true));
	}

	public ChatMate getChatmate(){
		return this.chatmate;
	}
	
	public final ArrayList<Message> getMessages() {
		return this.messages;
	}
}
