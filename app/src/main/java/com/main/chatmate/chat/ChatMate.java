package com.main.chatmate.chat;

import com.main.chatmate.MyHelper;
import com.main.chatmate.MyLogger;

public class ChatMate {
	private final String name, uid, info;
	
	private static final int NAME = 0, INFO = 1;
	
	public ChatMate(byte[] data, String uid) {
		String[] info = MyHelper.byteArrayToString(data).split("\n");
		this.name = info[NAME];
		this.info = info[INFO];
		this.uid = uid;
		
		MyLogger.log("Chatmate " + this.uid + " successfully mated in");
	}
	
	public String getName(){
		return this.name;
	}
	
	public String getUid(){
		return this.uid;
	}
}
