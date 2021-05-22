package com.main.chatmate.activities;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.widget.Toast;

import com.main.chatmate.chat.Contact;
import com.main.chatmate.MyLogger;
import com.main.chatmate.R;
import com.main.chatmate.adapters.ContactsAdapter;

import java.util.ArrayList;
import java.util.List;


public class ContactsActivity extends AppCompatActivity {
	RecyclerView contactsView;
	private final ActivityResultLauncher<String> requestPermissionLauncher =
			registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
				if (isGranted) {
					// Permission is granted.
					MyLogger.log("User granted Read Contacts permission");
					
					showContacts();
				} else {
					// Permission denied
					MyLogger.log("User denied Read Contacts permission");
					
					Intent mainActivity = new Intent(ContactsActivity.this, MainActivity.class);
					startActivity(mainActivity);
				}
			});
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_contacts);
		MyLogger.log("Contacts activity created successfully");
		
		contactsView = findViewById(R.id.contacts_contacts_listView);
		
		if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
			// Permission granted
			MyLogger.log("Read Contacts permission already granted");
			showContacts();
		}
		else {
			// Ask for permission
			if (shouldShowRequestPermissionRationale(Manifest.permission.READ_CONTACTS)) {
				Toast.makeText(this, "E' necessaria l'autorizzazione alla lettura dei contatti per trovare nuovi chatmate", Toast.LENGTH_SHORT).show();
			}
			requestPermissionLauncher.launch(Manifest.permission.READ_CONTACTS);
			MyLogger.log("Asked for Read Contacts permission");
		}
	}
	
	private void showContacts(){
		if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED)
			return;
		
		ContentResolver contentResolver = getContentResolver();
		Cursor cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
		if (cursor != null) {
			//List<Pair<String, String>> contacts = new ArrayList<>();
			List<Contact> contacts = new ArrayList<>();
			while (cursor.moveToNext()) {
				String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
				if (cursor.getInt(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
					Cursor cursorInfo = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
							ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[]{id},
							ContactsContract.Contacts.DISPLAY_NAME_PRIMARY + " ASC");
					
					if(cursorInfo != null) {
						String lastNumber = "";
						while(cursorInfo.moveToNext()) {
							String contactNumber = cursorInfo.getString(cursorInfo.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
							String contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME_PRIMARY));
							if(contactNumber.equals(lastNumber))
								continue;

							String data = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.PHOTO_URI));
							
							contacts.add(new Contact(contactName, contactNumber, data));
							lastNumber = contactNumber;
						}
						cursorInfo.close();
					}
					else{
						MyLogger.log("Contacts info cursor returned no data");
					}
				}
			}
			cursor.close();
			
			MyLogger.log("Number of contacts loaded:  " + contacts.size());
			
			ContactsAdapter adapterPazzo = new ContactsAdapter(contacts, this.getApplicationContext());
			contactsView.setLayoutManager(new LinearLayoutManager(this));
			contactsView.setAdapter(adapterPazzo);
			
		}
		else{
			MyLogger.log("Contacts cursor returned no data");
		}
		/*
		String[] projection = {ContactsContract.Contacts.DISPLAY_NAME_PRIMARY};
		
		ContentResolver contentResolver = getContentResolver();
		Cursor cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI,
				projection, // List of columns to retrieve
				null, // A filter of which rows to return (eg. SQL WHERE), null so get all
				null, // Selection args, param binding
				ContactsContract.Contacts.DISPLAY_NAME_PRIMARY); // Sort order
		
		if (cursor != null) { // No guarantee the resolver will return data, must sanity check
			List<Pair<String, String>> contacts = new ArrayList<>();
			while (cursor.moveToNext()) {
				if (cursor.getInt(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
					Cursor pCur = contentResolver.query(
							ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
							null,
							ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
							new String[]{cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID))}, null);
					
					while (pCur.moveToNext()) {
						String phoneNo = pCur.getString(pCur.getColumnIndex(
								ContactsContract.CommonDataKinds.Phone.NUMBER));
					}
					pCur.close();
					
					contacts.add(new Pair<>(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME_PRIMARY)),
							cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.NUMBER))));
				}
			}
			
			cursor.close();
			
			
			MyLogger.log("Number of contacts loaded:  " + contacts.size());
			
			ContactAdapter adapterPazzo = new ContactAdapter(contacts, this.getApplicationContext());
			
			contactsView.setLayoutManager(new LinearLayoutManager(this));
			contactsView.setAdapter(adapterPazzo);
		}
		else{
			MyLogger.log("Contacts cursor returned no data");
		}
		 */
	}
}

