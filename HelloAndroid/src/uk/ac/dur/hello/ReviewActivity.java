package uk.ac.dur.hello;

import java.net.URL;
import java.util.ArrayList;
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
				TextView t = new TextView(getApplicationContext());
				t.setPadding(10, 5, 5, 5);
				t.setTextColor(Color.BLACK);
				t.setTypeface(Typeface.SERIF);
				t.setBackgroundColor((i % 2 == 0) ? Color.parseColor("#ef9be8") : Color
						.parseColor("#d481ce"));
				t.setText(review.getComment());
				layout.addView(t);
			}
		}
	}

}
