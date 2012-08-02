package uk.ac.dur.duchess.activity;

import uk.ac.dur.duchess.R;
import uk.ac.dur.duchess.data.CalendarFunctions;
import uk.ac.dur.duchess.data.NetworkFunctions;
import uk.ac.dur.duchess.data.SessionFunctions;
import uk.ac.dur.duchess.entity.DBAccess;
import uk.ac.dur.duchess.entity.Event;
import uk.ac.dur.duchess.entity.User;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.util.Linkify;
import android.util.Log;
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
	private Event event;

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
		
		int[] colors = {Color.parseColor("#111111"), Color.parseColor("#222222"), Color.parseColor("#333333")};
		
		GradientDrawable gradient = new GradientDrawable(Orientation.BOTTOM_TOP, colors);
		gradient.setDither(true);
		
		eventImageContainer.setBackgroundDrawable(gradient);

		image = (ImageView) findViewById(R.id.eventImage);

		Bundle e = getIntent().getExtras();
		
		DBAccess database = new DBAccess(this);
		database.open();
		
		event = database.getEvent(e.getLong("event_id"));
		
		database.close();

		eventID = e.getLong("event_id");

		String date = CalendarFunctions.getEventDate(event);

		if (event.getName() != null) txtName.setText(event.getName());
		if (event.getStartDate() != null && event.getEndDate() != null)
			txtDate.setText(date);
		
		if(event.getICalURL() == null || date == "This event has ended") timeChooserButton.setVisibility(View.GONE);
		
		timeChooserButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				Intent timeIntent = new Intent(view.getContext(), TimeActivity.class);
		        timeIntent.putExtra("event_name", event.getName());
		        timeIntent.putExtra("event_start_date", event.getStartDate());
		        timeIntent.putExtra("event_end_date", event.getEndDate());
		        timeIntent.putExtra("event_address", event.getLocation().getAddress1());
		        timeIntent.putExtra("ical_url", event.getICalURL());
		        startActivity(timeIntent);
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
				eventImageContainer.setVisibility(View.GONE);
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
			menu.getItem(0).setIcon(getParent().getResources().getDrawable(R.drawable.bookmark));
			menu.getItem(0).setTitle("Remove Bookmark");
		}
		else
		{
			menu.getItem(0).setIcon(getParent().getResources().getDrawable(R.drawable.clear_bookmark));
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
				item.setIcon(getParent().getResources().getDrawable(R.drawable.clear_bookmark));
				item.setTitle("Bookmark");
				
				user.removeEvent(eventID);
			}
			else
			{
				item.setIcon(getParent().getResources().getDrawable(R.drawable.bookmark));
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