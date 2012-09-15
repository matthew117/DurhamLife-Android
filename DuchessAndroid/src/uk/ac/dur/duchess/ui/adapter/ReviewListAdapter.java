package uk.ac.dur.duchess.ui.adapter;

import java.util.List;

import uk.ac.dur.duchess.R;
import uk.ac.dur.duchess.model.Review;
import uk.ac.dur.duchess.util.CalendarUtils;
import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ReviewListAdapter extends ArrayAdapter<Review>
{
	private Context context;
	private int rowLayoutResourceID;
	
	public ReviewListAdapter(Context context, int rowLayoutResourceID, List<Review> reviewList)
	{
		super(context, rowLayoutResourceID, reviewList);
		this.context = context;
		this.rowLayoutResourceID = rowLayoutResourceID;
	}
	
	@Override
	public boolean isEnabled(int position) 
    { 
		return false; 
    }

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		View view = convertView;
		
		if (view == null)
		{
			LayoutInflater inflater = ((Activity) context).getLayoutInflater();
			view = inflater.inflate(rowLayoutResourceID, parent, false);
		}
		
		Review review = getItem(position);
		
		if (review != null)
		{
			TextView timestamp = (TextView) view.findViewById(R.id.reviewSubmitDate);
			TextView comment = (TextView) view.findViewById(R.id.reviewComment);
			
			timestamp.setText(CalendarUtils.getReviewTime(review));
			comment.setText(review.getComment());
			
			ImageView star1 = (ImageView) view.findViewById(R.id.reviewStar1);
			ImageView star2 = (ImageView) view.findViewById(R.id.reviewStar2);
			ImageView star3 = (ImageView) view.findViewById(R.id.reviewStar3);
			ImageView star4 = (ImageView) view.findViewById(R.id.reviewStar4);
			ImageView star5 = (ImageView) view.findViewById(R.id.reviewStar5);

			int rating = review.getRating();

			Drawable emptyStar = context.getResources().getDrawable(R.drawable.star_grey_small);
			Drawable halfStar  = context.getResources().getDrawable(R.drawable.star_half_small);

			if (rating < 10) star5.setImageDrawable(halfStar);
			if (rating <  9) star5.setImageDrawable(emptyStar);
			if (rating <  8) star4.setImageDrawable(halfStar);
			if (rating <  7) star4.setImageDrawable(emptyStar);
			if (rating <  6) star3.setImageDrawable(halfStar);
			if (rating <  5) star3.setImageDrawable(emptyStar);
			if (rating <  4) star2.setImageDrawable(halfStar);
			if (rating <  3) star2.setImageDrawable(emptyStar);
			if (rating <  2) star1.setImageDrawable(halfStar);
			if (rating <  1) star1.setImageDrawable(emptyStar);
		}
		
		return view;
	}
}

