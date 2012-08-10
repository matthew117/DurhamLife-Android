package uk.ac.dur.duchess.ui.activity;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import uk.ac.dur.duchess.R;
import uk.ac.dur.duchess.io.NetworkFunctions;
import uk.ac.dur.duchess.io.SessionFunctions;
import uk.ac.dur.duchess.io.xml.ReviewXMLParser;
import uk.ac.dur.duchess.model.Review;
import uk.ac.dur.duchess.model.User;
import uk.ac.dur.duchess.util.CalendarUtils;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
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

	private Activity activity;
	
	private ProgressBar progressBar;
	private User currentUser;

	// TODO disallow users from reviewing the same event twice in the API
	/* 
	 * Accomplishes this partially on client-side by checking review IDs againt current user ID
	 * EDIT: above solution removed, allowing the same user to submit multiple reviews but
	 * limit spam by removing text and rating once the review has been submitted sucessfully
	 * (i.e. user cannot repeatedly press submit)
	 */

	// TODO allow editing of that user's review

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		
		setContentView(R.layout.review_layout);
		
		activity = this;
		currentUser = SessionFunctions.getCurrentUser(activity);
		e = getIntent().getExtras();

		layout = (LinearLayout) findViewById(R.id.reviewLayoutID);
		eventNameLabel = (TextView) findViewById(R.id.reviewEventNameLabel);
		reviewEditText = (EditText) findViewById(R.id.reviewTextBox);
		ratingBar = (RatingBar) findViewById(R.id.ratingStars);
		submitReviewButton = (Button) findViewById(R.id.submitReviewButton);
		
		progressBar = (ProgressBar) findViewById(R.id.reviewDownloadProgressBar);

		eventNameLabel.setText(e.getString("event_name"));
		
		if(currentUser == null)
		{
			reviewEditText.setVisibility(View.GONE);
			ratingBar.setVisibility(View.GONE);
			submitReviewButton.setVisibility(View.GONE);
			
			TextView t = new TextView(getApplicationContext());
			t.setText("Please sign in to submit a review");
			t.setPadding(5, 5, 5, 5);
			
			layout.addView(t, 1);
		}

		submitReviewButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				if(reviewEditText.getText().length() != 0 && ratingBar.getRating() != 0)
				{
					String userXMLRequest = String.format(
							"<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?><review userID=\"%s\" eventID=\"%s\">"
									+ "<rating>%d</rating>" + "<post>%s</post>"
									+ "<timestamp>%s</timestamp>" + "</review>",
							currentUser.getUserID(), e.getLong("event_id"),
							(int) (ratingBar.getRating() * 2), reviewEditText.getText(),
							System.currentTimeMillis() / 1000);

					try
					{
						InputStream response = NetworkFunctions.getHTTPResponseStream(
								"http://www.dur.ac.uk/cs.seg01/duchess/api/v1/reviews.php/"
										+ e.getLong("event_id"), "PUT", userXMLRequest.getBytes());
						
						reviewEditText.setText("");
						ratingBar.setRating(0);
						
						Toast.makeText(v.getContext(), "Review submitted", Toast.LENGTH_LONG).show();
					}
					catch (IOException e1)
					{
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
				else if(reviewEditText.getText().length() == 0)
				{
					Toast.makeText(v.getContext(), "Review content is empty!", Toast.LENGTH_LONG).show();
					return;
				}
				else if(ratingBar.getRating() == 0)
				{
					Toast.makeText(v.getContext(), "Please rate this event", Toast.LENGTH_LONG).show();
					return;
				}
			}
		});

		(new BackgroundTask()).execute("http://www.dur.ac.uk/cs.seg01/duchess/api/v1/reviews.php/"
				+ e.getLong("event_id"));
	}

	private class BackgroundTask extends AsyncTask<String, Void, List<Review>>
	{
		@Override
		protected void onPreExecute()
		{
			progressBar.setVisibility(View.VISIBLE);
		}
		
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
			progressBar.setVisibility(View.GONE);
			
			if (reviewList.size() == 0)
			{
				TextView t = new TextView(getApplicationContext());
				t.setText("There are no reviews for this event yet.");
				t.setPadding(5, 5, 5, 5);
				layout.addView(t);
			}
			
			for (int i = 0; i < reviewList.size(); i++)
			{
				Review review = reviewList.get(i);

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

				commentDate.setText(CalendarUtils.getReviewTime(review));
				commentText.setText(review.getComment());

				int rating = review.getRating();

				Bitmap emptyStar = BitmapFactory.decodeResource(getResources(),
						R.drawable.empty_star);
				Bitmap halfStar = BitmapFactory
						.decodeResource(getResources(), R.drawable.half_star);

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
			}
		}
	}

}
