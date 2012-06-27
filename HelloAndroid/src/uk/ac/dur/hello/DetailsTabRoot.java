package uk.ac.dur.hello;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

public class DetailsTabRoot extends TabActivity
{
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tab_root_layout);

		TabHost tabHost = getTabHost();
		
		Bundle e = getIntent().getExtras();

		TabSpec eventDetailsTab = tabHost.newTabSpec("Details");
		eventDetailsTab.setIndicator("Details", getResources().getDrawable(R.drawable.event_info_tab_layout));
		Intent i = new Intent(this, EventDetailsScreen.class);

		i.putExtra("event_name", e.getString("event_name"));
		i.putExtra("event_start_date", e.getString("event_start_date"));
		i.putExtra("event_end_date", e.getString("event_end_date"));
		i.putExtra("event_description", e.getString("event_description"));
		i.putExtra("image_url", e.getString("image_url"));
		eventDetailsTab.setContent(i);

		TabSpec locationTab = tabHost.newTabSpec("Location");
		locationTab.setIndicator("Location", getResources().getDrawable(R.drawable.clock));
		Intent in2 = new Intent(this, LocationActivity.class);
		
		in2.putExtra("event_address1", e.getString("event_address1"));
		in2.putExtra("event_address2", e.getString("event_address2"));
		in2.putExtra("event_city", e.getString("event_city"));
		in2.putExtra("event_postcode", e.getString("event_postcode"));
		in2.putExtra("event_latitude", e.getString("event_latitude"));
		in2.putExtra("event_longitude", e.getString("event_longitude"));
		
		locationTab.setContent(in2);
		
		TabSpec reviewTab = tabHost.newTabSpec("Reviews");
		reviewTab.setIndicator("Review", getResources().getDrawable(R.drawable.clock));
		Intent in3 = new Intent(this, ReviewActivity.class);
		
		in3.putExtra("event_name", e.getString("event_name"));
		in3.putExtra("event_id", e.getLong("event_id"));
		reviewTab.setContent(in3);

		tabHost.addTab(eventDetailsTab);
		tabHost.addTab(locationTab);
		tabHost.addTab(reviewTab);
	}

}
