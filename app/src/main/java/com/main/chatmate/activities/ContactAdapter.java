package com.main.chatmate.activities;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.main.chatmate.Contact;
import com.main.chatmate.R;

import java.util.List;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ViewHolder> {
	private final List<Contact> contacts;
	private final Context context;
	
	public static class ViewHolder extends RecyclerView.ViewHolder {
		private final TextView name, number;
		private final ImageView image;
		
		public ViewHolder(View view, Context context, List<Contact> contacts) {
			super(view);
			
			view.setOnClickListener(v -> {
				Intent mainActivity = new Intent(context, MainActivity.class);
				mainActivity.putExtra("newChatmatePhone", contacts.get(getAdapterPosition()).getNumber());
				mainActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				context.startActivity(mainActivity);
			});
			
			name = view.findViewById(R.id.contact_name);
			number = view.findViewById(R.id.contact_number);
			image = view.findViewById(R.id.contact_img);
			
			// TODO: add image
		}
		
		public TextView getNameView() {
			return name;
		}
		public TextView getNumberView() {
			return number;
		}
		public ImageView getImageView() {
			return image;
		}
	}
	
	public ContactAdapter(List<Contact> contacts, Context context) { // TODO: show number
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
		viewHolder.getNameView().setText(contacts.get(position).getName());
		viewHolder.getNumberView().setText(contacts.get(position).getNumber());

		String imageUri = contacts.get(position).getImage();
		if(imageUri != null)
			viewHolder.getImageView().setImageURI(Uri.parse(imageUri));
		else
			viewHolder.getImageView().setImageResource(R.mipmap.scali_round);
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