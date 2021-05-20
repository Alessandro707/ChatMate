package com.main.chatmate.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ScrollView;


import androidx.appcompat.app.AppCompatActivity;

import com.main.chatmate.R;
import com.main.chatmate.chat.Chat;
import com.main.chatmate.chat.User;

import java.io.File;

public class ChatActivity extends AppCompatActivity {
    private Chat chat;

    @Override
    public void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Button backButton = findViewById(R.id.chat_back_Button);
        Button sendButton = findViewById(R.id.chat_send_Button);
        EditText writeBox = findViewById(R.id.chat_writeBox_editTextTextMultiLine);
        
        chat = User.get().getChats().get((int)getIntent().getExtras().get("chat"));
        chat.load();
        
        if(!chat.canWrite())
            writeBox.setEnabled(false);
            
        backButton.setOnClickListener(v -> {
            Intent back = new Intent(ChatActivity.this, ContactsActivity.class);
            startActivity(back);
        });

        sendButton.setOnClickListener(v -> {
            if(!writeBox.getText().toString().isEmpty()){
                chat.sendMessage(writeBox.getText().toString());
            }
        });
    }
    
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        chat.close();
    }
}
