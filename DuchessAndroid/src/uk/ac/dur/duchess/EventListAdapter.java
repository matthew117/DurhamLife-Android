package uk.ac.dur.duchess;

import java.util.List;

import uk.ac.dur.duchess.data.CalendarFunctions;
import uk.ac.dur.duchess.data.SessionFunctions;
import uk.ac.dur.duchess.entity.Event;
import uk.ac.dur.duchess.entity.User;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class EventListAdapter extends ArrayAdapter<Event>
{
	private List<Event> events;
	private Context context;
	private int rowLayoutResourceID;
	private User user;

	public EventListAdapter(Context context, int rowLayoutResourceID, List<Event> eventList)
	{
		super(context, rowLayoutResourceID, eventList);
		this.events = eventList;
		this.context = context;
		this.rowLayoutResourceID = rowLayoutResourceID;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent)
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
			holder.pinButton = (ImageView) v.findViewById(R.id.pinButton);
			
			user = SessionFunctions.getCurrentUser((Activity) context);
			
			
			v.setTag(holder);
		}
		else
		{
			holder = (ViewHolder) v.getTag();
		}

		Event e = getItem(position);
		
		Log.d("POSITION", ""+position);

		if (e != null)
		{
			Log.d("EVENT ID", ""+e.getEventID());
			if(user.hasPinnedEvent(e.getEventID()))
			{
				Log.d("PINNED LISTVIEW", "Setting image to purple because user has that event");
				holder.pinButton.setImageDrawable(context.getResources().getDrawable(R.drawable.purple_heart));
			}
			else
			{
				Log.d("PINNED LISTVIEW", "Setting image to clear");
				holder.pinButton.setImageDrawable(context.getResources().getDrawable(R.drawable.clear_heart));
			}
			
			holder.pinButton.setOnClickListener(new View.OnClickListener()
			{
				@Override
				public void onClick(View v)
				{					
					ImageView iv = (ImageView) v;
										
					if(user.hasPinnedEvent(getItem(position).getEventID()))
					{
						iv.setImageDrawable(context.getResources().getDrawable(R.drawable.clear_heart));
						user.removeEvent(getItem(position).getEventID());
					}
					else
					{
						iv.setImageDrawable(context.getResources().getDrawable(R.drawable.purple_heart));
						user.addEvent(getItem(position).getEventID());
					}
					
					SessionFunctions.saveUserPreferences((Activity) context, user);
				}
			});
			
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
				
				Bitmap[] stars = {emptyStar, emptyStar, emptyStar, emptyStar, emptyStar};
				
				int r = rating;
				int i = 0;
				
				while(r > 0)
				{
					if(r > 1)
					{
						stars[i] = fullStar;
						r -= 2;
						i++;
					}
					else
					{
						stars[i] = halfStar;
						r--;
					}
				}
				
				holder.star5.setImageBitmap(stars[4]);
				holder.star4.setImageBitmap(stars[3]);
				holder.star3.setImageBitmap(stars[2]);
				holder.star2.setImageBitmap(stars[1]);
				holder.star1.setImageBitmap(stars[0]);
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
		public ImageView pinButton;

		public ImageView star1;
		public ImageView star2;
		public ImageView star3;
		public ImageView star4;
		public ImageView star5;
		public TextView numberOfReviewsDisplay;
	}
}
