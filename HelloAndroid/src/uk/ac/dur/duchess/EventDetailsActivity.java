package uk.ac.dur.duchess;

import uk.ac.dur.duchess.data.CalendarFunctions;
import uk.ac.dur.duchess.data.NetworkFunctions;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class EventDetailsActivity extends Activity
{
	private ImageView image;
	private TextView txtName;
	private TextView txtDate;
	private TextView txtDescription;
	private Button facebookButton;
	private LinearLayout eventDetailsContainer;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.event_details_layout);

		txtName = (TextView) findViewById(R.id.textViewEventName);
		txtDate = (TextView) findViewById(R.id.textViewEventDate);
		txtDescription = (TextView) findViewById(R.id.textViewEventDescription);
		facebookButton = (Button) findViewById(R.id.facebookButton);
		eventDetailsContainer = (LinearLayout) findViewById(R.id.eventDetailsContainer);

		image = (ImageView) findViewById(R.id.imageView1);
		
		facebookButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				Intent i = new Intent(view.getContext(), FacebookActivity.class);
				i.putExtras(getIntent());
				startActivity(i);
			}
		});
				

		Bundle e = getIntent().getExtras();

		String name = e.getString("event_name");
		String start_date = e.getString("event_start_date");
		String end_date = e.getString("event_end_date");
		String description = e.getString("event_description");
		String linkedAddress = e.getString("linked_address");
		String image_url = e.getString("image_url");

		if (name != null) txtName.setText(name);
		if (start_date != null && end_date != null)
		{
			// TODO needs function to take just a date
			Event event = new Event();
			event.setStartDate(start_date);
			event.setEndDate(end_date);
			txtDate.setText(CalendarFunctions.getEventDate(event));

		}
		if (description != null) txtDescription.setText(description);

		(new DownloadImageTask()).execute(image_url);
	}

	private class DownloadImageTask extends AsyncTask<String, Void, Bitmap>
	{
		@Override
		protected Bitmap doInBackground(String... urlArray)
		{
			try
			{
				return NetworkFunctions.downloadImage(urlArray[0]);
			}
			catch (Exception ex)
			{
				// TODO error handling
			}
			return null;
		}

		@Override
		protected void onPostExecute(Bitmap bitmap)
		{
			image.setAdjustViewBounds(true);
			int width = eventDetailsContainer.getWidth();
			image.setMaxHeight((int) (width * (4.0 / 6.0)));
			image.setMaxWidth(width);
			image.setImageBitmap(bitmap);
			image.invalidate();
		}
	}
}