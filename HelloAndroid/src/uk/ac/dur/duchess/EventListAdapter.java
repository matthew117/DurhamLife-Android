package uk.ac.dur.duchess;

import java.util.ArrayList;

import uk.ac.dur.duchess.data.CalendarFunctions;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class EventListAdapter extends ArrayAdapter<Event>
{
	private ArrayList<Event> events;
	private Context context;
	private int rowLayoutResourceID;

	public EventListAdapter(Context context, int rowLayoutResourceID, ArrayList<Event> events)
	{
		super(context, rowLayoutResourceID, events);
		this.events = events;
		this.context = context;
		this.rowLayoutResourceID = rowLayoutResourceID;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		View v = convertView;
		ViewHolder holder;
		if (v == null)
		{
			LayoutInflater inflater = ((Activity) context).getLayoutInflater();
			v = inflater.inflate(rowLayoutResourceID, parent, false);
			
			holder = new ViewHolder();

			holder.txtEventName = (TextView) v.findViewById(R.id.txtEventName);
			holder.txtEventDescription = (TextView) v.findViewById(R.id.txtEventDescription);
			holder.txtEventDate = (TextView) v.findViewById(R.id.txtEventDate);
			holder.star1 = (ImageView) v.findViewById(R.id.newStar1);
			holder.star2 = (ImageView) v.findViewById(R.id.newStar2);
			holder.star3 = (ImageView) v.findViewById(R.id.newStar3);
			holder.star4 = (ImageView) v.findViewById(R.id.newStar4);
			holder.star5 = (ImageView) v.findViewById(R.id.newStar5);
			holder.numberOfReviewsDisplay = (TextView) v.findViewById(R.id.numberOfReviewsOnList);
			
			v.setTag(holder);
		}
		else
		{
			holder = (ViewHolder) v.getTag();
		}

		Event e = events.get(position);

		if (e != null)
		{
			if (holder.txtEventName != null)
			{
				holder.txtEventName.setText(e.getName());
			}
			if (holder.txtEventDescription != null)
			{
				holder.txtEventDescription.setText(e.getDescriptionHeader());
			}
			if (holder.txtEventDate != null)
			{
				holder.txtEventDate.setText(e.getAddress1() + "\n" + CalendarFunctions.getEventDate(e));
			}
			
			int numberOfReviews = e.getNumberOfReviews();
			
			if (numberOfReviews > 0)
			{
				holder.numberOfReviewsDisplay.setText("based on " + numberOfReviews + " review" + ((numberOfReviews != 1) ? "s" : ""));
				int rating = e.getReviewScore();
				Bitmap emptyStar = BitmapFactory.decodeResource(
						((Activity) context).getResources(), R.drawable.empty_star);
				Bitmap halfStar = BitmapFactory.decodeResource(((Activity) context).getResources(),
						R.drawable.half_star);
				Bitmap fullStar = BitmapFactory.decodeResource(((Activity) context).getResources(),
						R.drawable.full_star);
				
				holder.star1.setVisibility(View.VISIBLE);
				holder.star2.setVisibility(View.VISIBLE);
				holder.star3.setVisibility(View.VISIBLE);
				holder.star4.setVisibility(View.VISIBLE);
				holder.star5.setVisibility(View.VISIBLE);
				holder.numberOfReviewsDisplay.setVisibility(View.VISIBLE);
				
				if (rating == 10)
				{
					holder.star5.setImageBitmap(fullStar);
					holder.star4.setImageBitmap(fullStar);
					holder.star3.setImageBitmap(fullStar);
					holder.star2.setImageBitmap(fullStar);
					holder.star1.setImageBitmap(fullStar);
				}
				else if (rating == 9)
				{
					holder.star5.setImageBitmap(halfStar);
					holder.star4.setImageBitmap(fullStar);
					holder.star3.setImageBitmap(fullStar);
					holder.star2.setImageBitmap(fullStar);
					holder.star1.setImageBitmap(fullStar);
				}
				else if (rating == 8)
				{
					holder.star5.setImageBitmap(emptyStar);
					holder.star4.setImageBitmap(fullStar);
					holder.star3.setImageBitmap(fullStar);
					holder.star2.setImageBitmap(fullStar);
					holder.star1.setImageBitmap(fullStar);
				}
				else if (rating == 7)
				{
					holder.star5.setImageBitmap(emptyStar);
					holder.star4.setImageBitmap(halfStar);
					holder.star3.setImageBitmap(fullStar);
					holder.star2.setImageBitmap(fullStar);
					holder.star1.setImageBitmap(fullStar);
				}
				else if (rating == 6)
				{
					holder.star5.setImageBitmap(emptyStar);
					holder.star4.setImageBitmap(emptyStar);
					holder.star3.setImageBitmap(fullStar);
					holder.star2.setImageBitmap(fullStar);
					holder.star1.setImageBitmap(fullStar);
				}
				else if (rating == 5)
				{
					holder.star5.setImageBitmap(emptyStar);
					holder.star4.setImageBitmap(emptyStar);
					holder.star3.setImageBitmap(halfStar);
					holder.star2.setImageBitmap(fullStar);
					holder.star1.setImageBitmap(fullStar);
				}
				else if (rating == 4)
				{
					holder.star5.setImageBitmap(emptyStar);
					holder.star4.setImageBitmap(emptyStar);
					holder.star3.setImageBitmap(emptyStar);
					holder.star2.setImageBitmap(fullStar);
					holder.star1.setImageBitmap(fullStar);
				}
				else if (rating == 3)
				{
					holder.star5.setImageBitmap(emptyStar);
					holder.star4.setImageBitmap(emptyStar);
					holder.star3.setImageBitmap(emptyStar);
					holder.star2.setImageBitmap(halfStar);
					holder.star1.setImageBitmap(fullStar);
				}
				else if (rating == 2)
				{
					holder.star5.setImageBitmap(emptyStar);
					holder.star4.setImageBitmap(emptyStar);
					holder.star3.setImageBitmap(emptyStar);
					holder.star2.setImageBitmap(emptyStar);
					holder.star1.setImageBitmap(fullStar);
				}
				else if (rating == 1)
				{
					holder.star5.setImageBitmap(emptyStar);
					holder.star4.setImageBitmap(emptyStar);
					holder.star3.setImageBitmap(emptyStar);
					holder.star2.setImageBitmap(emptyStar);
					holder.star1.setImageBitmap(halfStar);
				}
				else
				{
					holder.star5.setImageBitmap(emptyStar);
					holder.star4.setImageBitmap(emptyStar);
					holder.star3.setImageBitmap(emptyStar);
					holder.star2.setImageBitmap(emptyStar);
					holder.star1.setImageBitmap(emptyStar);
				}
			}
			else
			{
				holder.star1.setVisibility(View.GONE);
				holder.star2.setVisibility(View.GONE);
				holder.star3.setVisibility(View.GONE);
				holder.star4.setVisibility(View.GONE);
				holder.star5.setVisibility(View.GONE);
				holder.numberOfReviewsDisplay.setVisibility(View.GONE);
			}

		}
		return v;
	}
	
	private static class ViewHolder
	{
		public TextView txtEventName;
		public TextView txtEventDescription;
		public TextView txtEventDate;

		public ImageView star1;
		public ImageView star2;
		public ImageView star3;
		public ImageView star4;
		public ImageView star5;
		public TextView numberOfReviewsDisplay;
	}
}
