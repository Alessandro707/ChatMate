package com.main.chatmate.contactsmadness;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.main.chatmate.MyLogger;
import com.main.chatmate.R;

import java.util.List;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ViewHolder> {
	private final List<String> contacts;
	
	public static class ViewHolder extends RecyclerView.ViewHolder {
		private final TextView textView;
		
		public ViewHolder(View view) {
			super(view);
			
			view.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					MyLogger.log("Element " + getAdapterPosition() + " clicked.");
				}
			});
			
			textView = (TextView) view.findViewById(R.id.contact_name);
			
			// TODO: add image from db
		}
		
		public TextView getTextView() {
			return textView;
		}
	}
	
	public ContactAdapter(List<String> contacts) {
		this.contacts = contacts;
	}
	
	@Override
	@NonNull
	public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
		// Create a new view.
		View v = LayoutInflater.from(viewGroup.getContext())
				.inflate(R.layout.contact_layout, viewGroup, false);
		
		return new ViewHolder(v);
	}
	
	
	@Override
	public void onBindViewHolder(ViewHolder viewHolder, final int position) {
		// TODO: solo se l'utente esiste sul db
		viewHolder.getTextView().setText(contacts.get(position));
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