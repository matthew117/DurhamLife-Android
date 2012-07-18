package uk.ac.dur.duchess.activity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import edu.emory.mathcs.backport.java.util.Collections;

import net.fortuna.ical4j.model.Calendar;
import uk.ac.dur.duchess.R;
import uk.ac.dur.duchess.TimeAdapter;
import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class TimeActivity extends Activity
{	
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
			Calendar c = TimeAdapter.parseICalFromURL("https://www.google.com/calendar/ical/vdcap3h4ubaatdhlumo99jecq8%40group.calendar.google.com/private-017b6bfe68db828e8051d8051746deaf/basic.ics");
			java.util.Calendar thisDay = java.util.Calendar.getInstance();
			
			java.util.Calendar monday = TimeAdapter.getMondayOfGivenWeek(thisDay);
			java.util.Calendar sunday = TimeAdapter.getSundayOfGivenWeek(thisDay);
			
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
					TimeAdapter.getRecurrenceSetForGivenWeek(c, "College Brunch",
							(new SimpleDateFormat("yyyy-MM-dd")).format(thisDay.getTime())));
			
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
			
			for (Integer day : map.keySet())
			{
				List<String> buttonText = map.get(day);
				if (buttonText != null)
				{
					for (String s : buttonText)
					{
						Button b = new Button(this);
						
						String startTime = s.substring(11, 16);
						String endTime   = s.substring(20, 26);
						
						startTime = timeFormat.format(source.parse(startTime));
						endTime   = timeFormat.format(source.parse(endTime));
						
						b.setText(startTime + " - " + endTime);
						
						containers[day - 1].addView(b);
					}
				}
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
		
	}
}
