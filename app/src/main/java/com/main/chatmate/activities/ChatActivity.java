package com.main.chatmate.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;


import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.main.chatmate.BioActivity;
import com.main.chatmate.R;
import com.main.chatmate.chat.Message;
import com.main.chatmate.chat.User;

import java.util.ArrayList;

public class ChatActivity extends AppCompatActivity {
    Button backButton;
    Button sendButton;
    ScrollView corpo;
    EditText writeBox;

    @Override
    public void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_layout);
        backButton= findViewById(R.id.chat_back_Button);
        sendButton= findViewById(R.id.chat_send_Button);
        writeBox = findViewById(R.id.chat_writeBox_editTextTextMultiLine);
            backButton.setOnClickListener(v -> {
            Intent back = new Intent(ChatActivity.this, ContactsActivity.class);
            startActivity(back);
        });

        sendButton.setOnClickListener(v -> {
            if(!writeBox.getText().toString().isEmpty()){
                int nchat = (int)getIntent().getExtras().get("chat");
                User.get().getChats().get(nchat).sendMessage(writeBox.getText().toString());
            }
            //corpo.addView(d);
        });


    }
}
