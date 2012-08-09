package uk.ac.dur.duchess.activity;

import java.io.IOException;
import java.io.InputStream;

import uk.ac.dur.duchess.EventListView;
import uk.ac.dur.duchess.R;
import uk.ac.dur.duchess.data.NetworkFunctions;
import uk.ac.dur.duchess.data.SessionFunctions;
import uk.ac.dur.duchess.entity.User;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

public class SocietyEventListActivity extends SherlockActivity
{
	private EventListView listView;
	private User user;
	private TextView societyNameText;
	
	private long societyID;

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
			setTitle(s.getString("society_name"));
			societyName = s.getString("society_name");
			societyNameText.setText(societyName);
		}
		
		if (s.getLong("society_id") != -1)
		{
			societyID = s.getLong("society_id");
		}
		
		listView = (EventListView) findViewById(R.id.societyEventListView);
		
		Bundle b = new Bundle();
		b.putString("society_name", societyName);
		
		listView.loadAllEvents(this, b);

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
	}

	@Override
	public void onResume()
	{
		super.onResume();
		user = SessionFunctions.getCurrentUser(this);
		listView.setAdapter(listView.getAdapter());
	}
	
	public boolean onCreateOptionsMenu(Menu menu)
	{
		getSupportMenuInflater().inflate(R.menu.society_event_list_menu, menu);
		
		User user = SessionFunctions.getCurrentUser(this);
		
		if(user.isSubscribedToSociety(societyName))
		{
			menu.getItem(1).setIcon(getResources().getDrawable(R.drawable.action_remove));
			menu.getItem(1).setTitle("Unsubscribe");
		}
		else
		{
			menu.getItem(1).setIcon(getResources().getDrawable(R.drawable.action_add));
			menu.getItem(1).setTitle("Subcribe");
		}
		
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
		case R.id.showAboutSocietyMenuItem:
			Intent showAboutBoxIntent = new Intent(this, SocietyAboutActivity.class);
			startActivity(showAboutBoxIntent);
			return true;
		case R.id.subscribeSocietyMenuItem:

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
											user.getUserID() + "&societyID=" + societyID, "GET", null);
						}
						catch (IOException e)
						{
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				});
				t.start();

				user.getSocieties().add(societyName);
				item.setIcon(getResources().getDrawable(R.drawable.action_remove));
				item.setTitle("Unsubscribe");
			}
			else
			{
				// TODO Should also update in the external database not just in the shared preferences 
				user.getSocieties().remove(societyName);
				item.setIcon(getResources().getDrawable(R.drawable.action_add));
				item.setTitle("Subscribe");
			}

			SessionFunctions.saveUserPreferences(this, user);
			return true;
		default:
			return true;
		}
	}
}
