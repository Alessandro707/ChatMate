package com.main.chatmate.chat;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.main.chatmate.FirebaseHandler;
import com.main.chatmate.MyLogger;
import com.main.chatmate.R;
import com.main.chatmate.activities.ChatActivity;

import java.io.File;

public class ChatsAdapter extends BaseAdapter {
	
	@Override
	public int getCount() {
		return User.get().getChats().size();
	}
	
	@Override
	public Object getItem(int position) {
		if(position >= User.get().getChats().size()) return null;
		return User.get().getChats().get(position);
	}
	
	@Override
	public long getItemId(int position) {
		return position;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if(convertView == null){
			LayoutInflater inflater = LayoutInflater.from(parent.getContext());
			convertView = inflater.inflate(R.layout.chat_layout, parent, false);
		}
		ImageView img_profilo = convertView.findViewById(R.id.profile_img);
		TextView nome_chat = convertView.findViewById(R.id.chat_name);
		
		//TODO: implement
		nome_chat.setText(User.get().getChats().get(position).getChatmate().getName());
		
		FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
		assert user != null;
		String chatmateUid = User.get().getChats().get(position).getChatmate().getUid();
		FirebaseHandler.download(FirebaseStorage.getInstance().getReference().child(chatmateUid + "/img_profilo.jpeg"), 1024 * 1024,
				bytes -> {
					Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
					img_profilo.setImageBitmap(bitmap);
				},
				e -> {
					MyLogger.log("Can't download profile image from storage");
					img_profilo.setImageResource(R.mipmap.mate);
				});
		
		View finalConvertView = convertView;
		convertView.setOnClickListener(v -> {
			MyLogger.log("Opening chat with: " + chatmateUid);
			
			User.get().loadChat(position, new File(finalConvertView.getContext().getFilesDir(), chatmateUid));
			
			Intent chat = new Intent(parent.getContext(), ChatActivity.class);
			chat.putExtra("chat", position);
			parent.getContext().startActivity(chat);
		});
		
		return convertView;
	}
}
