package com.main.chatmate;

import java.nio.charset.StandardCharsets;

public interface MyHelper {
	static String byteArrayToString(byte[] data){
		return new String(data, 0, data.length, StandardCharsets.UTF_8);
	}
}
