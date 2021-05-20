package com.main.chatmate.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ScrollView;


import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.main.chatmate.activities.BioActivity;
import com.main.chatmate.R;
import com.main.chatmate.chat.ChatMate;
import com.main.chatmate.chat.Message;
import com.main.chatmate.chat.MessagesAdapter;
import com.main.chatmate.chat.User;
import com.main.chatmate.chat.Chat;

import java.io.File;

public class ChatActivity extends AppCompatActivity {
    Button backButton, sendButton;
    ListView corpo;
    EditText writeBox;
    private Chat chat;

    @Override
    public void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_layout);
        int nchat = (int) getIntent().getExtras().get("chat");
        backButton= findViewById(R.id.chat_back_Button);
        sendButton= findViewById(R.id.chat_send_Button);
        MessagesAdapter messagesAdapter = new MessagesAdapter(nchat);
        corpo= findViewById(R.id.chat_corpo_listView);
        corpo.setAdapter(messagesAdapter);
        writeBox = findViewById(R.id.chat_writeBox_editTextTextMultiLine);
        
        chat = User.get().getChats().get((int)getIntent().getExtras().get("chat"));
        chat.load();
        
        if(!chat.canWrite())
            writeBox.setEnabled(false);
            
        backButton.setOnClickListener(v -> {
            Intent back = new Intent(ChatActivity.this, ContactsActivity.class);
            startActivity(back);
        });

        sendButton.setOnClickListener(v -> {
            DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference();
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if(!writeBox.getText().toString().isEmpty()) {
                User.get().getChats().get(nchat).sendMessage(writeBox.getText().toString());
                messagesAdapter.notifyDataSetChanged();
                ChatMate chatMate= User.get().getChats().get(nchat).getChatmate();
                final long[] count = new long[1];
                databaseRef.child("users/"+ chatMate.getUid() + "/chats" + user.getUid() + "/").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        count[0] = dataSnapshot.getChildrenCount() + 1;
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        //non ci sono problemi no preoccupa signò
                    }
                });
                databaseRef.child("users/"+ chatMate.getUid() + "/chats" + user.getUid() + "/" + count[0] + databaseRef.setValue(writeBox.getText().toString()));
            }
            //corpo.addView(d);
        });
    
        chat = User.get().getChats().get((int)getIntent().getExtras().get("chat"));
        chat.load();
    }
    
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        chat.close();
    }
}
