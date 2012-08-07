package uk.ac.dur.duchess;

import java.util.ArrayList;
import java.util.List;

import uk.ac.dur.duchess.activity.CollegeEventListActivity;
import uk.ac.dur.duchess.activity.EventDetailsTabRootActivity;
import uk.ac.dur.duchess.activity.EventListActivity;
import uk.ac.dur.duchess.data.DataProvider;
import uk.ac.dur.duchess.data.SessionFunctions;
import uk.ac.dur.duchess.entity.Event;
import uk.ac.dur.duchess.entity.EventLocation;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

public class EventListView extends ListView
{
	private Context listContext;
	private List<Event> eventList;
	private Runnable parseData;
	private EventListAdapter adapter;
	private ProgressDialog progressDialog;
	private AlertDialog alertDialog;

	public EventListView(Context context)
	{
		super(context);
		
		listContext = context;

		adapter = new EventListAdapter(context, R.layout.custom_event_list_row, new ArrayList<Event>());
		setAdapter(adapter);

		setOnItemClickListener(new OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id)
			{
				Intent i = new Intent(view.getContext(), EventDetailsTabRootActivity.class);
				Event e = (Event) getAdapter().getItem(position);
				EventLocation l = e.getLocation();
				i.putExtra("event_id", e.getEventID());
				i.putExtra("location_id", l.getLocationID());
				i.putExtra("event_name", e.getName());
				listContext.startActivity(i);
			}
		});
	}
	
	public EventListView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		
		listContext = context;
		
		adapter = new EventListAdapter(context, R.layout.custom_event_list_row, new ArrayList<Event>());
		setAdapter(adapter);

		setOnItemClickListener(new OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id)
			{
				Intent i = new Intent(view.getContext(), EventDetailsTabRootActivity.class);
				Event e = (Event) getAdapter().getItem(position);
				EventLocation l = e.getLocation();
				i.putExtra("event_id", e.getEventID());
				i.putExtra("location_id", l.getLocationID());
				i.putExtra("event_name", e.getName());
				listContext.startActivity(i);
			}
		});
	}

	public void loadEvents(final Activity activity)
	{
		final Runnable callbackFunction = new Runnable()
		{
			@Override
			public void run()
			{
				for (Event e : eventList) adapter.add(e);

				progressDialog.dismiss();
			}
		};

		final Runnable errorCallback = new Runnable()
		{
			@Override
			public void run()
			{
				progressDialog.dismiss();
				AlertDialog.Builder builder = new AlertDialog.Builder(listContext);
				builder.setMessage(
						"Could not connect. Are you sure that you have an internet connection?")
						.setCancelable(false)
						.setNegativeButton("Back", new DialogInterface.OnClickListener()
						{
							public void onClick(DialogInterface dialog, int id)
							{
								activity.finish();
							}
						}).setPositiveButton("Retry", new DialogInterface.OnClickListener()
						{
							public void onClick(DialogInterface dialog, int id)
							{
								Thread thread = new Thread(null, parseData, "SAXParser");
								thread.start();
								progressDialog = ProgressDialog.show(listContext, "Please wait...",
										"Downloading Events ...", true);
							}
						});
				alertDialog = builder.create();
				alertDialog.show();
			}
		};

		parseData = new Runnable()
		{
			@Override
			public void run()
			{
					GlobalApplicationData delegate = GlobalApplicationData.getInstance();
					DataProvider dataPro = delegate.getDataProvider();
					
					if(activity instanceof CollegeEventListActivity)
					{
						eventList = dataPro.getEventsByCollege(listContext,
							SessionFunctions.getCurrentUser(activity).getCollege());
					}
					else if(activity instanceof EventListActivity)
					{
						
					}
					
					if (eventList != null) activity.runOnUiThread(callbackFunction);
					else                   activity.runOnUiThread(errorCallback);
				
			}
		};

		Thread thread = new Thread(null, parseData, "SAXParser");
		thread.start();
		progressDialog = ProgressDialog.show(listContext, "Please wait...",
				"Downloading Events ...", true);
	}
}
