package uk.ac.dur.duchess;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import uk.ac.dur.duchess.data.NetworkFunctions;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

public class ReviewActivity extends Activity
{
	private LinearLayout layout;
	private Bundle e;

	private TextView eventNameLabel;
	private EditText reviewEditText;
	private RatingBar ratingBar;
	private Button submitReviewButton;

	// TODO remove EditText and RatingBar if current user has written a review
	// TODO remove EditText and RatingBar if no user is signed in (anonymous)
	
	// TODO allow editing of that user's review
	// TODO add API function that only returns reviews updated since a certain time
	
	// TODO add stars to each review
	// TODO general formatting

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.review_layout);

		e = getIntent().getExtras();

		layout = (LinearLayout) findViewById(R.id.reviewLayoutID);
		eventNameLabel = (TextView) findViewById(R.id.reviewEventNameLabel);
		reviewEditText = (EditText) findViewById(R.id.reviewTextBox);
		ratingBar = (RatingBar) findViewById(R.id.ratingStars);
		submitReviewButton = (Button) findViewById(R.id.submitReviewButton);

		eventNameLabel.setText(e.getString("event_name"));

		submitReviewButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				String userXMLRequest = String.format(
						"<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?><review userID=\"%s\" eventID=\"%s\">"
								+ "<rating>%d</rating>" + "<post>%s</post>"
								+ "<timestamp>%s</timestamp>" + "</review>", getCurrentUserID(),
						e.getLong("event_id"), (int) (ratingBar.getRating() * 2),
						reviewEditText.getText(), System.currentTimeMillis() / 1000);
				Toast.makeText(getApplicationContext(), userXMLRequest, Toast.LENGTH_LONG).show();

				try
				{
					InputStream response = NetworkFunctions.getHTTPResponseStream(
							"http://www.dur.ac.uk/cs.seg01/duchess/api/v1/reviews.php/"
									+ e.getLong("event_id"), "PUT", userXMLRequest.getBytes());
				}
				catch (IOException e1)
				{
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});

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
				try
				{
					Review review = reviewList.get(i);

					SimpleDateFormat sourceFormat = new SimpleDateFormat("yyyy-MM-dd");
					SimpleDateFormat destinationFormat = new SimpleDateFormat("d MMMMM yyyy");

					Date date = sourceFormat.parse(review.getTimestamp());

					View v = new View(getApplicationContext());
					
					LayoutInflater inflater = getLayoutInflater();
		            v = inflater.inflate(R.layout.review_comment_layout, layout, false);
		            
		            TextView commentDate = (TextView) v.findViewById(R.id.reviewSubmitDate);
		            TextView commentText = (TextView) v.findViewById(R.id.reviewComment);
		            ImageView star1 = (ImageView) v.findViewById(R.id.reviewStar1);
		            ImageView star2 = (ImageView) v.findViewById(R.id.reviewStar2);
		            ImageView star3 = (ImageView) v.findViewById(R.id.reviewStar3);
		            ImageView star4 = (ImageView) v.findViewById(R.id.reviewStar4);
		            ImageView star5 = (ImageView) v.findViewById(R.id.reviewStar5);
		            
		            commentDate.setText(destinationFormat.format(date));
		            commentText.setText(review.getComment());
		            
		            int rating = review.getRating();
		            
		            Bitmap emptyStar = BitmapFactory.decodeResource(getResources(), R.drawable.empty_star);
		            Bitmap halfStar = BitmapFactory.decodeResource(getResources(), R.drawable.half_star);
		            
		            if (rating < 10) star5.setImageBitmap(halfStar);
		            if (rating < 9) star5.setImageBitmap(emptyStar);
		            if (rating < 8) star4.setImageBitmap(halfStar);
		            if (rating < 7) star4.setImageBitmap(emptyStar);
		            if (rating < 6) star3.setImageBitmap(halfStar);
		            if (rating < 5) star3.setImageBitmap(emptyStar);
		            if (rating < 4) star2.setImageBitmap(halfStar);
		            if (rating < 3) star2.setImageBitmap(emptyStar);
		            if (rating < 2) star1.setImageBitmap(halfStar);
		            if (rating < 1) star1.setImageBitmap(emptyStar);
					
		            layout.addView(v);
//					TextView t = new TextView(getApplicationContext());
//					t.setPadding(10, 5, 5, 5);
//					t.setTextColor(Color.BLACK);
//					t.setTypeface(Typeface.SERIF);
//					t.setBackgroundColor((i % 2 == 0) ? Color.parseColor("#997A99") : Color
//							.parseColor("#9C8AA5"));
//					t.setText(destinationFormat.format(date) + "\n" + review.getComment());
//					layout.addView(t);
				}
				catch (ParseException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}
	}

	private long getCurrentUserID()
	{
		// TODO to be implemented properly once login works
		return 29;
	}

}
