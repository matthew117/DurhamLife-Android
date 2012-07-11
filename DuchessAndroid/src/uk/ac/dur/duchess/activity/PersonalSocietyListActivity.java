package uk.ac.dur.duchess.activity;

import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import uk.ac.dur.duchess.R;
import uk.ac.dur.duchess.data.CalendarFunctions;
import uk.ac.dur.duchess.data.SessionFunctions;
import uk.ac.dur.duchess.entity.Event;
import uk.ac.dur.duchess.entity.EventXMLParser;
import uk.ac.dur.duchess.entity.User;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.ImageView;
import android.widget.TextView;

public class PersonalSocietyListActivity extends CustomTitleBarActivity
{
	private ExpandableListView exListView;
	private ExpandableListAdapter exAdapter;
	private Map<String, List<Event>> eventMap;
	private Activity activity;
	private List<String> societyList;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.personal_society_events_list);

		activity = this;
		
		exListView = (ExpandableListView) findViewById(R.id.expandableSocietyList);
		
		exListView.setOnGroupClickListener(new OnGroupClickListener()
		{
			@Override
			public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id)
			{
				
				try
				{
					SAXParserFactory factory = SAXParserFactory.newInstance();
					SAXParser parser = factory.newSAXParser();
					XMLReader reader = parser.getXMLReader();

					URL url = new URL("http://www.dur.ac.uk/cs.seg01/duchess/api/v1/events.php?societyName=" + URLEncoder.encode(societyList.get(groupPosition), "UTF-8"));
					
					Log.d("URL", url.toString());
					
					List<Event> eventList = new ArrayList<Event>();

					EventXMLParser myXMLHandler = new EventXMLParser(eventList);

					reader.setContentHandler(myXMLHandler);
					reader.parse(new InputSource(url.openStream()));
					
					eventMap.put(societyList.get(groupPosition), eventList);
					
					Log.d("EVENT MAP", eventMap.toString());
					
					Log.d("EVENT LIST", eventList.toString());
					
					GroupedEventListAdapter newAdapter = new GroupedEventListAdapter(societyList, eventMap);
					
					exListView.setAdapter(newAdapter);
					
				}
				catch (Exception e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				
				return false;
			}
		});
		
		User user = SessionFunctions.getCurrentUser(this);
		societyList = user.getSocieties();

		eventMap = new HashMap<String, List<Event>>();
		
		for (String society : societyList)
		{
			List<Event> eventList = new ArrayList<Event>();
			eventMap.put(society, eventList);
		}

		exAdapter = new GroupedEventListAdapter(societyList, eventMap);
		exListView.setAdapter(exAdapter);
	}

	public class GroupedEventListAdapter extends BaseExpandableListAdapter
	{
		private List<String> groupList;
		private Map<String, List<Event>> groupedEvents;

		public GroupedEventListAdapter(List<String> groupList, Map<String, List<Event>> groupedEvents)
		{
			this.groupList = groupList;
			this.groupedEvents = groupedEvents;
		}

		public Object getGroup(int groupPosition)
		{
			return groupList.get(groupPosition);
		}

		public int getGroupCount()
		{
			return groupList.size();
		}

		public long getGroupId(int groupPosition)
		{
			return groupPosition;
		}

		public View getGroupView(int groupPosition, boolean isExpanded, View convertView,
				ViewGroup parent)
		{
			AbsListView.LayoutParams lp = new AbsListView.LayoutParams(
					ViewGroup.LayoutParams.FILL_PARENT, 64);
			TextView textView = new TextView(PersonalSocietyListActivity.this);
			textView.setLayoutParams(lp);
			textView.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
			textView.setPadding(100, 0, 0, 0);
			textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
			textView.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
			textView.setText(getGroup(groupPosition).toString());
			return textView;
		}

		public Object getChild(int groupPosition, int childPosition)
		{
			return groupedEvents.get(groupList.get(groupPosition)).get(childPosition);
		}

		public long getChildId(int groupPosition, int childPosition)
		{
			return childPosition;
		}

		public int getChildrenCount(int groupPosition)
		{
			return groupedEvents.get(groupList.get(groupPosition)).size();
		}

		public View getChildView(int groupPosition, int childPosition, boolean isLastChild,
				View convertView, ViewGroup parent)
		{
			View v = convertView;
			ViewHolder holder;
			if (v == null)
			{
				LayoutInflater inflater = activity.getLayoutInflater();
				v = inflater.inflate(R.layout.custom_event_list_row, parent, false);
				
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

			Event e = (Event) getChild(groupPosition, childPosition);

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
							activity.getResources(), R.drawable.empty_star);
					Bitmap halfStar = BitmapFactory.decodeResource(activity.getResources(),
							R.drawable.half_star);
					Bitmap fullStar = BitmapFactory.decodeResource(activity.getResources(),
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

		public boolean isChildSelectable(int groupPosition, int childPosition)
		{
			return true;
		}

		public boolean hasStableIds()
		{
			return true;
		}
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