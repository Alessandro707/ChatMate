package com.main.chatmate.chat;

public class ChatMate {
	private final String name;
	private final String uid;
	
	public ChatMate(String name, String uid){
		this.name = name;
		this.uid = uid;
	}
	
	public String getName(){
		return this.name;
	}
	
	public String getUid(){
		return this.uid;
	}
}
