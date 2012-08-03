package uk.ac.dur.duchess.activity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import uk.ac.dur.duchess.EventListAdapter;
import uk.ac.dur.duchess.R;
import uk.ac.dur.duchess.SeparatedListAdapter;
import uk.ac.dur.duchess.data.SessionFunctions;
import uk.ac.dur.duchess.entity.Event;
import uk.ac.dur.duchess.entity.EventLocation;
import uk.ac.dur.duchess.entity.User;
import uk.ac.dur.duchess.webservice.EventAPI;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class PersonalSocietyListActivity extends CustomTitleBarActivity
{
	private Map<String, List<Event>> eventMap;
	private Activity activity;
	private List<String> societyList;
	private ProgressDialog progressDialog;
	
	private SeparatedListAdapter adapter;
	private ListView listView;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		activity = this;
		listView = new ListView(activity); 

		User user = SessionFunctions.getCurrentUser(this);
		societyList = user.getSocieties();

		eventMap = new TreeMap<String, List<Event>>();

		for (String s : societyList)
		{
			eventMap.put(s, new ArrayList<Event>());
		}

		String[] societyArray = new String[societyList.size()];
		societyList.toArray(societyArray);
		
		(new DownloadEventsBackgroundTask()).execute(societyArray);
	}

	private class DownloadEventsBackgroundTask extends
			AsyncTask<String, Void, Map<String, List<Event>>>
	{
		@Override
		protected void onPreExecute()
		{
			progressDialog = ProgressDialog.show(PersonalSocietyListActivity.this,
					"Please wait...", "Downloading Events ...", true);
		}

		@Override
		protected Map<String, List<Event>> doInBackground(String... societyArray)
		{
			try
			{
				List<Event> eventList = new ArrayList<Event>();
				for (String s : societyArray)
				{
					eventList = EventAPI.downloadEventsBySociety(s);
					eventMap.put(s, eventList);
				}
				return eventMap;
			}
			catch (IOException ex)
			{

			}
			return eventMap;
		}

		@Override
		protected void onPostExecute(Map<String, List<Event>> eventMap)
		{
	        // create our list and custom adapter  
	        adapter = new SeparatedListAdapter(activity);  
	        
			for(String society : eventMap.keySet())
			{
				adapter.addSection(society,
						new EventListAdapter(activity, R.layout.custom_event_list_row, eventMap.get(society))); 
			}
			 
	        listView.setAdapter(adapter);
	        
	        listView.setOnItemClickListener(new OnItemClickListener()
			{
				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id)
				{

					Intent i = new Intent(view.getContext(), EventDetailsTabRootActivity.class);
					Event e = (Event) adapter.getItem(position);
					EventLocation l = e.getLocation();
					i.putExtra("event_id", e.getEventID());
					i.putExtra("location_id", l.getLocationID());
					i.putExtra("event_name", e.getName());
					i.putExtra("event_start_date", e.getStartDate());
					i.putExtra("event_end_date", e.getEndDate());
					i.putExtra("event_description_header", e.getDescriptionHeader());
					i.putExtra("event_description_body", e.getDescriptionBody());
					i.putExtra("event_contact_telephone_number", e.getContactTelephoneNumber());
					i.putExtra("event_contact_email_address", e.getContactEmailAddress());
					i.putExtra("event_web_address", e.getWebAddress());
					i.putExtra("event_address1", l.getAddress1());
					i.putExtra("event_address2", l.getAddress2());
					i.putExtra("event_city", l.getCity());
					i.putExtra("event_postcode", l.getPostcode());
					i.putExtra("event_latitude", l.getLatitude());
					i.putExtra("event_longitude", l.getLongitude());
					i.putExtra("image_url", e.getImageURL());
					i.putExtra("ical_url", e.getICalURL());
					startActivity(i);
				}
			});
	        
	        activity.setContentView(listView);
			
			progressDialog.dismiss();
		}
	}
	
	@Override
	public void onResume()
	{
		super.onResume();
		listView.setAdapter(listView.getAdapter());
	}
}