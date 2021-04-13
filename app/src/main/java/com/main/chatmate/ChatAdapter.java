package com.main.chatmate;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class ChatAdapter extends BaseAdapter {
	ArrayList<Chat> chats;
	
	@Override
	public int getCount() {
		return chats.size();
	}
	
	@Override
	public Object getItem(int position) {
		if(position >= chats.size()) return null;
		return chats.get(position);
	}
	
	@Override
	public long getItemId(int position) {
		return position;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if(convertView==null){
			LayoutInflater inflater = LayoutInflater.from(parent.getContext());
			convertView = inflater.inflate(R.layout.chat_layout, parent, false);
		}
		ImageView img_profilo = convertView.findViewById(R.id.profile_img);
		TextView nome_chat = convertView.findViewById(R.id.chat_name);
		
		//TODO: implement
		
		return convertView;
	}
}
