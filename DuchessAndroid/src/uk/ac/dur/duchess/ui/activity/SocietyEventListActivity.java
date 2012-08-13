package uk.ac.dur.duchess.ui.activity;

import java.io.IOException;
import java.io.InputStream;

import uk.ac.dur.duchess.R;
import uk.ac.dur.duchess.io.NetworkFunctions;
import uk.ac.dur.duchess.io.SessionFunctions;
import uk.ac.dur.duchess.model.User;
import uk.ac.dur.duchess.ui.view.EventListView;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

public class SocietyEventListActivity extends SherlockActivity
{
	private EventListView listView;
	private User user;
	
	private long societyID;

	private String societyName;
	
	private Context context;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.society_event_list_layout);

		context = this;
		
		user = SessionFunctions.getCurrentUser(this);

		final Bundle s = getIntent().getExtras();

		if (s.getString("society_name") != null)
		{
			setTitle(s.getString("society_name"));
			societyName = s.getString("society_name");
		}
		
		if (s.getLong("society_id") != -1)
		{
			societyID = s.getLong("society_id");
		}
		
		listView = (EventListView) findViewById(R.id.societyEventListView);
		
		Bundle b = new Bundle();
		b.putString("society_name", societyName);
		
		listView.loadAllEvents(this, b);
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
			menu.getItem(1).setTitle("Subscribe");
		}
		
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
		case R.id.showAboutSocietyMenuItem:
		{
			Intent i = new Intent(context, SocietyAboutActivity.class);
			Bundle societyInfo = getIntent().getExtras();

			i.putExtra("society_id", societyInfo.getString("society_id"));
			i.putExtra("society_name", societyInfo.getString("society_name"));
			i.putExtra("society_constitution", societyInfo.getString("society_constitution"));
			i.putExtra("society_website", societyInfo.getString("society_website"));
			i.putExtra("society_email", societyInfo.getString("society_email"));

			startActivity(i);
			
			return true;
		}
		case R.id.subscribeSocietyMenuItem:
		{
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
		}
		
		default: return true;
		}
	}
}
