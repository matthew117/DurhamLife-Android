package uk.ac.dur.duchess;

import java.util.ArrayList;
import java.util.List;

import uk.ac.dur.duchess.activity.BookmarkedEventListActivity;
import uk.ac.dur.duchess.activity.CalendarActivity;
import uk.ac.dur.duchess.activity.CollegeEventListActivity;
import uk.ac.dur.duchess.activity.EventDetailsTabRootActivity;
import uk.ac.dur.duchess.activity.EventListActivity;
import uk.ac.dur.duchess.activity.SocietyEventListActivity;
import uk.ac.dur.duchess.data.CalendarFunctions;
import uk.ac.dur.duchess.data.DataProvider;
import uk.ac.dur.duchess.data.SessionFunctions;
import uk.ac.dur.duchess.data.UserFunctions;
import uk.ac.dur.duchess.entity.Event;
import uk.ac.dur.duchess.entity.EventLocation;
import uk.ac.dur.duchess.entity.User;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

public class EventListView extends ListView
{
	private List<Event> eventList;
	private Runnable parseData;
	private EventListAdapter adapter;
	private ProgressDialog progressDialog;
	private AlertDialog alertDialog;

	public EventListView(Context context)
	{
		super(context);

		adapter = new EventListAdapter(context, R.layout.custom_event_list_row, new ArrayList<Event>());
		setAdapter(adapter);

		eventList = new ArrayList<Event>();

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
				getContext().startActivity(i);
			}
		});
	}

	public EventListView(Context context, AttributeSet attrs)
	{
		super(context, attrs);

		adapter = new EventListAdapter(context, R.layout.custom_event_list_row, new ArrayList<Event>());
		setAdapter(adapter);

		eventList = new ArrayList<Event>();

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
				getContext().startActivity(i);
			}
		});
	}

	public void loadAllEvents(final Activity activity, final Bundle bundle)
	{
		final Runnable uiThreadCallback = getCallback();
		final Runnable errorCallback = getErrorCallback(activity);

		parseData = new Runnable()
		{
			@Override
			public void run()
			{
				GlobalApplicationData delegate = GlobalApplicationData.getInstance();
				DataProvider dataPro = delegate.getDataProvider();

				if(activity instanceof CollegeEventListActivity)
				{
					eventList = dataPro.getEventsByCollege(getContext(),
							SessionFunctions.getCurrentUser(activity).getCollege());
				}
				else if(activity instanceof BookmarkedEventListActivity)
				{
					List<Event> allEvents = dataPro.getAllEvents(getContext());
					eventList.clear();

					User user = SessionFunctions.getCurrentUser(activity);

					if (allEvents != null && user != null)
						for (Event event : allEvents)
							if (user.hasPinnedEvent(event.getEventID())) eventList.add(event);	
				}
				else if(activity instanceof SocietyEventListActivity)
				{
					eventList = dataPro.getEventsBySociety(getContext(), bundle.getString("society_name"));
				}
				else if(activity instanceof CalendarActivity)
				{
					eventList = dataPro.getAllEvents(getContext());
					filterEventByDateRange(bundle.getString("from_date"), bundle.getString("to_date"));
				}
				else if(activity instanceof EventListActivity)
				{
					eventList = dataPro.getAllEvents(activity);
					User user = SessionFunctions.getCurrentUser(activity);
					
					if (user != null && eventList != null)
					{
						Log.d("BEFORE FILTER", "" + eventList.size());
						UserFunctions.filterByPreferences(user, eventList);
						Log.d("AFTER FILTER", "" + eventList.size());
					}
				}

				if (eventList != null) activity.runOnUiThread(uiThreadCallback);
				else                   activity.runOnUiThread(errorCallback);
			}
		};

		if(!(activity instanceof BookmarkedEventListActivity))
		{
			Thread thread = new Thread(null, parseData, "SAXParser");

			thread.start();
			progressDialog = ProgressDialog.show(getContext(), "Please wait...",
					"Downloading Events ...", true);
		}
		else if(SessionFunctions.getCurrentUser(activity).hasAnyBookmarkedEvents())
		{
			Thread thread = new Thread(null, parseData, "SAXParser");

			thread.start();
			progressDialog = ProgressDialog.show(getContext(), "Please wait...",
					"Downloading Events ...", true);
		}
	}

	private Runnable getCallback()
	{
		Runnable uiThreadCallback = new Runnable()
		{
			@Override
			public void run()
			{
				adapter.clear();
				for (Event e : eventList) adapter.add(e);
				adapter.notifyDataSetChanged();

				progressDialog.dismiss();
			}
		};

		return uiThreadCallback;
	}

	private Runnable getErrorCallback(final Activity activity)
	{
		Runnable errorCallback = new Runnable()
		{
			@Override
			public void run()
			{
				progressDialog.dismiss();
				AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
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
								progressDialog = ProgressDialog.show(getContext(), "Please wait...",
										"Downloading Events ...", true);
							}
						});
				alertDialog = builder.create();
				alertDialog.show();
			}
		};

		return errorCallback;
	}

	public void filterEventByDateRange(String fromDate, String toDate)
	{
		adapter.clear();

		if(eventList != null)
		{
			for (Event event : eventList)
			{
				if (CalendarFunctions.inRange(event.getStartDate(), event.getEndDate(),
						fromDate, toDate)) adapter.add(event);
			}
		}

		adapter.notifyDataSetChanged();
	}

	public void clearAdapter()
	{
		adapter.clear();
		adapter.notifyDataSetChanged();
	}
}
