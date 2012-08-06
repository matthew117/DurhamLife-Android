package uk.ac.dur.duchess;

import java.util.ArrayList;
import java.util.List;

import uk.ac.dur.duchess.activity.EventDetailsTabRootActivity;
import uk.ac.dur.duchess.data.DataProvider;
import uk.ac.dur.duchess.data.SessionFunctions;
import uk.ac.dur.duchess.entity.Event;
import uk.ac.dur.duchess.entity.EventLocation;
import uk.ac.dur.duchess.entity.User;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

public class EventListView extends ListView
{
	private Context listContext;
	private List<Event> newList;
	private Runnable parseData;
	private EventListAdapter adapter;
	private List<Event> eventList;
	private ProgressDialog progressDialog;
	private AlertDialog alertDialog;
	private User user;
	private Activity listActivity;

	public EventListView(Context context, Activity activity)
	{
		super(context);
		
		listContext = context;
		listActivity = activity;
		user = SessionFunctions.getCurrentUser(activity);
		
		eventList = new ArrayList<Event>();

		adapter = new EventListAdapter(context, R.layout.custom_event_list_row, eventList);
		setAdapter(adapter);
		setEmptyView(findViewById(R.id.collegeEventListEmpty));

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

		final Runnable callbackFunction = new Runnable()
		{
			@Override
			public void run()
			{
				for (Event e : newList)
				{
					adapter.add(e);
				}
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
								listActivity.finish();
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
					newList = dataPro.getEventsByCollege(listContext, user.getCollege());
					
					if (newList != null) listActivity.runOnUiThread(callbackFunction);
					else                 listActivity.runOnUiThread(errorCallback);
				
			}
		};

		Thread thread = new Thread(null, parseData, "SAXParser");
		thread.start();
		progressDialog = ProgressDialog.show(context, "Please wait...",
				"Downloading Events ...", true);
	}

}
