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
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class PinnedEventListActivity extends ListActivity {
	
	private ListView listView;
	private ArrayAdapter<Event> listAdapter;
	private List<Event> eventList;
	private Runnable errorThread;
	private Runnable downloadEvents;
	private Runnable uiCallbackThread;
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		listView = getListView();
		eventList = new ArrayList<Event>();
		listAdapter = new EventListAdapter(this, R.layout.custom_event_list_row, eventList);
		
		listView.setAdapter(listAdapter);
		
		errorThread = new Runnable() {	
			@Override
			public void run() {
				AlertDialog.Builder builder = new AlertDialog.Builder(PinnedEventListActivity.this);
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
						(new Thread(downloadEvents)).start();				
					}
				});			
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
			}
		};
		(new Thread(downloadEvents)).start();
	}

	private void downloadEventsAndUpdateList() throws IOException {
		List<Event> allEvents = EventAPI.downloadAllEvents();
		eventList.clear();
		User user = SessionFunctions.getCurrentUser(PinnedEventListActivity.this);
		for (Event event : allEvents)
		{
			if (user.hasPinnedEvent(event.getEventID()))
			{
				eventList.add(event);
			}
		}
		runOnUiThread(uiCallbackThread);
	}

}
