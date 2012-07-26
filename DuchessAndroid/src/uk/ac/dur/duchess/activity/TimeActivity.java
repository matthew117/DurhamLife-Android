package uk.ac.dur.duchess.activity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.fortuna.ical4j.model.Calendar;
import uk.ac.dur.duchess.R;
import uk.ac.dur.duchess.TimeAdapter;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import edu.emory.mathcs.backport.java.util.Collections;

public class TimeActivity extends Activity
{	
	private TextView eventNameTextView;
	private TextView dateRangeTextView;
	
	private TextView mondayTextView;
	private TextView tuesdayTextView;
	private TextView wednesdayTextView;
	private TextView thursdayTextView;
	private TextView fridayTextView;
	private TextView saturdayTextView;
	private TextView sundayTextView;
	
	private LinearLayout mondayContainer;
	private LinearLayout tuesdayContainer;
	private LinearLayout wednesdayContainer;
	private LinearLayout thursdayContainer;
	private LinearLayout fridayContainer;
	private LinearLayout saturdayContainer;
	private LinearLayout sundayContainer;
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.time_chooser_layout);
		
		eventNameTextView = (TextView) findViewById(R.id.textViewEventName);
		dateRangeTextView = (TextView) findViewById(R.id.timeViewDateRange);
		
		mondayTextView    = (TextView) findViewById(R.id.mondayTextView);
		tuesdayTextView   = (TextView) findViewById(R.id.tuesdayTextView);
		wednesdayTextView = (TextView) findViewById(R.id.wednesdayTextView);
		thursdayTextView  = (TextView) findViewById(R.id.thursdayTextView);
		fridayTextView    = (TextView) findViewById(R.id.fridayTextView);
		saturdayTextView  = (TextView) findViewById(R.id.saturdayTextView);
		sundayTextView    = (TextView) findViewById(R.id.sundayTextView);
		
		mondayContainer    = (LinearLayout) findViewById(R.id.mondayDateContainer);
		tuesdayContainer   = (LinearLayout) findViewById(R.id.tuesdayDateContainer);
		wednesdayContainer = (LinearLayout) findViewById(R.id.wednesdayDateContainer);
		thursdayContainer  = (LinearLayout) findViewById(R.id.thursdayDateContainer);
		fridayContainer    = (LinearLayout) findViewById(R.id.fridayDateContainer);
		saturdayContainer  = (LinearLayout) findViewById(R.id.saturdayDateContainer);
		sundayContainer    = (LinearLayout) findViewById(R.id.sundayDateContainer);
		
		LinearLayout[] containers = {sundayContainer, mondayContainer, tuesdayContainer,
				wednesdayContainer, thursdayContainer, fridayContainer, saturdayContainer};
		
		try
		{
			Bundle e = getIntent().getExtras();
			final String eventName = e.getString("event_name");
			final String eventLocation = e.getString("event_address");
			String iCalURL = e.getString("ical_url");
			String startDate = e.getString("event_start_date");
			
			eventNameTextView.setText(eventName);
			
			Calendar c = TimeAdapter.parseICalFromURL(iCalURL);
			java.util.Calendar week = java.util.Calendar.getInstance();
			
			week.setTime((new SimpleDateFormat("yyyy-MM-dd")).parse(startDate));
			week = TimeAdapter.getClosestFutureWeek(week);
			
			java.util.Calendar monday = TimeAdapter.getMondayOfGivenWeek(week);
			java.util.Calendar sunday = TimeAdapter.getSundayOfGivenWeek(week);
			
			dateRangeTextView.setText(       (new SimpleDateFormat("d MMMMMM")).format(monday.getTime()));
			dateRangeTextView.append(" - " + (new SimpleDateFormat("d MMMMMM")).format(sunday.getTime()));
			
			List<String> unformatted = TimeAdapter.getDatesBetween(monday, sunday);
			
			List<String> leftDaytext = new ArrayList<String>();
			
			for (String dayText : unformatted)
			{
				java.util.Calendar f = java.util.Calendar.getInstance();
				f.setTime((new SimpleDateFormat("yyyy-MM-dd")).parse(dayText));
				leftDaytext.add((new SimpleDateFormat("E dd")).format(f.getTime()));
			}

			mondayTextView.setText(leftDaytext.get(0));
			tuesdayTextView.setText(leftDaytext.get(1));
			wednesdayTextView.setText(leftDaytext.get(2));
			thursdayTextView.setText(leftDaytext.get(3));
			fridayTextView.setText(leftDaytext.get(4));
			saturdayTextView.setText(leftDaytext.get(5));
			sundayTextView.setText(leftDaytext.get(6));
			
			Map<Integer,List<String>> map = TimeAdapter.groupEventsByDay(
					TimeAdapter.getRecurrenceSetForGivenWeek(c, eventName,
							(new SimpleDateFormat("yyyy-MM-dd")).format(week.getTime())));
			
			for (Integer i : map.keySet())
			{
				List<String> tempList = map.get(i);
				if (tempList.size() > 1) Collections.sort(tempList);
			}
			
			String value = android.provider.Settings.System.getString
					(this.getContentResolver(), android.provider.Settings.System.TIME_12_24);
			
			String format12 = "hh:mm a";
			String format24 = "HH:mm";
			
			boolean usesAM_PM = value.equals("12");
			
			SimpleDateFormat source = new SimpleDateFormat(format24);
			SimpleDateFormat timeFormat = new SimpleDateFormat(usesAM_PM ? format12 : format24);
			
			final SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
			
			for (Integer day : map.keySet())
			{
				List<String> buttonText = map.get(day);
				if (buttonText != null)
				{
					for (final String s : buttonText)
					{
						Button b = new Button(this);
						
						String startTime = timeFormat.format(source.parse(s.substring(11, 16)));
						String endTime   = timeFormat.format(source.parse(s.substring(20, 26)));
						
						b.setText(startTime + " - " + endTime);
						
						b.setOnClickListener(new View.OnClickListener()
						{
							@Override
							public void onClick(View v)
							{
								try
								{
									java.util.Calendar start = java.util.Calendar.getInstance(); 
									java.util.Calendar end   = java.util.Calendar.getInstance(); 
									
									start.setTime(new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(s.substring(0, 10) + " " + s.substring(11, 16)));
									end.setTime  (new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(s.substring(0, 10) + " " + s.substring(20, 26)));
									
									Intent intent = new Intent(Intent.ACTION_EDIT);
									
									intent.setType("vnd.android.cursor.item/event");
									intent.putExtra("beginTime", start.getTimeInMillis());
									intent.putExtra("endTime", end.getTimeInMillis());
									intent.putExtra("title", eventName);
									intent.putExtra("eventLocation", eventLocation);
									
									startActivity(intent);
								}
								catch(ParseException e) { e.printStackTrace(); }
							}
						});
						
						containers[day - 1].addView(b);
					}
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
	}
}
