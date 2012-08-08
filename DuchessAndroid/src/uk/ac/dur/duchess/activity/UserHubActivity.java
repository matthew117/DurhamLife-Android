package uk.ac.dur.duchess.activity;

import uk.ac.dur.duchess.GlobalApplicationData;
import uk.ac.dur.duchess.R;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.google.android.apps.analytics.GoogleAnalyticsTracker;

public class UserHubActivity extends SherlockActivity
{
	private TextView browseButton;
	private TextView collegeEventButton;
	private TextView myEventsButton;
	private TextView newsButton;
	private TextView societiesButton;
	private TextView settingsButton;
	private TextView societyEventListButton;
	private TextView calendarButton;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.user_hub_table);

		browseButton = (TextView) findViewById(R.id.userHubBrowse);
		collegeEventButton = (TextView) findViewById(R.id.userHubCollege);
		myEventsButton = (TextView) findViewById(R.id.userHubMyEvents);
		newsButton = (TextView) findViewById(R.id.userHubNews);
		societiesButton = (TextView) findViewById(R.id.userHubSocieties);
		settingsButton = (TextView) findViewById(R.id.userHubSettings);
		societyEventListButton = (TextView) findViewById(R.id.userHubSocietyEvents);
		calendarButton = (TextView) findViewById(R.id.userHubCalendar);

		browseButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				if (GlobalApplicationData.getAnalyticsPermission(v.getContext()))
				{
					GoogleAnalyticsTracker tracker = ((GlobalApplicationData) getApplication()).getTracker();
					tracker.trackEvent("UserHub", "ButtonClicked", "Browse Events", 0);
					tracker.dispatch();
				}
				Intent i = new Intent(v.getContext(), EventListActivity.class);
				startActivity(i);
			}
		});

		collegeEventButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				if (GlobalApplicationData.getAnalyticsPermission(v.getContext()))
				{
					GoogleAnalyticsTracker tracker = ((GlobalApplicationData) getApplication())
							.getTracker();
					tracker.trackEvent("UserHub", "ButtonClicked", "College Events", 0);
					tracker.dispatch();
				}
				Intent i = new Intent(v.getContext(), CollegeEventListActivity.class);
				startActivity(i);
			}
		});

		myEventsButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				if (GlobalApplicationData.getAnalyticsPermission(v.getContext()))
				{
					GoogleAnalyticsTracker tracker = ((GlobalApplicationData) getApplication())
							.getTracker();
					tracker.trackEvent("UserHub", "ButtonClicked", "Bookmarked Events", 0);
					tracker.dispatch();
				}
				Intent i = new Intent(v.getContext(), BookmarkedEventListActivity.class);
				startActivity(i);
			}
		});

		newsButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				Intent i = new Intent(v.getContext(), TestEventListActivity.class);
				startActivity(i);
//				Toast.makeText(v.getContext(), "Displays a news feed about Durham events",
//						Toast.LENGTH_LONG).show();
			}
		});

		societiesButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				if (GlobalApplicationData.getAnalyticsPermission(v.getContext()))
				{
					GoogleAnalyticsTracker tracker = ((GlobalApplicationData) getApplication())
							.getTracker();
					tracker.trackEvent("UserHub", "ButtonClicked", "Browse Societies", 0);
					tracker.dispatch();
				}
				Intent i = new Intent(v.getContext(), SocietyListActivity.class);
				startActivity(i);
			}
		});

		settingsButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				Intent i = new Intent(v.getContext(), SettingsActivity.class);
				startActivity(i);
			}
		});

		societyEventListButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				if (GlobalApplicationData.getAnalyticsPermission(v.getContext()))
				{
					GoogleAnalyticsTracker tracker = ((GlobalApplicationData) getApplication()).getTracker();
					tracker.trackEvent("UserHub", "ButtonClicked", "Society Events", 0);
					tracker.dispatch();
				}
				Intent i = new Intent(v.getContext(), PersonalSocietyListActivity.class);
				startActivity(i);
			}
		});

		calendarButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				if (GlobalApplicationData.getAnalyticsPermission(v.getContext()))
				{
					GoogleAnalyticsTracker tracker = ((GlobalApplicationData) getApplication())
							.getTracker();
					tracker.trackEvent("UserHub", "ButtonClicked", "Event Calendar", 0);
					tracker.dispatch();
				}
				Intent i = new Intent(v.getContext(), CalendarActivity.class);
				startActivity(i);
			}
		});
	}
	
	public boolean onCreateOptionsMenu(Menu menu)
	{
		getSupportMenuInflater().inflate(R.menu.user_hub_menu, menu);
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
		case R.id.showAboutBoxMenuItem:
			Intent showAboutBoxIntent = new Intent(this, AboutBoxActivity.class);
			startActivity(showAboutBoxIntent);
			return true;
		default:
			return true;
		}
	}

}
