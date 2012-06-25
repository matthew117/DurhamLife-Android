package uk.ac.dur.hello;

import java.net.URL;
import java.util.ArrayList;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import android.app.Activity;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ReviewActivity extends Activity
{
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.review_layout);
		
		Bundle e = getIntent().getExtras();
		
		LinearLayout layout = (LinearLayout) findViewById(R.id.reviewLayoutID);
		TextView eventNameTitleText = (TextView) findViewById(R.id.reviewEventNameLabel);
		
		eventNameTitleText.setText(e.getString("event_name"));

		try
		{
			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser parser = factory.newSAXParser();
			XMLReader reader = parser.getXMLReader();

			URL url = new URL("http://www.dur.ac.uk/cs.seg01/duchess/api/v1/reviews.php/" + e.getLong("event_id"));

			ArrayList<Review> reviewList = new ArrayList<Review>();
			
			ReviewXMLParser reviewXMLParser = new ReviewXMLParser(reviewList);

			reader.setContentHandler(reviewXMLParser);
			
			reader.parse(new InputSource(url.openStream()));
			
			for (Review review : reviewList)
			{
				TextView t = new TextView(this);
				t.setText(review.getComment());
				layout.addView(t);
			}
		}
		catch (Exception ex)
		{
			// TODO write proper exception handling
			ex.printStackTrace();
		}
	}

}
