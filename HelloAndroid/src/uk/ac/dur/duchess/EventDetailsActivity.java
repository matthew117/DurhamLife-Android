package uk.ac.dur.duchess;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class EventDetailsActivity extends Activity
{
	private ImageView image;
	private TextView txtName;
	private TextView txtDate;
	private TextView txtDescription;
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.event_details_layout);

		txtName = (TextView) findViewById(R.id.textViewEventName);
		txtDate = (TextView) findViewById(R.id.textViewEventDate);
		txtDescription = (TextView) findViewById(R.id.textViewEventDescription);
		
		image = (ImageView) findViewById(R.id.imageView1);
		
		Bundle e = getIntent().getExtras();
		
		String name = e.getString("event_name");
		String start_date = e.getString("event_start_date");
		String end_date = e.getString("event_end_date");
		String description = e.getString("event_description");
		String image_url = e.getString("image_url");

		if (name != null) txtName.setText(name);
		if (start_date != null && end_date != null)
		{
			try
			{
				SimpleDateFormat sourceFormat = new SimpleDateFormat("yyyy-MM-dd");
				SimpleDateFormat destinationFormat = new SimpleDateFormat("d MMMMM yyyy");
				
				Date startDate = sourceFormat.parse(start_date);
				Date endDate = sourceFormat.parse(end_date);
					
				txtDate.setText(destinationFormat.format(startDate) + " - " +
					destinationFormat.format(endDate));
			}
			catch (ParseException pe)
			{
				txtDate.setText(start_date + " - " + end_date);
			}		
		}
		if (description != null) txtDescription.setText(description);
		
		(new BackgroundTask()).execute(image_url);
	}
	
	private class BackgroundTask extends AsyncTask<String, Void, Bitmap>
	{
		@Override
		protected Bitmap doInBackground(String... urlArray)
		{
			try
			{
				return downloadImage(urlArray[0]);
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
			image.setImageBitmap(bitmap);
			image.invalidate();
		}
	}
	
	// TODO move into networking module
	private InputStream openHTTPConnection(String urlString) throws IOException
	{
		InputStream in = null;
		int response = -1;

		URL url = new URL(urlString);
		URLConnection conn = url.openConnection();

		if (!(conn instanceof HttpURLConnection)) throw new IOException("Not an HTTP Exception");

		try
		{
			HttpURLConnection httpConn = (HttpURLConnection) conn;
			httpConn.setAllowUserInteraction(false);
			httpConn.setInstanceFollowRedirects(true);
			httpConn.setRequestMethod("GET");
			httpConn.connect();
			response = httpConn.getResponseCode();
			if (response == HttpURLConnection.HTTP_OK) in = httpConn.getInputStream();
		}
		catch (Exception ex)
		{
			Toast.makeText(this, ex.getLocalizedMessage(), Toast.LENGTH_LONG).show();
			ex.printStackTrace();
			throw new IOException("Connection Error");
		}
		return in;
	}
	
	private Bitmap downloadImage(String URL)
	{
		Bitmap bitmap = null;
		InputStream in = null;
		
		try
		{
			in = openHTTPConnection(URL);
			bitmap = BitmapFactory.decodeStream(in);
			in.close();
		}
		catch (Exception e)
		{
			// TODO: handle exception
		}
		return bitmap;
	}

}