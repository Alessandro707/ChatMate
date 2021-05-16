package com.main.chatmate.chat;


import java.time.LocalDateTime;
import java.time.ZoneId;

public class Message {
	private final String message;
	private final LocalDateTime data;
	
	public Message (String message) {
		this.message = message;
		this.data = LocalDateTime.now(ZoneId.of("Europe/Italy"));
	}

}
