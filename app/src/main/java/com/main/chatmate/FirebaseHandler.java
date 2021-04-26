package com.main.chatmate;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public interface FirebaseHandler {
	
	static void upload(StorageReference where, byte[] data, OnSuccessListener<UploadTask.TaskSnapshot> successListener, OnFailureListener failureListener){
		where.putBytes(data).addOnFailureListener(failureListener).addOnSuccessListener(successListener);
	}
	
	static void download(StorageReference where, long maxSize, OnSuccessListener<byte[]> successListener, OnFailureListener failureListener){
		where.getBytes(maxSize).addOnFailureListener(failureListener).addOnSuccessListener(successListener);
	}
	
	static void getAllFilesUnderReference(StorageReference where, OnSuccessListener<ListResult> successListener, OnFailureListener failureListener){
		where.listAll().addOnFailureListener(failureListener).addOnSuccessListener(successListener);
	}
	
}
