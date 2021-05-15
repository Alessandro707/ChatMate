package com.main.chatmate.chat;

public class Message {
	private final String message;
	// private final DateTime data; // TODO: implement
	private final boolean received;
	
	public Message (String message, boolean received) {
		this.message = message;
		this.received = received;
	}
	
	public String getMessage(){
		return this.message;
	}
	
	public boolean isReceived(){
		return this.received;
	}
}
