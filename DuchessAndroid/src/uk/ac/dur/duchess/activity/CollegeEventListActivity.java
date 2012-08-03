package uk.ac.dur.duchess.activity;

import java.util.ArrayList;
import java.util.List;

import uk.ac.dur.duchess.EventListAdapter;
import uk.ac.dur.duchess.GlobalApplicationData;
import uk.ac.dur.duchess.R;
import uk.ac.dur.duchess.data.DataProvider;
import uk.ac.dur.duchess.data.SessionFunctions;
import uk.ac.dur.duchess.entity.Event;
import uk.ac.dur.duchess.entity.EventLocation;
import uk.ac.dur.duchess.entity.User;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
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
	private Context context;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.college_events_list_layout);

		this.context = this;
		
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
		listView.setEmptyView(findViewById(R.id.collegeEventListEmpty));

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
				progressDialog.dismiss();
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
								progressDialog = ProgressDialog.show(CollegeEventListActivity.this, "Please wait...",
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
					newList = dataPro.getEventsByCollege(context, user.getCollege());
					
					if (newList != null) runOnUiThread(callbackFunction);
					else                 runOnUiThread(errorCallback);
				
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

	private int collegeToColor(String collegeName)
	{
		Resources res = getResources();
		
		if (collegeName.equals("St. Aidan's")) return res.getColor(R.color.Aidans);
		else if (collegeName.equals("Collingwood")) return res.getColor(R.color.Collingwood);
		else if (collegeName.equals("Grey")) return res.getColor(R.color.Grey);
		else if (collegeName.equals("Hatfield")) return res.getColor(R.color.Hatfield);
		else if (collegeName.equals("Josephine Butler")) return res.getColor(R.color.JB);
		else if (collegeName.equals("St. Chad's")) return res.getColor(R.color.Chads);
		else if (collegeName.equals("St. Cuthbert's")) return res.getColor(R.color.Cuthberts);
		else if (collegeName.equals("Hild Bede")) return res.getColor(R.color.HildBede);
		else if (collegeName.equals("St. John's")) return res.getColor(R.color.Johns);
		else if (collegeName.equals("St. Mary's")) return res.getColor(R.color.Marys);
		else if (collegeName.equals("Trevelyan")) return res.getColor(R.color.Trevs);
		else if (collegeName.equals("University")) return res.getColor(R.color.Castle);
		else if (collegeName.equals("Van Mildert")) return res.getColor(R.color.VanMildert);
		else if (collegeName.equals("Ustinov")) return res.getColor(R.color.Ustinov);
		else if (collegeName.equals("John Snow")) return res.getColor(R.color.JohnSnow);
		else if (collegeName.equals("Stephenson")) return res.getColor(R.color.Stephenson);
		else return 0xffffff;
	}

	private int collegeToImage(String collegeName)
	{
		if (collegeName.equals("St. Aidan's")) return R.drawable.st_aidans;
		else if (collegeName.equals("Collingwood")) return R.drawable.collingwood;
		else if (collegeName.equals("Grey")) return R.drawable.grey;
		else if (collegeName.equals("Hatfield")) return R.drawable.hatfield;
		else if (collegeName.equals("Josephine Butler")) return R.drawable.butler;
		else if (collegeName.equals("St. Chad's")) return R.drawable.chads;
		else if (collegeName.equals("St. Cuthbert's")) return R.drawable.cuthberts;
		else if (collegeName.equals("Hild Bede")) return R.drawable.hild_bede;
		else if (collegeName.equals("St. John's")) return R.drawable.st_johns;
		else if (collegeName.equals("St. Mary's")) return R.drawable.st_marys;
		else if (collegeName.equals("Trevelyan")) return R.drawable.trevelyan;
		else if (collegeName.equals("University")) return R.drawable.castle;
		else if (collegeName.equals("Van Mildert")) return R.drawable.van_mildert;
		else if (collegeName.equals("Ustinov")) return R.drawable.ustinov;
		else if (collegeName.equals("John Snow")) return R.drawable.john_snow;
		else if (collegeName.equals("Stephenson")) return R.drawable.stephenson;
		else return R.drawable.college;
	}
	
	@Override
	public void onResume()
	{
		super.onResume();
		listView.setAdapter(listView.getAdapter());
	}

}
