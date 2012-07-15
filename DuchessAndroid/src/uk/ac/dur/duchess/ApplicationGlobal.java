package uk.ac.dur.duchess;

import com.google.android.apps.analytics.GoogleAnalyticsTracker;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class ApplicationGlobal extends Application
{	
	private static final String FIRST_RUN_KEY = "first_run";
	
	public static final String REPORTING_PREFERENCES_KEY = "report_permissions";
	private static final String ANALYTICS_PERMISSION_KEY = "report_usage";
	private static final String BUGSENSE_PERMISSION_KEY = "report_bugs";
	
	private static final String GOOGLE_ANALYTICS_TRACKING_ID = "UA-33340216-1";
	private GoogleAnalyticsTracker tracker;
	
	public static final String BUGSENSE_API_KEY = "6b8d4b74";
	
	@Override
	public void onCreate()
	{
		super.onCreate();
		
		tracker = GoogleAnalyticsTracker.getInstance();
		tracker.startNewSession(GOOGLE_ANALYTICS_TRACKING_ID, getApplicationContext());
	}
	
	public GoogleAnalyticsTracker getTracker()
	{
		return tracker;
	}
	
	public static boolean setAnalyticsPermission(boolean reportUsage, Context context)
	{
		Editor editor = 
				context.getSharedPreferences(REPORTING_PREFERENCES_KEY, Context.MODE_PRIVATE).edit();
		editor.putBoolean(ANALYTICS_PERMISSION_KEY, reportUsage);
		return editor.commit();
	}
	
	public static boolean getAnalyticsPermission(Context context)
	{
		SharedPreferences sharedPreferences = 
				context.getSharedPreferences(REPORTING_PREFERENCES_KEY,Context.MODE_PRIVATE);
		return sharedPreferences.getBoolean(ANALYTICS_PERMISSION_KEY, false);
	}
	
	public static boolean setBugsensePermission(boolean reportBugs, Context context)
	{
		Editor editor = 
				context.getSharedPreferences(REPORTING_PREFERENCES_KEY, Context.MODE_PRIVATE).edit();
		editor.putBoolean(BUGSENSE_PERMISSION_KEY, reportBugs);
		return editor.commit();
	}
	
	public static boolean getBugsensePermission(Context context)
	{
		SharedPreferences sharedPreferences = 
				context.getSharedPreferences(REPORTING_PREFERENCES_KEY,Context.MODE_PRIVATE);
		return sharedPreferences.getBoolean(BUGSENSE_PERMISSION_KEY, false);
	}
	
	public static void clearReportingPreferences(Context context)
	{
		Editor editor = 
				context.getSharedPreferences(REPORTING_PREFERENCES_KEY, Context.MODE_PRIVATE).edit();
		editor.clear();
		editor.commit();
	}
	
	public static boolean isFirstRun(Context context)
	{
		SharedPreferences sharedPreferences = 
				context.getSharedPreferences(REPORTING_PREFERENCES_KEY,Context.MODE_PRIVATE);
		return sharedPreferences.getBoolean(FIRST_RUN_KEY, true);
	}
	
	public static boolean setFirstRun(Context context, boolean isFirstRun)
	{
		Editor editor = 
				context.getSharedPreferences(REPORTING_PREFERENCES_KEY, Context.MODE_PRIVATE).edit();
		editor.putBoolean(FIRST_RUN_KEY, isFirstRun);
		return editor.commit();
	}
}
