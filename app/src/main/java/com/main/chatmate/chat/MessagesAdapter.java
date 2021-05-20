package com.main.chatmate.chat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


import com.main.chatmate.MyLogger;
import com.main.chatmate.R;

public class MessagesAdapter extends BaseAdapter {
	private final int chat;
	
	@Override
	public int getCount() {
		return User.get().getChats().get(chat).getMessages().size();
	}
	
	@Override
	public Object getItem(int position) {
		if(position >= User.get().getChats().get(chat).getMessages().size()) return null;
		return User.get().getChats().get(chat).getMessages().get(position);
	}
	
	@Override
	public long getItemId(int position) {
		return position;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if(convertView==null){
			LayoutInflater inflater = LayoutInflater.from(parent.getContext());
			convertView = inflater.inflate(R.layout.message_layout, parent, false);
		}
		
		Message m = User.get().getChats().get(chat).getMessages().get(position);
		TextView text = convertView.findViewById(R.id.message_message_textView);
		text.setText(m.getMessage());
		if(m.isReceived())
			text.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_END);
		else
			text.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
		
		return convertView;
	}
	
	public MessagesAdapter(int chat){
		this.chat = chat;
	}
}
