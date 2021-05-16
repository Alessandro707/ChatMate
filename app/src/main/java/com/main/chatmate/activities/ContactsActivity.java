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

import com.main.chatmate.MyLogger;
import com.main.chatmate.R;
import com.main.chatmate.contactsmadness.ContactAdapter;

import java.util.ArrayList;
import java.util.List;


public class ContactsActivity extends AppCompatActivity {
	RecyclerView contactsView;
	private final ActivityResultLauncher<String> requestPermissionLauncher =
			registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
				if (isGranted) {
					// Permission is granted.
					// TODO: show contacts
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
		
		String[] projection = {ContactsContract.Contacts.DISPLAY_NAME_PRIMARY};
		
		ContentResolver contentResolver = getContentResolver();
		Cursor cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI,
				projection, // List of columns to retrieve
				null, // A filter of which rows to return (eg. SQL WHERE), null so get all
				null, // Selection args, param binding
				ContactsContract.Contacts.DISPLAY_NAME_PRIMARY); // Sort order
		
		if (cursor != null) { // No guarantee the resolver will return data, must sanity check
			List<String> contacts = new ArrayList<String>();
			while (cursor.moveToNext()) {
				contacts.add(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME_PRIMARY)));
			}
			
			cursor.close();
			
			MyLogger.log("Number of contacts loaded:  " + contacts.size());
			
			ContactAdapter adapterPazzo = new ContactAdapter(contacts);
			
			contactsView.setLayoutManager(new LinearLayoutManager(this));
			contactsView.setAdapter(adapterPazzo);
		}
		else{
			MyLogger.log("Contacts cursor returned no data");
		}
	}
}

