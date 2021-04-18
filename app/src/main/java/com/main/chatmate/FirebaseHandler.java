package com.main.chatmate;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public interface FirebaseHandler {
	
	static void upload(StorageReference where, byte[] data, OnFailureListener failureListener, OnSuccessListener<Object> successListener){
		UploadTask uploadTask = where.putBytes(data);
		         // Handle unsuccessful uploads        // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
		uploadTask.addOnFailureListener(failureListener).addOnSuccessListener(successListener);
	}
	
	static void download(StorageReference where, long maxSize, OnFailureListener failureListener, OnSuccessListener<Object> successListener){
		where.getBytes(maxSize).addOnFailureListener(failureListener).addOnSuccessListener(successListener);
	}
	
}
