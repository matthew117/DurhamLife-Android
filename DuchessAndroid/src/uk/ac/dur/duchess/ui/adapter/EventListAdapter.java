package uk.ac.dur.duchess.ui.adapter;

import java.util.List;

import uk.ac.dur.duchess.R;
import uk.ac.dur.duchess.io.SessionHandler;
import uk.ac.dur.duchess.model.Event;
import uk.ac.dur.duchess.model.User;
import uk.ac.dur.duchess.util.CalendarUtils;
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
	private Context context;
	private int rowLayoutResourceID;
	private User user;
	
	private static final Integer[] CATEGORY_ICONS = new Integer[]
		{R.drawable.university_black, R.drawable.college_black, R.drawable.music_black,
		 R.drawable.theatre_black, R.drawable.exhibition_black, R.drawable.sport_black,
		 R.drawable.conference_black, R.drawable.community_black};

	public EventListAdapter(Context context, int rowLayoutResourceID, List<Event> eventList)
	{
		super(context, rowLayoutResourceID, eventList);
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
			holder.categoryIcon1 = (ImageView) v.findViewById(R.id.categoryIcon1);
			holder.categoryIcon2 = (ImageView) v.findViewById(R.id.categoryIcon2);
			holder.categoryIcon3 = (ImageView) v.findViewById(R.id.categoryIcon3);
			holder.star1 = (ImageView) v.findViewById(R.id.newStar1);
			holder.star2 = (ImageView) v.findViewById(R.id.newStar2);
			holder.star3 = (ImageView) v.findViewById(R.id.newStar3);
			holder.star4 = (ImageView) v.findViewById(R.id.newStar4);
			holder.star5 = (ImageView) v.findViewById(R.id.newStar5);
			holder.numberOfReviewsDisplay = (TextView) v.findViewById(R.id.numberOfReviewsOnList);
			holder.pinButton = (ImageView) v.findViewById(R.id.pinButton);
			
			user = SessionHandler.getCurrentUser((Activity) context);
			
			v.setTag(holder);
		}
		else
		{
			holder = (ViewHolder) v.getTag();
		}

		Event e = getItem(position);

		if (e != null)
		{
			List<String> categories = e.getCategoryTags();
			
			if(categories != null)
			{
				if(categories.size() > 0)
				{
					int id = mapCategoryToIcon(categories.get(0));
					if(id != -1)
					{
						holder.categoryIcon1.setImageDrawable(context.getResources().getDrawable(CATEGORY_ICONS[id]));
						holder.categoryIcon1.setVisibility(View.VISIBLE);
					}
					else holder.categoryIcon1.setVisibility(View.GONE);
				}
				else holder.categoryIcon1.setVisibility(View.GONE);
				
				if(categories.size() > 1)
				{
					int id = mapCategoryToIcon(categories.get(1));
					if(id != -1)
					{
						holder.categoryIcon2.setImageDrawable(context.getResources().getDrawable(CATEGORY_ICONS[id]));
						holder.categoryIcon2.setVisibility(View.VISIBLE);
					}
					else holder.categoryIcon2.setVisibility(View.GONE);
				}
				else holder.categoryIcon2.setVisibility(View.GONE);
				
				if(categories.size() > 2)
				{
					int id = mapCategoryToIcon(categories.get(2));
					if(id != -1)
					{
						holder.categoryIcon3.setImageDrawable(context.getResources().getDrawable(CATEGORY_ICONS[id]));
						holder.categoryIcon3.setVisibility(View.VISIBLE);
					}
					else holder.categoryIcon3.setVisibility(View.GONE);
				}
				else holder.categoryIcon3.setVisibility(View.GONE);
			}
			else
			{
				holder.categoryIcon1.setVisibility(View.GONE);
				holder.categoryIcon2.setVisibility(View.GONE);
				holder.categoryIcon3.setVisibility(View.GONE);
			}
			
			if(user.hasPinnedEvent(e.getEventID()))
			{
				holder.pinButton.setImageDrawable(context.getResources().getDrawable(R.drawable.bookmark));
			}
			else
			{
				holder.pinButton.setImageDrawable(context.getResources().getDrawable(R.drawable.clear_bookmark));
			}	
			
			holder.pinButton.setOnClickListener(new View.OnClickListener()
			{
				@Override
				public void onClick(View v)
				{					
					ImageView iv = (ImageView) v;
										
					if(user.hasPinnedEvent(getItem(position).getEventID()))
					{
						iv.setImageDrawable(context.getResources().getDrawable(R.drawable.clear_bookmark));
						user.removeEvent(getItem(position).getEventID());
					}
					else
					{
						iv.setImageDrawable(context.getResources().getDrawable(R.drawable.bookmark));
						user.addEvent(getItem(position).getEventID());
					}
					
					SessionHandler.saveUserPreferences((Activity) context, user);
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
				holder.txtEventDate.setText(e.getLocation().getAddress1() + "\n"
						+ CalendarUtils.getEventDate(e));
			}
			
			int numberOfReviews = e.getNumberOfReviews();
			
			if (numberOfReviews > 0)
			{
				holder.numberOfReviewsDisplay.setText("based on " + numberOfReviews + " review" + ((numberOfReviews != 1) ? "s" : ""));
				int rating = e.getReviewScore();
				Bitmap emptyStar = BitmapFactory.decodeResource(
						((Activity) context).getResources(), R.drawable.star_grey_small);
				Bitmap halfStar = BitmapFactory.decodeResource(((Activity) context).getResources(),
						R.drawable.star_half_small);
				Bitmap fullStar = BitmapFactory.decodeResource(((Activity) context).getResources(),
						R.drawable.star_purple_small);
				
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
	
	private int mapCategoryToIcon(String category)
	{
		String[] categories =
				context.getResources().getStringArray(R.array.event_categories);
		
		for (int i = 0; i < categories.length; i++)
			if(categories[i].equals(category)) return i;
		
		return -1;
	}
	
	private static class ViewHolder
	{
		public TextView txtEventName;
		public TextView txtEventDescription;
		public TextView txtEventDate;
		public ImageView pinButton;
		
		public ImageView categoryIcon1;
		public ImageView categoryIcon2;
		public ImageView categoryIcon3;

		public ImageView star1;
		public ImageView star2;
		public ImageView star3;
		public ImageView star4;
		public ImageView star5;
		public TextView numberOfReviewsDisplay;
	}
}
