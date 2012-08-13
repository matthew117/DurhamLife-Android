package uk.ac.dur.duchess.ui.activity;

import uk.ac.dur.duchess.GlobalApplicationData;
import uk.ac.dur.duchess.R;
import uk.ac.dur.duchess.io.NetworkFunctions;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.google.android.apps.analytics.GoogleAnalyticsTracker;

public class UserHubActivity extends SherlockActivity
{
	private static final String AD_API_URL = "http://www.dur.ac.uk/cs.seg01/duchess/api/v1/features.php";
	
	private TextView browseButton;
	private TextView collegeEventButton;
	private TextView myEventsButton;
	private TextView societiesButton;
	private TextView societyEventListButton;
	private TextView calendarButton;
	
	private ImageView adImageView;
	private TextView adText;
	private ProgressBar adDownloadActivityIndicator;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dashboard);

		browseButton = (TextView) findViewById(R.id.dashboard_browse_button);
		collegeEventButton = (TextView) findViewById(R.id.dashboard_college_button);
		myEventsButton = (TextView) findViewById(R.id.dashboard_bookmarked_button);
		societiesButton = (TextView) findViewById(R.id.dashboard_societies_button);
		societyEventListButton = (TextView) findViewById(R.id.dashboard_subscriptions_button);
		calendarButton = (TextView) findViewById(R.id.dashboard_calendar_button);
		
		adImageView = (ImageView) findViewById(R.id.dashboardImageView);
		adText = (TextView) findViewById(R.id.dashboardAdText);
		adDownloadActivityIndicator = (ProgressBar) findViewById(R.id.dashboardAdProgress);

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
		
		(new DownloadImageTask()).execute("");
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
		case R.id.dashboardSettingsMenuItem:
			Intent settingsIntent = new Intent(this, SettingsActivity.class);
			startActivity(settingsIntent);
			return true;
		default:
			return true;
		}
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
	    super.onConfigurationChanged(newConfig);
	    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
	}
	
	private class DownloadImageTask extends AsyncTask<String, Void, Bitmap>
	{
		private String text;
		@Override
		protected Bitmap doInBackground(String... vargs)
		{
			try 
			{ 
				String adSpec[] = NetworkFunctions.downloadText(AD_API_URL).split("\n");
				text = adSpec[1];
				return NetworkFunctions.downloadImage(adSpec[2]); 
			}
			catch (Exception ex)
			{
				if (adDownloadActivityIndicator != null && adDownloadActivityIndicator.isShown())
					adDownloadActivityIndicator.setVisibility(View.GONE);
				// TODO error handling
			}
			return null;
		}

		@Override
		protected void onPostExecute(Bitmap bitmap)
		{
			if (bitmap == null)
			{
				if (adDownloadActivityIndicator != null && adDownloadActivityIndicator.isShown())
					adDownloadActivityIndicator.setVisibility(View.GONE);
				//TODO error handling
			}
			adText.setText(text);
			adImageView.setImageBitmap(bitmap);
			adImageView.invalidate();
			if (adDownloadActivityIndicator != null && adDownloadActivityIndicator.isShown())
				adDownloadActivityIndicator.setVisibility(View.GONE);
		}
	}

}
