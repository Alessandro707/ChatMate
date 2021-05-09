package com.main.chatmate.activities;

import android.content.Context;
import android.content.Intent;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.main.chatmate.R;

import java.util.List;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ViewHolder> {
	private final List<Pair<String, String>> contacts; // name, number
	private final Context context;
	
	public static class ViewHolder extends RecyclerView.ViewHolder {
		private final TextView textView;
		
		public ViewHolder(View view, Context context, List<Pair<String, String>> contacts) {
			super(view);
			
			view.setOnClickListener(v -> {
				Intent mainActivity = new Intent(context, MainActivity.class);
				mainActivity.putExtra("newChatmatePhone", contacts.get(getAdapterPosition()).second);
				context.startActivity(mainActivity);
			});
			
			textView = (TextView) view.findViewById(R.id.contact_name);
			
			// TODO: add image from db
		}
		
		public TextView getTextView() {
			return textView;
		}
	}
	
	public ContactAdapter(List<Pair<String, String>> contacts, Context context) { // TODO: show number
		this.contacts = contacts;
		this.context = context;
	}
	
	@Override
	@NonNull
	public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
		// Create a new view.
		View v = LayoutInflater.from(viewGroup.getContext())
				.inflate(R.layout.contact_layout, viewGroup, false);
		
		return new ViewHolder(v, context, contacts);
	}
	
	
	@Override
	public void onBindViewHolder(ViewHolder viewHolder, final int position) {
		// TODO: solo se l'utente esiste sul db
		viewHolder.getTextView().setText(contacts.get(position).first);
	}
	
	@Override
	public int getItemCount() {
		return contacts.size();
	}
	
	@Override
	public long getItemId(int position) {
		return position;
	}
}