package uk.ac.dur.duchess.ui.activity;

import uk.ac.dur.duchess.R;
import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

public class EventDetailsTabRootActivity extends TabActivity
{
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tab_root_layout);

		TabHost tabHost = getTabHost();
		
		Bundle e = getIntent().getExtras();

		TabSpec eventDetailsTab = tabHost.newTabSpec("Details");
		eventDetailsTab.setIndicator("Details", getResources().getDrawable(R.drawable.details_tab_icon));
		Intent detailsIntent = new Intent(this, EventDetailsActivity.class);

		detailsIntent.putExtra("event_id", e.getLong("event_id"));
		
		eventDetailsTab.setContent(detailsIntent);

		TabSpec locationTab = tabHost.newTabSpec("Location");
		locationTab.setIndicator("Location", getResources().getDrawable(R.drawable.location_tab_icon));
		Intent locationIntent = new Intent(this, LocationActivity.class);
		
		locationIntent.putExtra("location_id", e.getLong("location_id"));
		locationIntent.putExtra("event_name", e.getString("event_name"));
		
		locationTab.setContent(locationIntent);
		
		TabSpec reviewTab = tabHost.newTabSpec("Reviews");
		reviewTab.setIndicator("Review", getResources().getDrawable(R.drawable.rating_tab_icon));
		Intent reviewIntent = new Intent(this, ReviewActivity.class);
		
		reviewIntent.putExtra("event_id", e.getLong("event_id"));
		reviewIntent.putExtra("event_name", e.getString("event_name"));
		
		reviewTab.setContent(reviewIntent);

		// Add each tab to the tab host
		tabHost.addTab(eventDetailsTab);
		tabHost.addTab(locationTab);
		tabHost.addTab(reviewTab);
	}

}
