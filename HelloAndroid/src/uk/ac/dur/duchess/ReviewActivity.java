package uk.ac.dur.duchess;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ReviewActivity extends Activity
{
	private LinearLayout layout;
	private Bundle e;
	
	// TODO actually needs to submit reviews
	// TODO remove EditText and RatingBar if current user has written a review
	// TODO remove EditText and RatingBar if no user is signed in (anonymous mode)

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.review_layout);

		e = getIntent().getExtras();

		layout = (LinearLayout) findViewById(R.id.reviewLayoutID);
		TextView eventNameTitleText = (TextView) findViewById(R.id.reviewEventNameLabel);

		eventNameTitleText.setText(e.getString("event_name"));
		
		// TODO requires a progress bar of some sort
		(new BackgroundTask()).execute("http://www.dur.ac.uk/cs.seg01/duchess/api/v1/reviews.php/"
				+ e.getLong("event_id"));
	}

	private class BackgroundTask extends AsyncTask<String, Void, List<Review>>
	{
		@Override
		protected List<Review> doInBackground(String... urlArray)
		{
			List<Review> reviewList = new ArrayList<Review>();

			try
			{
				SAXParserFactory factory = SAXParserFactory.newInstance();
				SAXParser parser = factory.newSAXParser();
				XMLReader reader = parser.getXMLReader();

				URL url = new URL(urlArray[0]);

				ReviewXMLParser reviewXMLParser = new ReviewXMLParser(reviewList);

				reader.setContentHandler(reviewXMLParser);

				reader.parse(new InputSource(url.openStream()));

				return reviewList;
			}
			catch (Exception ex)
			{
				return reviewList;
			}
		}
		
		@Override
		protected void onPostExecute(List<Review> reviewList)
		{			
			for (int i = 0; i < reviewList.size(); i++)
			{
				Review review = reviewList.get(i);
				Calendar c = Calendar.getInstance();
				int year = Integer.parseInt(review.getTimestamp().substring(0,4));
				int month = Integer.parseInt(review.getTimestamp().substring(5,7));
				int day = Integer.parseInt(review.getTimestamp().substring(8,10));
				int hourOfDay = Integer.parseInt(review.getTimestamp().substring(11,13));
				int minute = Integer.parseInt(review.getTimestamp().substring(14,16));
				c.set(year, month, day, hourOfDay, minute);
				SimpleDateFormat sdf = new SimpleDateFormat("d MMMMM yyyy");
				TextView t = new TextView(getApplicationContext());
				t.setPadding(10, 5, 5, 5);
				t.setTextColor(Color.BLACK);
				t.setTypeface(Typeface.SERIF);
				t.setBackgroundColor((i % 2 == 0) ? Color.parseColor("#997A99") : Color
						.parseColor("#9C8AA5"));
				t.setText(sdf.format(c.getTime()) + "\n" + review.getComment());
				layout.addView(t);
			}
		}
	}

}
