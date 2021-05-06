package com.main.chatmate.contactsmadness;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import com.main.chatmate.R;

public class ContactsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, AdapterView.OnItemClickListener {
	@SuppressLint({"InlinedApi", "ObsoleteSdkInt"})
	private final static String[] FROM_COLUMNS = {
			Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ? ContactsContract.Contacts.DISPLAY_NAME_PRIMARY : ContactsContract.Contacts.DISPLAY_NAME
	};
	
	private final static int[] TO_IDS = {
			android.R.id.text1
	};
	
	@SuppressLint({"InlinedApi", "ObsoleteSdkInt"})
	private static final String SELECTION =
			Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ?
					ContactsContract.Contacts.DISPLAY_NAME_PRIMARY + " LIKE ?" :
					ContactsContract.Contacts.DISPLAY_NAME + " LIKE ?";
	private String searchString;
	private String[] selectionArgs = { searchString };
	
	@SuppressLint({"InlinedApi", "ObsoleteSdkInt"})
	private static final String[] PROJECTION =
			{
					ContactsContract.Data._ID,
					Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ?
							ContactsContract.Data.DISPLAY_NAME_PRIMARY :
							ContactsContract.Data.DISPLAY_NAME,
					ContactsContract.Data.CONTACT_ID,
					ContactsContract.Data.LOOKUP_KEY // A permanent link to the contact
			};
	
	ListView contactsList;
	long contactId;
	String contactKey;
	Uri contactUri;
	private SimpleCursorAdapter cursorAdapter;
	private static final int CONTACT_ID_INDEX = 0;
	private static final int CONTACT_KEY_INDEX = 1;
	
	public ContactsFragment() {}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.contact_layout, container, false);
		
		
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		LoaderManager.getInstance(this).initLoader(0, null, this);
	}
	
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		contactsList = (ListView) requireActivity().findViewById(R.id.main_chats_listView);
		cursorAdapter = new SimpleCursorAdapter(getActivity(), R.layout.contact_layout, null, FROM_COLUMNS, TO_IDS, 0);
		contactsList.setAdapter(cursorAdapter);
		
		contactsList.setOnItemClickListener(this);
	}
	
	@Override
	public void onItemClick(AdapterView<?> parent, View item, int position, long rowID) {
		Cursor cursor = ((SimpleCursorAdapter)parent.getAdapter()).getCursor();
		cursor.moveToPosition(position);
		contactId = cursor.getLong(CONTACT_ID_INDEX);
		contactKey = cursor.getString(CONTACT_KEY_INDEX);
		contactUri = ContactsContract.Contacts.getLookupUri(contactId, /*mContactKey*/ "ciao");
	}
	
	@NonNull
	@Override
	public Loader<Cursor> onCreateLoader(int loaderId, Bundle args) {
		/*
		 * Makes search string into pattern and
		 * stores it in the selection array
		 */
		selectionArgs[0] = "%" + searchString + "%";
		return new CursorLoader(requireActivity(), ContactsContract.Contacts.CONTENT_URI, PROJECTION, SELECTION, selectionArgs,null);
	}
	
	@Override
	public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {
		cursorAdapter.swapCursor(cursor);
	}
	
	@Override
	public void onLoaderReset(@NonNull Loader<Cursor> loader) {
		cursorAdapter.swapCursor(null);
	}
}
