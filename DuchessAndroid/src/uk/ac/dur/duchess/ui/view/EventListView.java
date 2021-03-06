package uk.ac.dur.duchess.ui.view;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import uk.ac.dur.duchess.GlobalApplicationData;
import uk.ac.dur.duchess.R;
import uk.ac.dur.duchess.io.SessionHandler;
import uk.ac.dur.duchess.io.UserFunctions;
import uk.ac.dur.duchess.io.provider.DataProvider;
import uk.ac.dur.duchess.model.DurhamAffiliation;
import uk.ac.dur.duchess.model.Event;
import uk.ac.dur.duchess.model.EventLocation;
import uk.ac.dur.duchess.model.User;
import uk.ac.dur.duchess.ui.activity.BookmarkedEventListActivity;
import uk.ac.dur.duchess.ui.activity.CalendarActivity;
import uk.ac.dur.duchess.ui.activity.CalendarEventListActivity;
import uk.ac.dur.duchess.ui.activity.CollegeEventListActivity;
import uk.ac.dur.duchess.ui.activity.EventDetailsTabRootActivity;
import uk.ac.dur.duchess.ui.activity.EventListActivity;
import uk.ac.dur.duchess.ui.activity.SocietyEventListActivity;
import uk.ac.dur.duchess.ui.adapter.EventListAdapter;
import uk.ac.dur.duchess.util.CalendarUtils;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

/**
 * 
 * The EventListView class provides a custom ListView designed to display a list
 * of events. The events are summarised by: name, description header, first line
 * of address, and date(s) as defined by its custom ListAdapter, EventListAdapter.
 * 
 * A different set of events can be loaded into the list depending on which
 * activity accesses the list. If these events cannot be loaded due to network
 * problems for example, this class is also responsible for reporting the problem
 * to the user and displaying download progress.
 * 
 * The class also provides utilities for sorting and filtering a list of events
 * in a number of ways.
 * 
 * @author Jamie Bates
 *
 */
public class EventListView extends ListView
{
	private List<Event> eventList;
	private EventListAdapter adapter;
	
	private Runnable dataProviderThread;
	
	private ProgressDialog progressDialog;
	private AlertDialog alertDialog;

	public EventListView(Context context)
	{
		super(context);

		adapter = new EventListAdapter(context, R.layout.custom_event_list_row, new ArrayList<Event>());
		setAdapter(adapter);

		eventList = new ArrayList<Event>();
		
		setDivider(new ColorDrawable(Color.BLACK));
		setDividerHeight(1);
		
		//When an event from the list is clicked, start a new activity to display more information about the event
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
		
		//uses a custom ListAdapter to organise event details appropriately
		adapter = new EventListAdapter(context, R.layout.custom_event_list_row, new ArrayList<Event>());
		setAdapter(adapter);

		eventList = new ArrayList<Event>();
		
		setDivider(new ColorDrawable(Color.BLACK));
		setDividerHeight(1);

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

		dataProviderThread = new Runnable()
		{
			@Override
			public void run()
			{
				GlobalApplicationData delegate = GlobalApplicationData.getInstance();
				DataProvider dataPro = delegate.getDataProvider();

				if(activity instanceof CollegeEventListActivity)
				{
					User user = SessionHandler.getCurrentUser(activity);
					
					if(user.getAffiliation() != DurhamAffiliation.STAFF)
						 eventList = dataPro.getEventsByCollege(getContext(), user.getCollege());
					else eventList = dataPro.getEventsByColleges(getContext(), user.getColleges());
				}
				else if(activity instanceof BookmarkedEventListActivity)
				{
					List<Event> allEvents = dataPro.getAllEvents(getContext());
					eventList.clear();

					User user = SessionHandler.getCurrentUser(activity);

					if (allEvents != null && user != null)
					{
						for (Event event : allEvents)
							if (user.hasPinnedEvent(event.getEventID()))
								eventList.add(event);
					}
					else eventList = null;			
				}
				else if(activity instanceof SocietyEventListActivity)
				{
					eventList = dataPro.getEventsBySociety(getContext(), bundle.getString("society_name"));
				}
				else if(activity instanceof CalendarActivity || activity instanceof CalendarEventListActivity)
				{
					List<Event> allEvents = dataPro.getAllEvents(getContext());
					eventList.clear();

					if(allEvents != null)
					{
						for (Event event : allEvents)
						{
							if (CalendarUtils.inRange(event.getStartDate(), event.getEndDate(),
									bundle.getString("from_date"), bundle.getString("to_date"))) eventList.add(event);
						}
					}
					else eventList = null;
				}
				else if(activity instanceof EventListActivity)
				{
					eventList = dataPro.getAllEvents(activity);
					User user = SessionHandler.getCurrentUser(activity);
					
					if (user != null && eventList != null)
					{
						UserFunctions.filterByPreferences(user, eventList);
					}
				}

				if (eventList != null) activity.runOnUiThread(uiThreadCallback);
				else                   activity.runOnUiThread(errorCallback);
			}
		};

		if(!(activity instanceof BookmarkedEventListActivity))
		{
			Thread thread = new Thread(null, dataProviderThread, "LoadEventThread");

			thread.start();
			progressDialog = ProgressDialog.show(getContext(), "Please wait...",
					"Loading Events...", true);
		}
		else if(SessionHandler.getCurrentUser(activity).hasAnyBookmarkedEvents())
		{
			Thread thread = new Thread(null, dataProviderThread, "LoadEventThread");

			thread.start();
			progressDialog = ProgressDialog.show(getContext(), "Please wait...",
					"Loading Events ...", true);
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

				if (progressDialog != null && progressDialog.isShowing())
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
				if (progressDialog != null && progressDialog.isShowing())
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
								Thread thread = new Thread(null, dataProviderThread, "LoadEventThread");
								thread.start();
								progressDialog = ProgressDialog.show(getContext(), "Please wait...",
										"Loading Events ...", true);
							}
						});
				alertDialog = builder.create();
				alertDialog.show();
			}
		};

		return errorCallback;
	}

	public void filterByDateRange(String fromDate, String toDate)
	{
		adapter.clear();
		
		GlobalApplicationData delegate = GlobalApplicationData.getInstance();
		DataProvider dataPro = delegate.getDataProvider();
		
		List<Event> allEvents = dataPro.getAllEvents(getContext());

		if(allEvents != null)
		{
			for (Event event : allEvents)
			{
				if (CalendarUtils.inRange(event.getStartDate(), event.getEndDate(),
						fromDate, toDate)) adapter.add(event);
			}
		}

		adapter.notifyDataSetChanged();
	}
	
	public void filterByLocation(String location)
	{
		adapter.clear();
		
		GlobalApplicationData delegate = GlobalApplicationData.getInstance();
		DataProvider dataPro = delegate.getDataProvider();
		
		List<Event> allEvents = dataPro.getAllEvents(getContext());

		if(allEvents != null)
		{
			for (Event event : allEvents)
			{
				if (event.getLocation().getAddress1().equals(location))
					adapter.add(event);
			}
		}

		adapter.notifyDataSetChanged();
	}
	
	public void filterByCollege(String college)
	{
		adapter.clear();
		
		GlobalApplicationData delegate = GlobalApplicationData.getInstance();
		DataProvider dataPro = delegate.getDataProvider();
		
		List<Event> collegeEvents = dataPro.getEventsByCollege(getContext(), college);

		if(collegeEvents != null)
		{
			for (Event event : collegeEvents) adapter.add(event);
		}

		adapter.notifyDataSetChanged();
	}
	
	public void filterByCategory(String category)
	{
		adapter.clear();
		
		GlobalApplicationData delegate = GlobalApplicationData.getInstance();
		DataProvider dataPro = delegate.getDataProvider();
		
		List<Event> allEvents = dataPro.getAllEvents(getContext());

		if(allEvents != null)
		{
			for (Event event : allEvents)
				if (event.getCategoryTags().contains(category)) adapter.add(event);
		}

		adapter.notifyDataSetChanged();
	}
	
	public void sortByDistance(final Location location)
	{
		adapter.sort(new Comparator<Event>()
		{

			@Override
			public int compare(Event e1, Event e2)
			{
				EventLocation loc1 = e1.getLocation();
				EventLocation loc2 = e2.getLocation();

				float[] distanceResult1 = new float[3];
				
				Location.distanceBetween(location.getLatitude(), location.getLongitude(),
					Double.parseDouble(loc1.getLatitude()), Double.parseDouble(loc1.getLongitude()),
					distanceResult1);
				
				double distance1 = distanceResult1[0];

				
				float[] distanceResult2 = new float[3];
				
				Location.distanceBetween(location.getLatitude(), location.getLongitude(),
					Double.parseDouble(loc2.getLatitude()), Double.parseDouble(loc2.getLongitude()),
					distanceResult2);
				
				double distance2 = distanceResult2[0];

				
				return (int) (distance1 - distance2);
			}
		});
		adapter.notifyDataSetChanged();
	}
	
	public void sortAlphabetically()
	{
		adapter.sort(new Comparator<Event>()
		{

			@Override
			public int compare(Event obj1, Event obj2)
			{
				return obj1.getName().compareTo(obj2.getName());
			}
		});
		adapter.notifyDataSetChanged();
	}

	public void sortChronologically()
	{
		adapter.sort(new Comparator<Event>()
		{
			@Override
			public int compare(Event e1, Event e2)
			{
				String sDateStr = e1.getStartDate();
				String tDateStr = e2.getStartDate();

				return CalendarUtils.compareDates(sDateStr, tDateStr);
			}

		});
		adapter.notifyDataSetChanged();
	}
	
	public void sortByHighestReview()
	{
		adapter.sort(new Comparator<Event>()
		{
			@Override
			public int compare(Event e1, Event e2)
			{
				if (e1.getReviewScore() == e2.getReviewScore())
					return e2.getNumberOfReviews() - e1.getNumberOfReviews();
				return e2.getReviewScore() - e1.getReviewScore();
			}

		});
		adapter.notifyDataSetChanged();
	}

	public void clearAdapter()
	{
		adapter.clear();
		adapter.notifyDataSetChanged();
	}
}
