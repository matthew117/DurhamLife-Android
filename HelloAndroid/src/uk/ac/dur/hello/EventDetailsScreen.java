package uk.ac.dur.hello;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class EventDetailsScreen extends Activity
{
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.event_details_page);

		String name = getIntent().getExtras().getString("event_name");
		String start_date = getIntent().getExtras().getString("event_start_date");
		String end_date = getIntent().getExtras().getString("event_end_date");
		String description = getIntent().getExtras().getString("event_description");

		TextView txtName = (TextView) findViewById(R.id.textViewEventName);
		TextView txtDate = (TextView) findViewById(R.id.textViewEventDate);
		TextView txtDescription = (TextView) findViewById(R.id.textViewEventDescription);

		if (name != null) txtName.setText(name);
		if (start_date != null && end_date != null) txtDate.setText(start_date + " - " + end_date);
		if (description != null) txtDescription.setText(description);
	}

}
