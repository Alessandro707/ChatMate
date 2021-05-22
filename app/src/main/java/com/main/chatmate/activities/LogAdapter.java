package com.main.chatmate.activities;

import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.main.chatmate.R;

public class LogAdapter extends BaseAdapter {
	
	@Override
	public int getCount() {
		return LogActivity.log.size() + LogActivity.debug.size();
	}
	
	@Override
	public Object getItem(int position) {
		if(position >= LogActivity.log.size() + LogActivity.debug.size()) return null;
		if(position >= LogActivity.log.size())
			return LogActivity.debug.get(position - LogActivity.log.size());
		else
			return LogActivity.log.get(position);
	}
	
	@Override
	public long getItemId(int position) {
		return position;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if(convertView == null){
			LayoutInflater inflater = LayoutInflater.from(parent.getContext());
			convertView = inflater.inflate(R.layout.log_layout, parent, false);
		}
		TextView log = convertView.findViewById(R.id.log_textView);
		
		if(position >= LogActivity.log.size()){
			log.setText(LogActivity.debug.get(position - LogActivity.log.size()));
			log.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);
		}
		else{
			log.setText(LogActivity.log.get(position));
		}
		
		return convertView;
	}
}
