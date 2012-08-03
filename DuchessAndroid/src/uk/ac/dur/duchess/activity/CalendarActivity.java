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
import uk.ac.dur.duchess.data.DataProvider;
import uk.ac.dur.duchess.entity.Event;
import uk.ac.dur.duchess.entity.EventLocation;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.os.Bundle;
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
	
	private LinearLayout header;
	
	private TextView monthText;
	private ImageView prevMonthButton;
	private ImageView nextMonthButton;
	
	private LinearLayout calendarView;
	
	private List<Event> eventList;
	private EventListAdapter adapter;
	private ListView listView;
	
	private CalendarButton currentCell;
	
	private Context context;
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.calendar_layout);
		
		this.context = this;
		
		header = (LinearLayout) findViewById(R.id.calendarHeader);
		
		monthText = (TextView) findViewById(R.id.monthText);
		prevMonthButton = (ImageView) findViewById(R.id.prevMonthButton);
		nextMonthButton = (ImageView) findViewById(R.id.nextMonthButton);
		
		calendarView = (LinearLayout) findViewById(R.id.calendarView);
		
		listView = (ListView) findViewById(R.id.calendarListView);
		
		calendar = Calendar.getInstance();
		
		setMonthBounds(calendar.get(Calendar.MONTH));
		
		SimpleDateFormat month = new SimpleDateFormat("MMMMM yyyy");
		
		monthText.setText(month.format(calendar.getTime()));
		
		int[] colors = {Color.parseColor("#7E317B"), Color.parseColor("#D8ACE0")};
		
		GradientDrawable gradient = new GradientDrawable(Orientation.BOTTOM_TOP, colors);
		gradient.setDither(true);
		
		header.setBackgroundDrawable(gradient);
		
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
				EventLocation l = e.getLocation();
				i.putExtra("event_id", e.getEventID());
				i.putExtra("event_name", e.getName());
				i.putExtra("event_start_date", e.getStartDate());
				i.putExtra("event_end_date", e.getEndDate());
				i.putExtra("event_description_header", e.getDescriptionHeader());
				i.putExtra("event_description_body", e.getDescriptionBody());
				i.putExtra("event_contact_telephone_number", e.getContactTelephoneNumber());
				i.putExtra("event_contact_email_address", e.getContactEmailAddress());
				i.putExtra("event_web_address", e.getWebAddress());
				i.putExtra("event_address1", l.getAddress1());
				i.putExtra("event_address2", l.getAddress2());
				i.putExtra("event_city", l.getCity());
				i.putExtra("event_postcode", l.getPostcode());
				i.putExtra("event_latitude", l.getLatitude());
				i.putExtra("event_longitude", l.getLongitude());
				i.putExtra("image_url", e.getImageURL());
				i.putExtra("ical_url", e.getICalURL());
				startActivity(i);
			}
		});
		
		listView.setBackgroundDrawable(getResources().getDrawable(R.drawable.top_bottom_border));
		
		setupCalendarLayout();
		
		currentCell.setTextColor(Color.WHITE);
		currentCell.setBackgroundColor(Color.parseColor("#8560A8"));
		
		String fromDate = cellToDate(currentCell.cell);
		String toDate   = cellToDate(currentCell.cell + 1);
		
		filterEventByDateRange(fromDate, toDate);
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
				
				int left   = (col == 0) ? 6 : 3;
				int top    = (row == 0) ? 6 : 3;
				int right  = (col == 6) ? 6 : 3;
				int bottom = (row == 5) ? 6 : 3;
				
				cellParams.setMargins(left, top, right, bottom);
				
				tableRow.addView(getMonthButton(cell, col), cellParams);
				
				cell++;
			}
			
			calendarView.addView(tableRow);
		}

		adapter.clear();
		adapter.notifyDataSetChanged();
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
			 
			 Calendar.getInstance();		 
			 
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
		adapter.clear();
		GlobalApplicationData delegate = GlobalApplicationData.getInstance();
		DataProvider dataPro = delegate.getDataProvider();
		List<Event> events = dataPro.getAllEvents(context);

		for (Event event : events)
		{
			if (CalendarFunctions.inRange(event.getStartDate(), event.getEndDate(), fromDate,
					toDate)) adapter.add(event);
		}
		
		adapter.notifyDataSetChanged();
	}
	
	@Override
	public void onResume()
	{
		super.onResume();
		listView.setAdapter(listView.getAdapter());
	}
}
