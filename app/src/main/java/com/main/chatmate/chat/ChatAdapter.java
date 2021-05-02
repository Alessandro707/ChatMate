package com.main.chatmate.chat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.main.chatmate.R;

public class ChatAdapter extends BaseAdapter {
	
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
