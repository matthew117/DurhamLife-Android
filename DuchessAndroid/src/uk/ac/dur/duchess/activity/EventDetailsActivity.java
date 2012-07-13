package uk.ac.dur.duchess.activity;

import uk.ac.dur.duchess.R;
import uk.ac.dur.duchess.data.CalendarFunctions;
import uk.ac.dur.duchess.data.NetworkFunctions;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
	private Button phoneContactButton;
	private Button emailContactButton;
	private Button viewWebsiteButton;
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
		phoneContactButton = (Button) findViewById(R.id.telephoneButton);
		emailContactButton = (Button) findViewById(R.id.emailContactButton);
		viewWebsiteButton = (Button) findViewById(R.id.websiteButton);
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
		String date = CalendarFunctions.getEventDate(start_date, end_date);
		String description = e.getString("event_description");
		String contactTelephone = e.getString("event_contact_telephone_number");
		String contactEmail = e.getString("event_contact_email_address");
		String webAddress = e.getString("event_web_address");
		String image_url = e.getString("image_url");

		if (name != null) txtName.setText(name);
		if (start_date != null && end_date != null) txtDate.setText(date);
		if (description != null) txtDescription.setText(description);
		
		if(contactTelephone != null) phoneContactButton.setText(contactTelephone);
		else phoneContactButton.setVisibility(View.GONE);
		
		phoneContactButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				Intent callIntent = new Intent(Intent.ACTION_CALL);
		        callIntent.setData(Uri.parse("tel:+447794330580"));
		        startActivity(callIntent);
			}
		});
		
		if(contactEmail != null) emailContactButton.setText(contactEmail);
		else emailContactButton.setVisibility(View.GONE);
		
		emailContactButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				Intent intent = new Intent(Intent.ACTION_SEND);
				intent.setType("plain/text");
				intent.putExtra(Intent.EXTRA_EMAIL,new String[] { "jamie_bates_8@live.co.uk" });
				intent.putExtra(Intent.EXTRA_SUBJECT, "");
				intent.putExtra(Intent.EXTRA_TEXT, "");

				startActivity(Intent.createChooser(intent, "Send E-mail"));
			}
		});
		
		if(webAddress != null) viewWebsiteButton.setText(webAddress);
		else viewWebsiteButton.setVisibility(View.GONE);
		
		viewWebsiteButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				String url = getIntent().getExtras().getString("event_web_address");
				Intent i = new Intent(Intent.ACTION_VIEW);
				i.setData(Uri.parse(url));
				startActivity(i);
			}
		});

		(new DownloadImageTask()).execute(image_url);
	}

	private class DownloadImageTask extends AsyncTask<String, Void, Bitmap>
	{
		@Override
		protected Bitmap doInBackground(String... urlArray)
		{
			try { return NetworkFunctions.downloadImage(urlArray[0]); }
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
	
	public boolean onCreateOptionsMenu(Menu menu)
	{
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.event_details_menu, menu);
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
		case R.id.menuItemBookmark:
			
			return true;
		case R.id.menuItemShare:
		{
			Intent i = new Intent(getBaseContext(), FacebookActivity.class);
			i.putExtras(getIntent());
			startActivity(i);
			
			return true;
		}
		default:
			return true;
		}
	}
}