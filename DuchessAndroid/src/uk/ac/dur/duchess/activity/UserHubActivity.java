package uk.ac.dur.duchess.activity;

import com.google.android.apps.analytics.GoogleAnalyticsTracker;

import uk.ac.dur.duchess.AnalyticsInterface;
import uk.ac.dur.duchess.R;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class UserHubActivity extends CustomTitleBarActivity
{
	private TextView browseButton;
	private TextView collegeEventButton;
	private TextView myEventsButton;
	private TextView newsButton;
	private TextView societiesButton;
	private TextView settingsButton;
	private TextView societyEventListButton;

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

		// Custom Title Bar properties
		titleBarButton4.setVisibility(View.GONE);
		titleBarButton3.setVisibility(View.GONE);
		titleBarButton2.setVisibility(View.GONE);

		Bitmap aboutIcon = BitmapFactory.decodeResource(getResources(), R.drawable.about);
		titleBarButton1.setImageBitmap(aboutIcon);
		titleBarButton1.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				Intent i = new Intent(v.getContext(), AboutBoxActivity.class);
				startActivity(i);
			}
		});

		browseButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				GoogleAnalyticsTracker tracker = ((AnalyticsInterface) getApplication())
						.getTracker();
				tracker.trackEvent("UserHub", "ButtonClicked", "Browse Events", 0);
				tracker.dispatch();
				Intent i = new Intent(v.getContext(), EventListActivity.class);
				startActivity(i);
			}
		});

		collegeEventButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				GoogleAnalyticsTracker tracker = ((AnalyticsInterface) getApplication())
						.getTracker();
				tracker.trackEvent("UserHub", "ButtonClicked", "College Events", 0);
				tracker.dispatch();
				Intent i = new Intent(v.getContext(), CollegeEventListActivity.class);
				startActivity(i);
			}
		});

		myEventsButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				Toast.makeText(v.getContext(),
						"Displays a list of events that the user is going to", Toast.LENGTH_LONG)
						.show();
			}
		});

		newsButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				Intent i = new Intent(v.getContext(), ViewSharedPreferencesActivity.class);
				startActivity(i);
				Toast.makeText(v.getContext(), "Displays a news feed about Durham events",
						Toast.LENGTH_LONG).show();
			}
		});

		societiesButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				GoogleAnalyticsTracker tracker = ((AnalyticsInterface) getApplication())
						.getTracker();
				tracker.trackEvent("UserHub", "ButtonClicked", "Browse Societies", 0);
				tracker.dispatch();
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
				GoogleAnalyticsTracker tracker = ((AnalyticsInterface) getApplication())
						.getTracker();
				tracker.trackEvent("UserHub", "ButtonClicked", "Society Events", 0);
				tracker.dispatch();
				Intent i = new Intent(v.getContext(), PersonalSocietyListActivity.class);
				startActivity(i);
			}
		});

	}
}
