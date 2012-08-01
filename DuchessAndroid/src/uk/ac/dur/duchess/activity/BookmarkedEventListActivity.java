package uk.ac.dur.duchess.activity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import uk.ac.dur.duchess.EventListAdapter;
import uk.ac.dur.duchess.R;
import uk.ac.dur.duchess.data.SessionFunctions;
import uk.ac.dur.duchess.entity.Event;
import uk.ac.dur.duchess.entity.User;
import uk.ac.dur.duchess.webservice.EventAPI;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class BookmarkedEventListActivity extends Activity
{	
	private ListView listView;
	private ArrayAdapter<Event> listAdapter;
	private List<Event> eventList;
	private Runnable errorThread;
	private Runnable downloadEvents;
	private Runnable uiCallbackThread;
	private ProgressDialog progressDialog;
	private AlertDialog alertDialog;
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.bookmark_list_layout);
		
		listView = (ListView) findViewById(R.id.bookmarkListView);
		eventList = new ArrayList<Event>();
		listAdapter = new EventListAdapter(this, R.layout.custom_event_list_row, eventList);
		
		User user = SessionFunctions.getCurrentUser(BookmarkedEventListActivity.this);
		if (!user.hasAnyBookmarkedEvents())
		{
			listView.setEmptyView(findViewById(R.id.bookmarkListEmpty));
		}
		
		listView.setAdapter(listAdapter);
		
		listView.setOnItemClickListener(new OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id)
			{

				Intent i = new Intent(view.getContext(), EventDetailsTabRootActivity.class);
				Event e = (Event) listAdapter.getItem(position);
				i.putExtra("event_id", e.getEventID());
				i.putExtra("event_name", e.getName());
				i.putExtra("event_start_date", e.getStartDate());
				i.putExtra("event_end_date", e.getEndDate());
				i.putExtra("event_description_header", e.getDescriptionHeader());
				i.putExtra("event_description_body", e.getDescriptionBody());
				i.putExtra("event_contact_telephone_number", e.getContactTelephoneNumber());
				i.putExtra("event_contact_email_address", e.getContactEmailAddress());
				i.putExtra("event_web_address", e.getWebAddress());
				i.putExtra("event_address1", e.getAddress1());
				i.putExtra("event_address2", e.getAddress2());
				i.putExtra("event_city", e.getCity());
				i.putExtra("event_postcode", e.getPostcode());
				i.putExtra("event_latitude", e.getLatitude());
				i.putExtra("event_longitude", e.getLongitude());
				i.putExtra("image_url", e.getImageURL());
				i.putExtra("ical_url", e.getICalURL());
				startActivity(i);
			}
		});
		
		errorThread = new Runnable() {	
			@Override
			public void run() {
				if (progressDialog != null && progressDialog.isShowing()) progressDialog.dismiss();
				AlertDialog.Builder builder = new AlertDialog.Builder(BookmarkedEventListActivity.this);
				builder.setTitle("Connection Error.")
				.setMessage("Failed to download events.")
				.setCancelable(false)
				.setNegativeButton("Back", new OnClickListener()
				{
					@Override
					public void onClick(DialogInterface dialog, int id) {
						finish();					
					}
				})
				.setPositiveButton("Retry", new OnClickListener()
				{
					@Override
					public void onClick(DialogInterface dialog, int which) {
						progressDialog = ProgressDialog.show(BookmarkedEventListActivity.this, "Please wait...",
								"Downloading Events ...", true);
						(new Thread(downloadEvents)).start();				
					}
				});
				alertDialog = builder.create();
				alertDialog.show();
			}
		};
		
		downloadEvents = new Runnable() {
			@Override
			public void run() {
				try {
					downloadEventsAndUpdateList();
				} catch (IOException e) {
					(new Thread(errorThread)).start();
				}
			}
		};
		
		uiCallbackThread = new Runnable() {
			@Override
			public void run() {
				listAdapter.notifyDataSetChanged();
				if (progressDialog != null && progressDialog.isShowing()) progressDialog.dismiss();
			}
		};
		(new Thread(downloadEvents)).start();
		progressDialog = ProgressDialog.show(BookmarkedEventListActivity.this, "Please wait...",
				"Downloading Events ...", true);
	}

	private void downloadEventsAndUpdateList() throws IOException {
		List<Event> allEvents = EventAPI.downloadAllEvents();
		eventList.clear();
		User user = SessionFunctions.getCurrentUser(BookmarkedEventListActivity.this);
		for (Event event : allEvents)
		{
			if (user.hasPinnedEvent(event.getEventID()))
			{
				eventList.add(event);
			}
		}
		runOnUiThread(uiCallbackThread);
	}
	

	@Override
	public void onResume()
	{
		super.onResume();
		listView.setAdapter(listView.getAdapter());
	}
	
	@Override
	public void onDestroy()
	{
		super.onDestroy();
		if (alertDialog != null && alertDialog.isShowing()) alertDialog.dismiss();
		if (progressDialog != null && progressDialog.isShowing()) progressDialog.dismiss();
	}
}
