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

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

public class SocietyEventListActivity extends SortableListActivity
{
	private User user;

	private long societyID;

	private String societyName;

	private Context context;

	private MenuItem aboutMenuItem;
	private MenuItem subscribeMenuItem;

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

	public boolean onCreateOptionsMenu(Menu menu)
	{
		User user = SessionFunctions.getCurrentUser(this);

		menu.add("About");
		aboutMenuItem = menu.getItem(menu.size() - 1);
		aboutMenuItem.setIcon(R.drawable.action_about);
		aboutMenuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS
				| MenuItem.SHOW_AS_ACTION_WITH_TEXT);

		if (user.isSubscribedToSociety(societyName))
		{
			menu.add("Unsubscribe");
			subscribeMenuItem = menu.getItem(menu.size() - 1);
			subscribeMenuItem.setIcon(getResources().getDrawable(R.drawable.remove_society_action_icon));
			subscribeMenuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS
					| MenuItem.SHOW_AS_ACTION_WITH_TEXT);
		}
		else
		{
			menu.add("Subscribe");
			subscribeMenuItem = menu.getItem(menu.size() - 1);
			subscribeMenuItem.setIcon(getResources().getDrawable(R.drawable.add_society_action_icon));
			subscribeMenuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS
					| MenuItem.SHOW_AS_ACTION_WITH_TEXT);
		}

		return super.onCreateOptionsMenu(menu);
	}

	public boolean onOptionsItemSelected(MenuItem item)
	{
		if (item.equals(aboutMenuItem))
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
		else if (item.equals(subscribeMenuItem))
		{
			if (!user.isSubscribedToSociety(societyName))
			{
				Thread t = new Thread(new Runnable()
				{
					@Override
					public void run()
					{
						try
						{
							InputStream is = NetworkFunctions.getHTTPResponseStream(
									"http://www.dur.ac.uk/cs.seg01/duchess/api/v1/societies.php?userID="
											+ user.getUserID() + "&societyID=" + societyID, "GET", null);
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
				item.setIcon(getResources().getDrawable(R.drawable.remove_society_action_icon));
				item.setTitle("Unsubscribe");
			}
			else
			{
				// TODO Should also update in the external database not just in the shared
				// preferences
				user.getSocieties().remove(societyName);
				item.setIcon(getResources().getDrawable(R.drawable.add_society_action_icon));
				item.setTitle("Subscribe");
			}

			SessionFunctions.saveUserPreferences(this, user);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
