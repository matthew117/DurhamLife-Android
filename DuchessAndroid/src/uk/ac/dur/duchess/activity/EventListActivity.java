package uk.ac.dur.duchess.activity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.List;

import uk.ac.dur.duchess.EventListAdapter;
import uk.ac.dur.duchess.GlobalApplicationData;
import uk.ac.dur.duchess.R;
import uk.ac.dur.duchess.data.CalendarFunctions;
import uk.ac.dur.duchess.data.DataProvider;
import uk.ac.dur.duchess.data.SessionFunctions;
import uk.ac.dur.duchess.data.UserFunctions;
import uk.ac.dur.duchess.entity.Event;
import uk.ac.dur.duchess.entity.EventLocation;
import uk.ac.dur.duchess.entity.User;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.DatePicker;
import android.widget.ImageView;

public class EventListActivity extends ListActivity
{
	private ProgressDialog progressDialog;
	private ProgressDialog locationProgress;
	private boolean featureMode = true;

	private User currentUser;
	private Activity activity;

	private ImageView adImageContainer;
	private Event currentAd;

	private List<Event> eventList;
	private EventListAdapter adapter;

	private LocationManager lm;

	private static final int REQUEST_DATEFRAME = 1;
	private static final int REQUEST_CATEG0RY = 2;
	private static final int DATE_DIALOG_ID = 1;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
//		android.os.Debug.startMethodTracing("duchess");

		super.onCreate(savedInstanceState);
		setContentView(R.layout.event_list_layout);

		currentUser = SessionFunctions.getCurrentUser(this);
		activity = this;

		adImageContainer = (ImageView) findViewById(R.id.adImageContainer);

		eventList = new ArrayList<Event>();

		adapter = new EventListAdapter(this, R.layout.custom_event_list_row, eventList);
		setListAdapter(adapter);

		getListView().setOnItemClickListener(new OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id)
			{

				Intent i = new Intent(view.getContext(), EventDetailsTabRootActivity.class);
				Event e = (Event) getListAdapter().getItem(position);
				EventLocation l = e.getLocation();
				i.putExtra("event_id", e.getEventID());
				i.putExtra("location_id", l.getLocationID());
				i.putExtra("event_name", e.getName());
				// i.putExtra("event_start_date", e.getStartDate());
				// i.putExtra("event_end_date", e.getEndDate());
				// i.putExtra("event_description_header", e.getDescriptionHeader());
				// i.putExtra("event_description_body", e.getDescriptionBody());
				// i.putExtra("event_contact_telephone_number", e.getContactTelephoneNumber());
				// i.putExtra("event_contact_email_address", e.getContactEmailAddress());
				// i.putExtra("event_web_address", e.getWebAddress());
				// i.putExtra("event_address1", l.getAddress1());
				// i.putExtra("event_address2", l.getAddress2());
				// i.putExtra("event_city", l.getCity());
				// i.putExtra("event_postcode", l.getPostcode());
				// i.putExtra("event_latitude", l.getLatitude());
				// i.putExtra("event_longitude", l.getLongitude());
				// i.putExtra("image_url", e.getImageURL());
				// i.putExtra("ical_url", e.getICalURL());
				startActivity(i);
			}
		});

		final Runnable callbackFunction = new Runnable()
		{

			@Override
			public void run()
			{
				if (eventList != null)
				{
					for (Event event : eventList)
					{
						adapter.add(event);
					}
				}
				progressDialog.dismiss();
				adapter.notifyDataSetChanged();

				// for (Event e : eventList)
				// {
				// if (e.isFeatured() && e.getAdImageURL() != null)
				// {
				// currentAd = e;
				// Log.d("Download AD", e.getAdImageURL());
				// adImageContainer.setAdjustViewBounds(true);
				// adImageContainer.setScaleType(ScaleType.CENTER_CROP);
				// DisplayMetrics displaymetrics = new DisplayMetrics();
				// getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
				// int height = displaymetrics.heightPixels;
				// int width = displaymetrics.widthPixels;
				// adImageContainer.setMinimumWidth(width);
				// adImageContainer.setMinimumHeight((int) (width / 3.0));
				// adImageContainer.setImageBitmap(NetworkFunctions.downloadImage(e
				// .getAdImageURL()));
				// adImageContainer.invalidate();
				// break;
				// }
				// }

//				android.os.Debug.stopMethodTracing();
			}
		};

		Runnable parseData = new Runnable()
		{
			@Override
			public void run()
			{
				try
				{
					GlobalApplicationData delegate = GlobalApplicationData.getInstance();
					DataProvider dataPro = delegate.getDataProvider();
					eventList = dataPro.getAllEvents(activity);

					if (currentUser != null && eventList != null)
					{
						Log.d("BEFORE FILTER", "" + eventList.size());
						UserFunctions.filterByPreferences(currentUser, eventList);
						Log.d("AFTER FILTER", "" + eventList.size());
					}
					runOnUiThread(callbackFunction);
				}
				catch (Exception ex)
				{
					ex.printStackTrace();
				}
			}
		};

		Thread thread = new Thread(null, parseData, "SAXParser");
		thread.start();
		progressDialog = ProgressDialog.show(EventListActivity.this, "Please wait...",
				"Downloading Events ...", true);

		adImageContainer.setClickable(true);
		adImageContainer.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				Intent i = new Intent(v.getContext(), EventDetailsTabRootActivity.class);
				Event e = currentAd;
				EventLocation l = e.getLocation();
				i.putExtra("event_id", e.getEventID());
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

		

	}

	@Override
	public void onResume()
	{
		super.onResume();
		currentUser = SessionFunctions.getCurrentUser(this);
		setListAdapter(getListAdapter());
	}

	public boolean onCreateOptionsMenu(Menu menu)
	{
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.event_list_menu, menu);
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
		case R.id.submenuEventListFeatured:
			filterEventByFeatured();
			return true;
		case R.id.submenuEventListByDate:
		{
			showDialog(DATE_DIALOG_ID);
			return true;
		}
		case R.id.submenuEventListByDateRange:
		{
			Intent i = new Intent(this, DateFrameActivity.class);
			startActivityForResult(i, REQUEST_DATEFRAME);
			return true;
		}
		case R.id.submenuEventListAZ:
			sortEventsAlphabetically();
			return true;
		case R.id.submenuEventListChronological:
			sortEventsChronologically();
			return true;
		case R.id.submenuEventListRatings:
			sortEventsByHighestReview();
			return true;
		case R.id.submenuEventListDistance:
		{
			locationProgress = ProgressDialog.show(activity, "Sorting By Distance", "Retrieving Location...");
			lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
			LocationListener locationListener = new NearEventsListener();
			lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
			return true;
		}
		case R.id.menuCategoryBrowse:
			Intent i = new Intent(this, CategoryGridActivity.class);
			startActivityForResult(i, REQUEST_CATEG0RY);
			return true;
		default:
			return true;
		}
	}

	private void filterEventByFeatured()
	{
		if (featureMode)
		{
			ArrayList<Event> featuredEvents = new ArrayList<Event>();
			for (Event event : eventList)
			{
				if (event.isFeatured()) featuredEvents.add(event);
			}
			setListAdapter(new EventListAdapter(this, R.layout.custom_event_list_row,
					featuredEvents));
			featureMode = false;
		}
		else
		{
			setListAdapter(adapter);
			featureMode = true;
		}
	}

	private void filterEventByDateRange(String fromDate, String toDate)
	{
		ArrayList<Event> inRangeEvents = new ArrayList<Event>();

		for (Event event : eventList)
		{
			if (CalendarFunctions.inRange(event.getStartDate(), event.getEndDate(), fromDate,
					toDate)) inRangeEvents.add(event);
		}

		setListAdapter(new EventListAdapter(this, R.layout.custom_event_list_row, inRangeEvents));
	}

	private void sortEventsAlphabetically()
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

	private void sortEventsChronologically()
	{
		adapter.sort(new Comparator<Event>()
		{
			@Override
			public int compare(Event e1, Event e2)
			{
				String sDateStr = e1.getStartDate();
				String tDateStr = e2.getStartDate();

				return CalendarFunctions.compareDates(sDateStr, tDateStr);
			}

		});
		adapter.notifyDataSetChanged();
	}

	private void sortEventsByHighestReview()
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

	@Override
	protected void onActivityResult(int requestCode, int responseCode, Intent data)
	{
		switch (requestCode)
		{
		case REQUEST_DATEFRAME:
		{
			if (responseCode == RESULT_OK)
			{
				String fromDate = data.getStringExtra("from_date");
				String toDate = data.getStringExtra("to_date");

				filterEventByDateRange(fromDate, toDate);
			}
			break;
		}
		case REQUEST_CATEG0RY:
		{
			if (responseCode == RESULT_OK)
			{
				String category = data.getStringExtra("category_filter");

				ArrayList<Event> categoryEvents = new ArrayList<Event>();

				for (Event event : eventList)
					if (event.getCategoryTags().contains(category)) categoryEvents.add(event);

				setListAdapter(new EventListAdapter(this, R.layout.custom_event_list_row,
						categoryEvents));
			}
			break;
		}

		default:
			break;
		}
	}

	@Override
	protected Dialog onCreateDialog(int id)
	{
		switch (id)
		{
		case DATE_DIALOG_ID:
		{
			Calendar c = Calendar.getInstance();
			int year = c.get(Calendar.YEAR);
			int month = c.get(Calendar.MONTH);
			int day = c.get(Calendar.DATE);

			return new DatePickerDialog(this, dateSetListener, year, month, day);
		}
		}
		return null;
	}

	private OnDateSetListener dateSetListener = new OnDateSetListener()
	{
		public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth)
		{
			String date = year + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
			String nextDay = year + "-" + (monthOfYear + 1) + "-" + (dayOfMonth + 1);

			filterEventByDateRange(date, nextDay);
		}
	};

	private class NearEventsListener implements LocationListener
	{
		private static final int WALKING_DISTANCE = 800;

		@Override
		public void onLocationChanged(final Location newLocation)
		{
			if (newLocation != null)
			{
				locationProgress.dismiss();

				adapter.sort(new Comparator<Event>()
				{

					@Override
					public int compare(Event e1, Event e2)
					{
						EventLocation loc1 = e1.getLocation();
						EventLocation loc2 = e2.getLocation();

						float[] distanceResult1 = new float[3];
						Location.distanceBetween(newLocation.getLatitude(),
								newLocation.getLongitude(), Double.parseDouble(loc1.getLatitude()),
								Double.parseDouble(loc1.getLongitude()), distanceResult1);
						double distance1 = distanceResult1[0];

						float[] distanceResult2 = new float[3];
						Location.distanceBetween(newLocation.getLatitude(),
								newLocation.getLongitude(), Double.parseDouble(loc2.getLatitude()),
								Double.parseDouble(loc2.getLongitude()), distanceResult2);
						double distance2 = distanceResult2[0];

						return (int) (distance1 - distance2);
					}
				});
				adapter.notifyDataSetChanged();
				lm.removeUpdates(this);
			}
		}

		@Override
		public void onProviderDisabled(String provider)
		{
		}

		@Override
		public void onProviderEnabled(String provider)
		{
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras)
		{
		}
	}
}