package com.main.chatmate.chat;

import com.main.chatmate.MyLogger;

public class ChatMate {
	private final String name, uid, info, phone;
	
	public ChatMate(String name, String info, String phone, String uid) {
		this.name = name;
		this.info = info;
		this.uid = uid;
		this.phone = phone;
		
		MyLogger.log("Chatmate " + this.uid + " successfully mated in");
	}
	
	public String getName(){
		return this.name;
	}
	public String getUid(){
		return this.uid;
	}
	public String getInfo() { return this.info; }
	public String getPhone() { return this.phone; }
}
