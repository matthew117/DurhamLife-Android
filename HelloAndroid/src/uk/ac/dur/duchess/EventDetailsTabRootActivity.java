package uk.ac.dur.duchess;

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

		detailsIntent.putExtra("event_name", e.getString("event_name"));
		detailsIntent.putExtra("event_start_date", e.getString("event_start_date"));
		detailsIntent.putExtra("event_end_date", e.getString("event_end_date"));
		detailsIntent.putExtra("event_description", e.getString("event_description"));
		detailsIntent.putExtra("event_contact_telephone_number", e.getString("event_contact_telephone_number"));
		detailsIntent.putExtra("event_contact_email_address", e.getString("event_contact_email_address"));
		detailsIntent.putExtra("event_web_address", e.getString("event_web_address"));
		detailsIntent.putExtra("image_url", e.getString("image_url"));
		
		eventDetailsTab.setContent(detailsIntent);

		TabSpec locationTab = tabHost.newTabSpec("Location");
		locationTab.setIndicator("Location", getResources().getDrawable(R.drawable.location_tab_icon));
		Intent locationIntent = new Intent(this, LocationActivity.class);
		
		locationIntent.putExtra("event_name", e.getString("event_name"));
		locationIntent.putExtra("event_address1", e.getString("event_address1"));
		locationIntent.putExtra("event_address2", e.getString("event_address2"));
		locationIntent.putExtra("event_city", e.getString("event_city"));
		locationIntent.putExtra("event_postcode", e.getString("event_postcode"));
		locationIntent.putExtra("event_latitude", e.getString("event_latitude"));
		locationIntent.putExtra("event_longitude", e.getString("event_longitude"));
		
		locationTab.setContent(locationIntent);
		
		TabSpec reviewTab = tabHost.newTabSpec("Reviews");
		reviewTab.setIndicator("Review", getResources().getDrawable(R.drawable.rating_tab_icon));
		Intent reviewIntent = new Intent(this, ReviewActivity.class);
		
		reviewIntent.putExtra("event_name", e.getString("event_name"));
		reviewIntent.putExtra("event_id", e.getLong("event_id"));
		
		reviewTab.setContent(reviewIntent);

		// Add each tab to the tab host
		tabHost.addTab(eventDetailsTab);
		tabHost.addTab(locationTab);
		tabHost.addTab(reviewTab);
	}

}
