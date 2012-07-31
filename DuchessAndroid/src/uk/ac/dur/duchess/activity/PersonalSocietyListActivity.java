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
import uk.ac.dur.duchess.entity.User;
import uk.ac.dur.duchess.webservice.EventAPI;
import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ListView;

public class PersonalSocietyListActivity extends CustomTitleBarActivity
{
	private Map<String, List<Event>> eventMap;
	private Activity activity;
	private List<String> societyList;
	private ProgressDialog progressDialog;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		activity = this;

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
	        SeparatedListAdapter adapter = new SeparatedListAdapter(activity);  
	        
			for(String society : eventMap.keySet())
			{
				adapter.addSection(society,
						new EventListAdapter(activity, R.layout.custom_event_list_row, eventMap.get(society))); 
			}
			
			ListView list = new ListView(activity);  
	        list.setAdapter(adapter);
	        
	        activity.setContentView(list);
			
			progressDialog.dismiss();
		}
	}
}