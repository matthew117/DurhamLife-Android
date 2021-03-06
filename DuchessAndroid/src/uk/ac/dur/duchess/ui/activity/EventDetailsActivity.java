package uk.ac.dur.duchess.ui.activity;

import uk.ac.dur.duchess.R;
import uk.ac.dur.duchess.io.NetworkFunctions;
import uk.ac.dur.duchess.io.provider.DatabaseHandler;
import uk.ac.dur.duchess.model.Event;
import uk.ac.dur.duchess.util.CalendarUtils;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.util.Linkify;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class EventDetailsActivity extends Activity
{
	private ImageView image;
	private TextView txtName;
	private TextView txtDate;
	private Button timeChooserButton;
	private TextView txtDescription;
	private Button phoneContactButton;
	private Button emailContactButton;
	private Button viewWebsiteButton;
	private TextView txtAccessibility;
	private LinearLayout eventDetailsContainer;
	private FrameLayout eventImageContainer;
	private ProgressBar imageActivityIndicator;
	
	private Event event;
	private Activity activity;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.event_details_layout);
		
		activity = this;

		txtName = (TextView) findViewById(R.id.textViewEventName);
		txtDate = (TextView) findViewById(R.id.textViewEventDate);
		timeChooserButton = (Button) findViewById(R.id.timeChooserButton);
		txtDescription = (TextView) findViewById(R.id.textViewEventDescription);
		phoneContactButton = (Button) findViewById(R.id.telephoneButton);
		emailContactButton = (Button) findViewById(R.id.emailContactButton);
		viewWebsiteButton = (Button) findViewById(R.id.websiteButton);
		txtAccessibility = (TextView) findViewById(R.id.textViewAccessInfo);
		eventDetailsContainer = (LinearLayout) findViewById(R.id.eventDetailsContainer);
		eventImageContainer = (FrameLayout) findViewById(R.id.eventImageContainer);
		imageActivityIndicator = (ProgressBar) findViewById(R.id.eventDetailsImageProgress);

		image = (ImageView) findViewById(R.id.eventImage);

		Bundle e = getIntent().getExtras();
		
		DatabaseHandler database = new DatabaseHandler(this);
		database.open();
		
		event = database.getEvent(e.getLong("event_id"));
		
		database.close();

		String date = CalendarUtils.getEventDate(event);

		if (event.getName() != null) txtName.setText(event.getName());
		if (event.getStartDate() != null && event.getEndDate() != null)
			txtDate.setText(date);
		
		if(event.getICalURL() == null || date == "This event has ended") timeChooserButton.setVisibility(View.GONE);
		
		timeChooserButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				if(NetworkFunctions.networkIsConnected(activity))
				{
					Intent timeIntent = new Intent(view.getContext(), TimeActivity.class);
			        timeIntent.putExtra("event_name", event.getName());
			        timeIntent.putExtra("event_start_date", event.getStartDate());
			        timeIntent.putExtra("event_end_date", event.getEndDate());
			        timeIntent.putExtra("event_address", event.getLocation().getAddress1());
			        timeIntent.putExtra("ical_url", event.getICalURL());
			        startActivity(timeIntent);
				}
				else Toast.makeText(activity, "Times unavailable." +
					" Are you sure that you have an internet connection?", Toast.LENGTH_LONG).show();
			}
		});
		
		if (event.getDescriptionHeader() != null) txtDescription.setText(event.getDescriptionHeader());
		if (event.getDescriptionBody() != null) txtDescription.append("\n\n" + event.getDescriptionBody());
		
		Linkify.addLinks(txtDescription, Linkify.WEB_URLS);
		
		if(event.getContactTelephoneNumber() != null)
			phoneContactButton.setText(event.getContactTelephoneNumber());
		else phoneContactButton.setVisibility(View.GONE);
		
		phoneContactButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				Intent callIntent = new Intent(Intent.ACTION_CALL);
		        callIntent.setData(Uri.parse("tel:" + event.getContactTelephoneNumber()));
		        startActivity(callIntent);
			}
		});
		
		if(event.getContactEmailAddress() != null)
			emailContactButton.setText(event.getContactEmailAddress());
		else emailContactButton.setVisibility(View.GONE);
		
		emailContactButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				Intent intent = new Intent(Intent.ACTION_SEND);
				intent.setType("plain/text");
				intent.putExtra(Intent.EXTRA_EMAIL, new String[] { event.getContactEmailAddress() });
				intent.putExtra(Intent.EXTRA_SUBJECT, "");
				intent.putExtra(Intent.EXTRA_TEXT, "");

				startActivity(Intent.createChooser(intent, "Send E-mail"));
			}
		});
		
		if(event.getWebAddress() != null) viewWebsiteButton.setText(event.getWebAddress());
		else viewWebsiteButton.setVisibility(View.GONE);
		
		viewWebsiteButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				String url = event.getWebAddress();
				Intent i = new Intent(Intent.ACTION_VIEW);
				i.setData(Uri.parse(url));
				startActivity(i);
			}
		});
		
		if(event.getAccessibilityInformation() != null) txtAccessibility.setText(event.getAccessibilityInformation());

		if(event.getImageURL() != null) (new DownloadImageTask()).execute(event.getImageURL());
		else eventImageContainer.setVisibility(View.GONE);
	}

	private class DownloadImageTask extends AsyncTask<String, Void, Bitmap>
	{
		@Override
		protected Bitmap doInBackground(String... urlArray)
		{
			try { return NetworkFunctions.downloadImage(urlArray[0]); }
			catch (Exception ex)
			{
				imageActivityIndicator.setVisibility(View.GONE);
				eventImageContainer.setVisibility(View.GONE);
				// TODO error handling
			}
			return null;
		}

		@Override
		protected void onPostExecute(Bitmap bitmap)
		{
			if (bitmap == null)
			{
				imageActivityIndicator.setVisibility(View.GONE);
				eventImageContainer.setVisibility(View.GONE);
				image.setVisibility(View.GONE);
				return;
			}
			image.setAdjustViewBounds(true);
			int width = eventDetailsContainer.getWidth();
			image.setMaxHeight((int) (width * (4.0 / 6.0)));
			image.setMaxWidth(width);
			image.setImageBitmap(bitmap);
			imageActivityIndicator.setVisibility(View.GONE);
			image.invalidate();
		}
	}
}