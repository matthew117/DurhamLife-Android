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
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

public class CollegeEventListActivity extends CustomTitleBarActivity
{
	private List<Event> eventList;
	private EventListAdapter adapter;
	private ListView listView;
	private ProgressDialog progressDialog;
	private AlertDialog alertDialog;
	private User user;
	private TextView collegeNameText;
	private List<Event> newList;
	private Runnable parseData;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.college_events_list_layout);

		user = SessionFunctions.getCurrentUser(this);

		listView = (ListView) findViewById(R.id.collegeEventListView);
		collegeNameText = (TextView) findViewById(R.id.collegeNameOnEventList);

		if (user != null && user.getCollege() != null)
		{
			collegeNameText.setText(user.getCollege());
			collegeNameText.setBackgroundColor(collegeToColor(user.getCollege()));
			collegeNameText.setCompoundDrawablesWithIntrinsicBounds(
					collegeToImage(user.getCollege()), 0, 0, 0);
		}

		eventList = new ArrayList<Event>();

		adapter = new EventListAdapter(this, R.layout.custom_event_list_row, eventList);
		listView.setAdapter(adapter);

		listView.setOnItemClickListener(new OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id)
			{

				Intent i = new Intent(view.getContext(), EventDetailsTabRootActivity.class);
				Event e = (Event) adapter.getItem(position);
				i.putExtra("event_id", e.getEventID());
				i.putExtra("event_name", e.getName());
				i.putExtra("event_start_date", e.getStartDate());
				i.putExtra("event_end_date", e.getEndDate());
				i.putExtra("event_description", e.getDescriptionHeader());
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
				startActivity(i);
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
				AlertDialog.Builder builder = new AlertDialog.Builder(CollegeEventListActivity.this);
				builder.setMessage(
						"Could not connect. Are you sure that you have an internet connection?")
						.setCancelable(false)
						.setNegativeButton("Back", new DialogInterface.OnClickListener()
						{
							public void onClick(DialogInterface dialog, int id)
							{
								CollegeEventListActivity.this.finish();
							}
						}).setPositiveButton("Retry", new DialogInterface.OnClickListener()
						{
							public void onClick(DialogInterface dialog, int id)
							{
								Thread thread = new Thread(null, parseData, "SAXParser");
								thread.start();
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
				try
				{
					newList = EventAPI.downloadEventsByCollege(user.getCollege());
					runOnUiThread(callbackFunction);
				}
				catch (IOException ex)
				{
					runOnUiThread(errorCallback);
				}
			}
		};

		Thread thread = new Thread(null, parseData, "SAXParser");
		thread.start();
		progressDialog = ProgressDialog.show(CollegeEventListActivity.this, "Please wait...",
				"Downloading Events ...", true);

	}

	@Override
	public void onDestroy()
	{
		super.onDestroy();
		if (alertDialog != null && alertDialog.isShowing()) alertDialog.dismiss();
		if (progressDialog != null && progressDialog.isShowing()) progressDialog.dismiss();
	}

	private String collegeToCode(String collegeName)
	{
		if (collegeName.equals("St. Aidan's")) return "SA";
		else if (collegeName.equals("Collingwood")) return "CW";
		else if (collegeName.equals("Grey")) return "GR";
		else if (collegeName.equals("Hatfield")) return "HAT";
		else if (collegeName.equals("Josephine Butler")) return "JB";
		else if (collegeName.equals("St. Chad's")) return "CH";
		else if (collegeName.equals("St. Cuthbert's")) return "CB";
		else if (collegeName.equals("Hild Bede")) return "HB";
		else if (collegeName.equals("St. John's")) return "JHN";
		else if (collegeName.equals("St. Mary's")) return "MRY";
		else if (collegeName.equals("Trevelyan")) return "TRV";
		else if (collegeName.equals("University")) return "UNI";
		else if (collegeName.equals("Van Mildert")) return "VM";
		else if (collegeName.equals("Ustinov")) return "UST";
		else if (collegeName.equals("John Snow")) return "JS";
		else if (collegeName.equals("Stephenson")) return "STV";
		else return null;

	}

	private int collegeToColor(String collegeName)
	{
		if (collegeName.equals("St. Aidan's")) return Color.parseColor("#146539");
		else if (collegeName.equals("Collingwood")) return Color.parseColor("#A01B1B");
		else if (collegeName.equals("Grey")) return Color.parseColor("#AF0B0B");
		else if (collegeName.equals("Hatfield")) return Color.parseColor("00006B");
		else if (collegeName.equals("Josephine Butler")) return Color.parseColor("990033");
		else if (collegeName.equals("St. Chad's")) return Color.parseColor("#001900");
		else if (collegeName.equals("St. Cuthbert's")) return Color.parseColor("#339999");
		else if (collegeName.equals("Hild Bede")) return Color.parseColor("#003366");
		else if (collegeName.equals("St. John's")) return Color.parseColor("#3A4C80");
		else if (collegeName.equals("St. Mary's")) return Color.parseColor("#5F248E");
		else if (collegeName.equals("Trevelyan")) return Color.parseColor("#2584BB");
		else if (collegeName.equals("University")) return Color.parseColor("#6E0803");
		else if (collegeName.equals("Van Mildert")) return Color.parseColor("#BE1727");
		else if (collegeName.equals("Ustinov")) return Color.parseColor("#3C4443");
		else if (collegeName.equals("John Snow")) return Color.parseColor("#326A89");
		else if (collegeName.equals("Stephenson")) return Color.parseColor("#BA1810");
		else return 0xffffff;
	}

	private int collegeToImage(String collegeName)
	{
		if (collegeName.equals("St. Aidan's")) return R.drawable.st_adians;
		else if (collegeName.equals("Collingwood")) return R.drawable.collingwood;
		else if (collegeName.equals("Grey")) return R.drawable.grey;
		else if (collegeName.equals("Hatfield")) return R.drawable.college;
		else if (collegeName.equals("Josephine Butler")) return R.drawable.college;
		else if (collegeName.equals("St. Chad's")) return R.drawable.college;
		else if (collegeName.equals("St. Cuthbert's")) return R.drawable.college;
		else if (collegeName.equals("Hild Bede")) return R.drawable.college;
		else if (collegeName.equals("St. John's")) return R.drawable.college;
		else if (collegeName.equals("St. Mary's")) return R.drawable.college;
		else if (collegeName.equals("Trevelyan")) return R.drawable.college;
		else if (collegeName.equals("University")) return R.drawable.college;
		else if (collegeName.equals("Van Mildert")) return R.drawable.college;
		else if (collegeName.equals("Ustinov")) return R.drawable.college;
		else if (collegeName.equals("John Snow")) return R.drawable.college;
		else if (collegeName.equals("Stephenson")) return R.drawable.college;
		else return R.drawable.college;
	}

}
