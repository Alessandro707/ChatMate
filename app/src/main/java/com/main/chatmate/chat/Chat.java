package com.main.chatmate.chat;

import java.util.ArrayList;

public class Chat {
	private final ArrayList<Message> messages = new ArrayList<>();
	private final ChatMate chatmate;
	
	public Chat(ChatMate destinatario){
		this.chatmate = destinatario;
	}
	
	public void sendMessage(String message){
		messages.add(new Message(message));
	}
	
	public void receiveMessage(String message){
		messages.add(new Message(message));
	}
	
}
