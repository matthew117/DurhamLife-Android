package uk.ac.dur.duchess.ui.activity;

import uk.ac.dur.duchess.GlobalApplicationData;
import uk.ac.dur.duchess.R;
import uk.ac.dur.duchess.io.NetworkFunctions;
import uk.ac.dur.duchess.io.SessionFunctions;
import uk.ac.dur.duchess.model.User;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.google.android.apps.analytics.GoogleAnalyticsTracker;

public class DashboardActivity extends BaseActivity
{
	private static final String AD_API_URL = "http://www.dur.ac.uk/cs.seg01/duchess/api/v1/features.php";

	private Context context;

	private static final int REQUEST_AFFILIATION = 1;
	private static final int REQUEST_SETTINGS = 2;

	private FrameLayout buttonGrid;

	private ViewSwitcher adViewSwitcher;
	private TextView adText;
	private String adLink;

	private ImageView defaultImage;
	private ImageView adImage;
	private ImageView adNavigationIndicator;

	private boolean imageHasLoaded = false;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dashboard);

		buttonGrid = (FrameLayout) findViewById(R.id.dashboardButtonGrid);

		User user = SessionFunctions.getCurrentUser(this);

		switch(user.getAffiliation())
		{
			case NONE:
			{
				LayoutInflater inflater = getLayoutInflater();
				View v = inflater.inflate(R.layout.user_dashboard_button_grid, buttonGrid, true);
				break;
			}
			case STUDENT:
			case STAFF:
			{
				LayoutInflater inflater = getLayoutInflater();
				View v = inflater.inflate(R.layout.durham_dashboard_button_grid, buttonGrid, true);
				break;
			}
		}

		context = this;

		adViewSwitcher = (ViewSwitcher) findViewById(R.id.dashboardAdAnimator);

		defaultImage = new ImageView(this);
		defaultImage.setScaleType(ScaleType.CENTER_CROP);
		defaultImage.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.the_great_wave_of_kanagawa));

		adViewSwitcher.addView(defaultImage);
		adText = (TextView) findViewById(R.id.dashboardAdText);
		adNavigationIndicator = (ImageView) findViewById(R.id.adNavigationIndicator);

		if (adNavigationIndicator != null)
			adNavigationIndicator.setOnClickListener(new View.OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					if (adLink != null && imageHasLoaded)
					{
						Intent i = new Intent(Intent.ACTION_VIEW);
						i.setData(Uri.parse(adLink));
						startActivity(i);
					}
				}
			});

		(new DownloadImageTask()).execute("");
		
		if (GlobalApplicationData.isFirstRun(this))
		{
			GlobalApplicationData.setFirstRun(this, false);
			
			Intent i = new Intent(this, RegisterActivity.class);
			startActivityForResult(i, REQUEST_SETTINGS);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int responseCode, Intent data)
	{
		switch (requestCode)
		{
			case REQUEST_AFFILIATION:
			{
				if (responseCode == RESULT_OK)
				{
					User user = SessionFunctions.getCurrentUser(this);
					buttonGrid.removeAllViews();
	
					switch(user.getAffiliation())
					{
						case NONE:
						{
							LayoutInflater inflater = getLayoutInflater();
							View v = inflater.inflate(R.layout.user_dashboard_button_grid, buttonGrid, true);
							break;
						}
						case STUDENT:
						case STAFF:
						{
							LayoutInflater inflater = getLayoutInflater();
							View v = inflater.inflate(R.layout.durham_dashboard_button_grid, buttonGrid, true);
							break;
						}
					}
				}
				break;
			}
			case REQUEST_SETTINGS:
			{			
				if (responseCode == RESULT_OK)
				{
					Intent settingsIntent = new Intent(this, SettingsActivity.class);
					startActivityForResult(settingsIntent, REQUEST_AFFILIATION);
				}
				break;
			}
			default: break;
		}
	}

	@Override
	public void onResume()
	{
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		super.onResume();
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
			startActivityForResult(settingsIntent, REQUEST_AFFILIATION);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig)
	{
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
				adLink = adSpec[3];
				return NetworkFunctions.downloadImage(adSpec[2]); 
			}
			catch (Exception ex)
			{
				return null;
			}
		}

		@Override
		protected void onPostExecute(Bitmap bitmap)
		{
			if (bitmap == null || text == null)
			{
				return;
			}
			adText.setText(text);

			Animation navIndicatorFade = AnimationUtils.loadAnimation(context, R.anim.image_fadein_animation);

			adNavigationIndicator.setVisibility(View.VISIBLE);
			adNavigationIndicator.startAnimation(navIndicatorFade);

			adImage = new ImageView(context);
			adImage.setScaleType(ScaleType.CENTER_CROP);
			adImage.setImageBitmap(bitmap);

			adViewSwitcher.addView(adImage);
			adViewSwitcher.showNext();
			imageHasLoaded = true;
		}
	}

	public void startBrowseActivity(View v)
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

	public void startCalendarActivity(View v)
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

	public void startBookmarkActivity(View v)
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

	public void startCollegeEventsActivity(View v)
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

	public void startSocietyBrowseActivity(View v)
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

	public void startMySocietiesActivity(View v)
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

}
