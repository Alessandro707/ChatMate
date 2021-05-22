package com.main.chatmate.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.main.chatmate.R;
import com.main.chatmate.chat.ChatMate;
import com.main.chatmate.adapters.MessagesAdapter;
import com.main.chatmate.chat.User;
import com.main.chatmate.chat.Chat;

public class ChatActivity extends AppCompatActivity {
    Button backButton, sendButton;
    ListView corpo;
    EditText writeBox;
    private Chat chat;

    @Override
    public void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        int nchat = (int) getIntent().getExtras().get("chat");
        backButton= findViewById(R.id.chat_back_Button);
        sendButton= findViewById(R.id.chat_send_Button);
        MessagesAdapter messagesAdapter = new MessagesAdapter(nchat);
        corpo= findViewById(R.id.chat_corpo_listView);
        corpo.setAdapter(messagesAdapter);
        writeBox = findViewById(R.id.chat_writeBox_editTextTextMultiLine);
        
        backButton.setOnClickListener(v -> {
            Intent back = new Intent(ChatActivity.this, ContactsActivity.class);
            chat.close();
            startActivity(back);
        });
    
        chat = User.get().getChats().get(nchat);
        chat.load();
    
        if(!chat.canWrite())
            writeBox.setEnabled(false);
        
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        
        sendButton.setOnClickListener(v -> {
            if(!writeBox.getText().toString().isEmpty()) {
               chat.sendMessage(writeBox.getText().toString());
                messagesAdapter.notifyDataSetChanged();
                ChatMate chatMate= chat.getChatmate();
                ref.child("users/"+ chatMate.getUid() + "/chats/" + user.getUid() + "/").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        ref.child("users/"+ chatMate.getUid() + "/chats/" + user.getUid() + "/" + (dataSnapshot.getChildrenCount() + 1)).setValue(writeBox.getText().toString());
                        writeBox.setText("");
                    }
                
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        //non ci sono problemi no preoccupa signò
                    }
                });
            }
        });
    }
}
