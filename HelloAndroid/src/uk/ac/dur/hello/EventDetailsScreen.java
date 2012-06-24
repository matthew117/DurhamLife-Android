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
		String date = getIntent().getExtras().getString("event_date");
		String description = getIntent().getExtras().getString("event_description");

		TextView txtName = (TextView) findViewById(R.id.textViewEventName);
		TextView txtDate = (TextView) findViewById(R.id.textViewEventDate);
		TextView txtDescription = (TextView) findViewById(R.id.textViewEventDescription);

		if (name != null) txtName.setText(name);
		if (date != null) txtDate.setText(date);
		if (description != null) txtDescription.setText(description);
	}

}
