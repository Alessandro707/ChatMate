package com.main.chatmate.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.main.chatmate.MyLogger;
import com.main.chatmate.R;
import com.main.chatmate.chat.MessagesAdapter;
import com.main.chatmate.chat.User;


public class ChatActivity extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chat);
		
		int chat = (int)getIntent().getExtras().get("chat");

		ListView lista = findViewById(R.id.chat_messages_listView);
		MessagesAdapter adapter = new MessagesAdapter(chat);
		lista.setAdapter(adapter);
		
		Button bottonePazzo = findViewById(R.id.testoloPazzolo);
		bottonePazzo.setOnClickListener(new View.OnClickListener() {
			int count =0;
			@Override
			public void onClick(View v) {
				User.get().getChats().get(chat).sendMessage("Send badinellis " + count);
				count++;
				adapter.notifyDataSetChanged();
			}
		});
		Button bottonePazzo2 = findViewById(R.id.buttonbotton);
		bottonePazzo2.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				User.get().getChats().get(chat).receiveMessage("Nope");
				adapter.notifyDataSetChanged();
			}
		});
		
		MyLogger.log("Chat activity created successfully");
	}
	
}
