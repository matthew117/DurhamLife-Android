package uk.ac.dur.duchess.activity;

import uk.ac.dur.duchess.R;
import uk.ac.dur.duchess.data.CalendarFunctions;
import uk.ac.dur.duchess.data.NetworkFunctions;
import uk.ac.dur.duchess.data.SessionFunctions;
import uk.ac.dur.duchess.entity.User;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.util.Linkify;
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
	private Button timeChooserButton;
	private TextView txtDescription;
	private Button phoneContactButton;
	private Button emailContactButton;
	private Button viewWebsiteButton;
	private LinearLayout eventDetailsContainer;
	private LinearLayout eventImageContainer;
	
	private long eventID;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.event_details_layout);

		txtName = (TextView) findViewById(R.id.textViewEventName);
		txtDate = (TextView) findViewById(R.id.textViewEventDate);
		timeChooserButton = (Button) findViewById(R.id.timeChooserButton);
		txtDescription = (TextView) findViewById(R.id.textViewEventDescription);
		phoneContactButton = (Button) findViewById(R.id.telephoneButton);
		emailContactButton = (Button) findViewById(R.id.emailContactButton);
		viewWebsiteButton = (Button) findViewById(R.id.websiteButton);
		eventDetailsContainer = (LinearLayout) findViewById(R.id.eventDetailsContainer);
		eventImageContainer = (LinearLayout) findViewById(R.id.eventImageContainer);
		
		int[] colors = {Color.parseColor("#111111"), Color.parseColor("#333333"), Color.parseColor("#555555")};
		
		GradientDrawable gradient = new GradientDrawable(Orientation.BOTTOM_TOP, colors);
		gradient.setDither(true);
		
		eventImageContainer.setBackgroundDrawable(gradient);

		image = (ImageView) findViewById(R.id.imageView1);

		Bundle e = getIntent().getExtras();

		eventID = e.getLong("event_id");
		final String name = e.getString("event_name");
		final String start_date = e.getString("event_start_date");
		String end_date = e.getString("event_end_date");
		String date = CalendarFunctions.getEventDate(start_date, end_date);
		String descriptionHeader = e.getString("event_description_header");
		String descriptionBody = e.getString("event_description_body");
		String contactTelephone = e.getString("event_contact_telephone_number");
		String contactEmail = e.getString("event_contact_email_address");
		String webAddress = e.getString("event_web_address");
		String image_url = e.getString("image_url");
		final String ical_url = e.getString("ical_url");

		if (name != null) txtName.setText(name);
		if (start_date != null && end_date != null) txtDate.setText(date);
		
		if(ical_url == null) timeChooserButton.setVisibility(View.GONE);
		
		timeChooserButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				Intent timeIntent = new Intent(view.getContext(), TimeActivity.class);
		        timeIntent.putExtra("event_name", name);
		        timeIntent.putExtra("event_start_date", start_date);
		        timeIntent.putExtra("ical_url", ical_url);
		        startActivity(timeIntent);
			}
		});
		
		if (descriptionHeader != null) txtDescription.setText(descriptionHeader);
		if (descriptionBody != null) txtDescription.append("\n\n" + descriptionBody);
		
		Linkify.addLinks(txtDescription, Linkify.WEB_URLS);
		
		if(contactTelephone != null) phoneContactButton.setText(contactTelephone);
		else phoneContactButton.setVisibility(View.GONE);
		
		phoneContactButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				Intent callIntent = new Intent(Intent.ACTION_CALL);
		        callIntent.setData(Uri.parse("tel:+447794330580")); // TODO
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
				intent.putExtra(Intent.EXTRA_EMAIL,new String[] { "jamie_bates_8@live.co.uk" }); // TODO
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
		
		User user = SessionFunctions.getCurrentUser(getParent());
		
		if(user.hasPinnedEvent(eventID))
		{
			menu.getItem(0).setIcon(getParent().getResources().getDrawable(R.drawable.purple_heart));
			menu.getItem(0).setTitle("Remove Bookmark");
		}
		else
		{
			menu.getItem(0).setIcon(getParent().getResources().getDrawable(R.drawable.clear_heart));
			menu.getItem(0).setTitle("Bookmark");
		}
		
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
		case R.id.menuItemBookmark:
			
			User user = SessionFunctions.getCurrentUser(this);
			
			if(user.hasPinnedEvent(eventID))
			{
				item.setIcon(getParent().getResources().getDrawable(R.drawable.clear_heart));
				item.setTitle("Bookmark");
				
				user.removeEvent(eventID);
			}
			else
			{
				item.setIcon(getParent().getResources().getDrawable(R.drawable.purple_heart));
				item.setTitle("Remove Bookmark");
				
				user.addEvent(eventID);
			}
			
			SessionFunctions.saveUserPreferences(this, user);
			
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