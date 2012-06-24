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

		TabSpec eventDetailsTab = tabHost.newTabSpec("Details");
		eventDetailsTab.setIndicator("Details", getResources().getDrawable(R.drawable.event_info_tab_layout));
		Intent i = new Intent(this, EventDetailsScreen.class);
		String name = getIntent().getExtras().getString("event_name");
		String date = getIntent().getExtras().getString("event_date");
		String description = getIntent().getExtras().getString("event_description");
		i.putExtra("event_name", name);
		i.putExtra("event_date", date);
		i.putExtra("event_description", description);
		eventDetailsTab.setContent(i);

		TabSpec spec2 = tabHost.newTabSpec("Location");
		spec2.setIndicator("Location", getResources().getDrawable(R.drawable.clock));
		Intent in2 = new Intent(this, LocationActivity.class);
		spec2.setContent(in2);

		tabHost.addTab(eventDetailsTab);
		tabHost.addTab(spec2);
	}

}
