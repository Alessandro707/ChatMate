package com.main.chatmate;

public class Contact {
	private final String name, number;
	private final String image;
	
	public Contact(String name, String number,String image){
		this.name = name;
		this.number = number;
		this.image = image;
	}
	
	public String getName() {
		return this.name;
	}
	
	public String getNumber() {
		return this.number;
	}
	
	public String getImage() {
		return this.image;
	}
}
