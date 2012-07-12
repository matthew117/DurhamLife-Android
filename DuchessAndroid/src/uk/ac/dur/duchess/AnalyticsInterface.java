package uk.ac.dur.duchess;

import com.google.android.apps.analytics.GoogleAnalyticsTracker;

import android.app.Application;

public class AnalyticsInterface extends Application
{
	private static final String TRACKING_ID = "UA-33340216-1";
	
	private GoogleAnalyticsTracker tracker;
	
	@Override
	public void onCreate()
	{
		super.onCreate();
		
		tracker = GoogleAnalyticsTracker.getInstance();
		tracker.startNewSession(TRACKING_ID, getApplicationContext());
	}
	
	public GoogleAnalyticsTracker getTracker()
	{
		return tracker;
	}
}
