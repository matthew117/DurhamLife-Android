package uk.ac.dur.duchess.ui.activity;

import uk.ac.dur.duchess.R;
import uk.ac.dur.duchess.io.provider.DatabaseHandler;
import uk.ac.dur.duchess.model.Event;
import uk.ac.dur.duchess.util.CalendarUtils;
import android.app.LocalActivityManager;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.Toast;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

public class EventDetailsTabRootActivity extends BaseActivity
{
	private LocalActivityManager tabManager;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tab_root_layout);
		
        tabManager = new LocalActivityManager(this, false);
        tabManager.dispatchCreate(savedInstanceState);
        
        TabHost tabHost = (TabHost) findViewById(R.id.tab_root);
        tabHost.setup(tabManager);
		
		Bundle e = getIntent().getExtras();

		TabSpec detailsTab = tabHost.newTabSpec("Details");
		detailsTab.setIndicator("Details", getResources().getDrawable(R.drawable.details_tab_icon));
		Intent detailsIntent = new Intent(this, EventDetailsActivity.class);

		detailsIntent.putExtra("event_id", e.getLong("event_id"));
		
		detailsTab.setContent(detailsIntent);

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

		tabHost.addTab(detailsTab);
		tabHost.addTab(locationTab);
		tabHost.addTab(reviewTab);
	}
	
	public boolean onCreateOptionsMenu(Menu menu)
	{
		getSupportMenuInflater().inflate(R.menu.event_details_menu, menu);
		
		return super.onCreateOptionsMenu(menu);
	}

	public boolean onOptionsItemSelected(MenuItem item)
	{
		Bundle e = getIntent().getExtras();
		
		DatabaseHandler database = new DatabaseHandler(this);
		database.open();
		
		Event event = database.getEvent(e.getLong("event_id"));
		
		database.close();
		
		if(event == null)
		{
			Toast.makeText(this, "Unable to obtain event details!", Toast.LENGTH_SHORT).show();
			return super.onOptionsItemSelected(item);
		}
		
		switch (item.getItemId())
		{
			case R.id.submenuFacebook:
			{
				Intent i = new Intent(getBaseContext(), FacebookActivity.class);
				
				i.putExtra("event_name", event.getName());
				i.putExtra("event_start_date", event.getStartDate());
				i.putExtra("event_end_date", event.getEndDate());
				i.putExtra("event_description", event.getDescriptionHeader());
				i.putExtra("image_url", event.getImageURL());
				i.putExtra("event_web_address", event.getWebAddress());

				startActivity(i);
				
				return true;
			}
			case R.id.submenuShareMore:
			{
				Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
				shareIntent.setType("text/plain");
				
				shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, event.getName());
				shareIntent.putExtra(android.content.Intent.EXTRA_TEXT,
						event.getLocation().getAddress1() + "\n"
					  + CalendarUtils.getEventDate(event) + "\n\n"
					  + event.getDescriptionHeader());
				
				startActivity(Intent.createChooser(shareIntent, "Share via"));
				
				return true;
			}
		}	
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public void onResume()
	{
		super.onResume();
		tabManager.dispatchResume();
	}
	
	@Override
	public void onPause()
	{
		super.onPause();
		tabManager.dispatchPause(isFinishing());

	}
}
