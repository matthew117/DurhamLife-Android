package uk.ac.dur.duchess.activity;

import static android.view.ViewGroup.LayoutParams.FILL_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import uk.ac.dur.duchess.EventListAdapter;
import uk.ac.dur.duchess.GlobalApplicationData;
import uk.ac.dur.duchess.R;
import uk.ac.dur.duchess.data.CalendarFunctions;
import uk.ac.dur.duchess.entity.Event;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.Surface;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.TextView;

public class CalendarActivity extends Activity
{
	private Calendar calendar;
	private int lowerBound;
	private int upperBound;
	private int lastBound;
	private int currentDay = -1;
	
	private LinearLayout layout;
	private LayoutParams params;
	private LinearLayout header;
	private LinearLayout subHeader;
	private LinearLayout calendarView;
	
	private CalendarButton currentCell;
	
	private ImageView prevMonthButton;
	private ImageView nextMonthButton;
	private List<Event> eventList;
	private EventListAdapter adapter;
	private ListView listView;
	private TextView monthText;
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		layout = new LinearLayout(this);
		params = new LayoutParams(FILL_PARENT, WRAP_CONTENT, 0);
		
		Display display = ((android.view.WindowManager) getSystemService(WINDOW_SERVICE)).getDefaultDisplay();
		int screenOrientation = display.getOrientation();
		
		if(screenOrientation == Surface.ROTATION_90 ||
		   screenOrientation == Surface.ROTATION_270) params = new LayoutParams(FILL_PARENT, FILL_PARENT, 1);
		
		layout.setLayoutParams(params);
		layout.setBackgroundColor(Color.parseColor("#DDDDDD"));
		
		calendarView = new LinearLayout(this);
		calendarView.setOrientation(LinearLayout.VERTICAL);
		
		calendar = Calendar.getInstance();
		
		setMonthBounds(calendar.get(Calendar.MONTH));
		
		header = new LinearLayout(this);
		LayoutParams headerParams = new LayoutParams(FILL_PARENT, WRAP_CONTENT, 0);
		headerParams.setMargins(0, 0, 0, 3);
		header.setLayoutParams(headerParams);
		header.setOrientation(LinearLayout.VERTICAL);
		
		subHeader = new LinearLayout(this);
		subHeader.setLayoutParams(new LayoutParams(FILL_PARENT, WRAP_CONTENT, 1));
		
		monthText = new TextView(this);
		
		SimpleDateFormat month = new SimpleDateFormat("MMMMM yyyy");
		
		monthText.setText(month.format(calendar.getTime()));
		monthText.setTextColor(Color.BLACK);
		monthText.setTextSize(15);
		monthText.setTypeface(Typeface.SERIF, Typeface.BOLD);
		monthText.setPadding(5, 5, 5, 5);
		monthText.setGravity(Gravity.CENTER);
		
		int[] colors = {Color.parseColor("#7E317B"), Color.parseColor("#D8ACE0")};
		
		GradientDrawable gradient = new GradientDrawable(Orientation.BOTTOM_TOP, colors);
		gradient.setDither(true);
		
		header.setBackgroundDrawable(gradient);
		
		prevMonthButton = new ImageView(this);
		prevMonthButton.setClickable(true);
		prevMonthButton.setImageDrawable(getResources().getDrawable(R.drawable.month_backward));
		
		nextMonthButton = new ImageView(this);
		nextMonthButton.setClickable(true);
		nextMonthButton.setImageDrawable(getResources().getDrawable(R.drawable.month_forward));
		
		LayoutParams buttonParams = new LayoutParams(WRAP_CONTENT, WRAP_CONTENT, 0);
		buttonParams.setMargins(0, 5, 0, 5);
		
		prevMonthButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				calendar.add(Calendar.MONTH, -1);
				setMonthBounds(calendar.get(Calendar.MONTH));
				
				SimpleDateFormat month = new SimpleDateFormat("MMMMM yyyy");
				
				monthText.setText(month.format(calendar.getTime()));
				
				setupCalendarLayout();
			}
		});
		
		nextMonthButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				calendar.add(Calendar.MONTH, 1);
				setMonthBounds(calendar.get(Calendar.MONTH));
				
				SimpleDateFormat month = new SimpleDateFormat("MMMMM yyyy");
				
				monthText.setText(month.format(calendar.getTime()));
				
				setupCalendarLayout();
			}
		});
		
		subHeader.addView(prevMonthButton, buttonParams);
		subHeader.addView(monthText, new LayoutParams(WRAP_CONTENT, WRAP_CONTENT, 1));
		subHeader.addView(nextMonthButton, buttonParams);
		
		header.addView(subHeader);
		
		LinearLayout days = new LinearLayout(this);
		days.setLayoutParams(new LayoutParams(FILL_PARENT, WRAP_CONTENT, 0));
		
		String[] dayStrings = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
		
		for(int i = 0; i < 7; i++)
		{
			TextView day = new TextView(this);
			day.setText(dayStrings[i]);
			day.setGravity(Gravity.CENTER);
			day.setPadding(0, 0, 0, 5);
			day.setTextColor(Color.BLACK);
			day.setTextSize(14);
			days.addView(day, new LayoutParams(0, WRAP_CONTENT, 1));
		}
		
		header.addView(days);
		
		layout.addView(header);
		
		setupCalendarLayout();
		
		layout.addView(calendarView, params);
		
		listView = new ListView(this);
		
		eventList = new ArrayList<Event>();

		adapter = new EventListAdapter(this, R.layout.custom_event_list_row, eventList);
		listView.setAdapter(adapter);
		
		listView.setOnItemClickListener(new OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id)
			{

				Intent i = new Intent(view.getContext(), EventDetailsTabRootActivity.class);
				Event e = (Event) adapter.getItem(position);
				i.putExtra("event_id", e.getEventID());
				i.putExtra("event_name", e.getName());
				i.putExtra("event_start_date", e.getStartDate());
				i.putExtra("event_end_date", e.getEndDate());
				i.putExtra("event_description_header", e.getDescriptionHeader());
				i.putExtra("event_description_body", e.getDescriptionBody());
				i.putExtra("event_contact_telephone_number", e.getContactTelephoneNumber());
				i.putExtra("event_contact_email_address", e.getContactEmailAddress());
				i.putExtra("event_web_address", e.getWebAddress());
				i.putExtra("event_address1", e.getAddress1());
				i.putExtra("event_address2", e.getAddress2());
				i.putExtra("event_city", e.getCity());
				i.putExtra("event_postcode", e.getPostcode());
				i.putExtra("event_latitude", e.getLatitude());
				i.putExtra("event_longitude", e.getLongitude());
				i.putExtra("image_url", e.getImageURL());
				i.putExtra("ical_url", e.getICalURL());
				startActivity(i);
			}
		});
		
		listView.setBackgroundDrawable(getResources().getDrawable(R.drawable.top_bottom_border));
		
		layout.addView(listView, new LayoutParams(FILL_PARENT, FILL_PARENT, 0));
		
		layout.setOrientation(LinearLayout.VERTICAL);
		
		setContentView(layout);
	}

	private void setupCalendarLayout()
	{
		calendarView.removeAllViews();
		
		int cell = 1 - lowerBound;
		
		for(int row = 0; row < 6; row++)
		{
			LinearLayout tableRow = new LinearLayout(this);
			tableRow.setLayoutParams(new LayoutParams(FILL_PARENT, WRAP_CONTENT, 0));
			
			for(int col = 0; col < 7; col++)
			{
				LayoutParams cellParams = new LayoutParams(0, WRAP_CONTENT, 1);
				
				int bottom = (row == 5) ? 6 : 3;
				
				if(col == 0) cellParams.setMargins(6, 3, 3, bottom);
				else if(col == 6) cellParams.setMargins(3, 3, 6, bottom);
				else cellParams.setMargins(3, 3, 3, bottom);
				
				tableRow.addView(getMonthButton(cell, col), cellParams);
				
				cell++;
			}
			
			calendarView.addView(tableRow);
		}
	}
	
	private Button getMonthButton(int cell, int col)
	{
		int day = cell;
		int color = Color.BLACK;
		
		if(col == 5) color = Color.parseColor("#0076A3");
		else if(col == 6) color = Color.parseColor("#ED1C24");
		
		if(day < 1)
		{
			day += lastBound;
			color = Color.GRAY;
		}
		else if(day > upperBound)
		{
			day %= upperBound;
			color = Color.GRAY;
		}
		
		CalendarButton button = new CalendarButton(this, cell, color);
		
		button.setText(String.valueOf(day));
		button.setTextSize(16);
		button.setPadding(5, 15, 5, 15);
		button.setTextColor(color);
		button.setBackgroundColor(Color.WHITE);
		
		button.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				CalendarButton b = (CalendarButton) v;
			
				currentCell.setTextColor(currentCell.color);
				currentCell.setBackgroundColor(Color.WHITE);

				b.setTextColor(Color.WHITE);
				v.setBackgroundColor(Color.parseColor("#8560A8"));
				currentCell = b;
				
				String fromDate = cellToDate(b.cell);
				String toDate   = cellToDate(b.cell + 1);
				
				filterEventByDateRange(fromDate, toDate);
			}
			
		});

		return button;
	}
	
	private class CalendarButton extends Button
	{
		private int cell;
		private int color;
		
		public CalendarButton(Context context, int cell, int color)
		{
			super(context);
			this.cell = cell;
			this.color = color;
			
			if(cell == currentDay) currentCell = this;
		}
		
		 @Override
		 protected void onDraw(Canvas canvas)
		 {
			 super.onDraw(canvas);
			 
			 Calendar cal = Calendar.getInstance();		 
			 
			 if(cell == currentDay && currentDay != -1)
			 {
				 float w = getWidth()  / 2;
				 float h = getHeight() / 2;
				 
				 Bitmap bitmap =
						 BitmapFactory.decodeResource(getResources(), R.drawable.today_indicator);
				 
				 w -= bitmap.getWidth()  / 2;
				 h -= bitmap.getHeight() / 2;
				 
			     canvas.drawBitmap(bitmap, w, h, null);
			 }
		 }
	}
	
	private void setMonthBounds(int month)
	{
		Calendar cal = Calendar.getInstance();
		
		if(month == cal.get(Calendar.MONTH) &&
		   calendar.get(Calendar.YEAR) == cal.get(Calendar.YEAR))
			currentDay = cal.get(Calendar.DAY_OF_MONTH);
		else currentDay = -1;
		
		cal.set(Calendar.YEAR, calendar.get(Calendar.YEAR));
		cal.set(Calendar.MONTH, month);
		cal.set(Calendar.DAY_OF_MONTH, 1);
		
		lowerBound = (cal.get(Calendar.DAY_OF_WEEK) + 5) % 7;
		upperBound = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
		
		cal.set(Calendar.MONTH, month - 1);
		
		lastBound = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
	}
	
	private String cellToDate(int cell)
	{
		SimpleDateFormat range = new SimpleDateFormat("yyyy-MM-dd");
		
		Calendar cal = Calendar.getInstance();
		
		cal.set(Calendar.YEAR, calendar.get(Calendar.YEAR));
		cal.set(Calendar.MONTH, calendar.get(Calendar.MONTH));
		cal.set(Calendar.DAY_OF_MONTH, cell);
		
		return range.format(cal.getTime());

	}
	
	private void filterEventByDateRange(String fromDate, String toDate)
	{
		eventList.clear();
		List<Event> events = GlobalApplicationData.globalEventList;

		for (Event event : events)
		{
			if (CalendarFunctions.inRange(event.getStartDate(), event.getEndDate(), fromDate,
					toDate)) eventList.add(event);
		}

		listView.setAdapter(new EventListAdapter(this, R.layout.custom_event_list_row,
				eventList));
		
		adapter.notifyDataSetChanged();
	}
}
