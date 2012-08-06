package uk.ac.dur.duchess.activity;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import uk.ac.dur.duchess.EventListAdapter;
import uk.ac.dur.duchess.GlobalApplicationData;
import uk.ac.dur.duchess.R;
import uk.ac.dur.duchess.data.DataProvider;
import uk.ac.dur.duchess.data.NetworkFunctions;
import uk.ac.dur.duchess.data.SessionFunctions;
import uk.ac.dur.duchess.entity.Event;
import uk.ac.dur.duchess.entity.EventLocation;
import uk.ac.dur.duchess.entity.User;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class SocietyEventListActivity extends CustomTitleBarActivity
{
	private List<Event> eventList;
	private EventListAdapter adapter;
	private ListView listView;
	private ProgressDialog progressDialog;
	private User user;
	private TextView societyNameText;
	
	private Button aboutButton;
	private Button subscribeButton;
	
	private String societyName;
	
	private Context context;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.society_event_list_layout);

		context = this;
		
		user = SessionFunctions.getCurrentUser(this);

		listView = (ListView) findViewById(R.id.societyEventListView);
		societyNameText = (TextView) findViewById(R.id.nameOnSocietyEventList);
		
		aboutButton = (Button) findViewById(R.id.societyAboutButton);
		subscribeButton = (Button) findViewById(R.id.societyButtonSubscribe);
		
		final Bundle s = getIntent().getExtras();
		
		aboutButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				Intent i = new Intent(v.getContext(), SocietyAboutActivity.class);
				Bundle societyInfo = getIntent().getExtras();
				
				i.putExtra("society_id", societyInfo.getString("society_id"));
				i.putExtra("society_name", societyInfo.getString("society_name"));
				i.putExtra("society_constitution", societyInfo.getString("society_constitution"));
				i.putExtra("society_website", societyInfo.getString("society_website"));
				i.putExtra("society_email", societyInfo.getString("society_email"));
				
				startActivity(i);
				
			}
		});
		
		if (s.getString("society_name") != null)
		{
			societyName = s.getString("society_name");
			societyNameText.setText(societyName);
		}
		
		if(user.isSubscribedToSociety(societyNameText.getText().toString()))
			subscribeButton.setText("Unsubscribe");
		
		subscribeButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				String societyName = societyNameText.getText().toString();
				
				if(!user.isSubscribedToSociety(societyName))
				{
					Thread t = new Thread(new Runnable()
					{		
						@Override
						public void run()
						{
							try
							{
								InputStream is = NetworkFunctions.getHTTPResponseStream(
										"http://www.dur.ac.uk/cs.seg01/duchess/api/v1/societies.php?userID=" +
										user.getUserID() + "&societyID=" + s.getLong("society_id"), "GET", null);
							}
							catch (IOException e)
							{
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					});
					t.start();
					
					user.getSocieties().add(societyNameText.getText().toString());
					subscribeButton.setText("Unsubscribe");
				}
				else
				{
					// TODO Should also update in the external database not just in the shared preferences 
					user.getSocieties().remove(societyNameText.getText().toString());
					subscribeButton.setText("Subscribe");
				}
				
				SessionFunctions.saveUserPreferences((Activity) v.getContext(), user);
			}
		});

			adapter = new EventListAdapter(this, R.layout.custom_event_list_row, new ArrayList<Event>());
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
					i.putExtra("event_name", e.getName());
					i.putExtra("location_id", l.getLocationID());
					startActivity(i);
				}
			});

			final Runnable callbackFunction = new Runnable()
			{

				@Override
				public void run()
				{
					for (Event event : eventList) adapter.add(event);
					progressDialog.dismiss();
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
						DataProvider data = delegate.getDataProvider();
						eventList = data.getEventsBySociety(context, societyName);
						runOnUiThread(callbackFunction);
					}
					catch (Exception ex)
					{
						ex.printStackTrace();
					}
				}
			};

			Thread thread = new Thread(null, parseData, "DownloadEventsThread");
			thread.start();
			progressDialog = ProgressDialog.show(SocietyEventListActivity.this, "Please wait...",
					"Loading Events ...", true);
		
	}
	
	@Override
	public void onResume()
	{
		super.onResume();
		user = SessionFunctions.getCurrentUser(this);
		listView.setAdapter(listView.getAdapter());
	}

}
