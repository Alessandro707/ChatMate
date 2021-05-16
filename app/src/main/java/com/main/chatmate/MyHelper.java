package com.main.chatmate;

import android.net.Uri;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public interface MyHelper {
	static String byteArrayToString(byte[] data){
		return new String(data, 0, data.length, StandardCharsets.UTF_8);
	}
	static String getFormat(Uri uri){
		
		return uri.getPath().substring((uri.getPath().lastIndexOf("."))+1);
	}
}