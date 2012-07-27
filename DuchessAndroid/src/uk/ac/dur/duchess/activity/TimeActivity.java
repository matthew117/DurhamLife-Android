package uk.ac.dur.duchess.activity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.fortuna.ical4j.model.Calendar;
import uk.ac.dur.duchess.FlowLayout;
import uk.ac.dur.duchess.R;
import uk.ac.dur.duchess.TimeAdapter;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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
	
	private FlowLayout mondayContainer;
	private FlowLayout tuesdayContainer;
	private FlowLayout wednesdayContainer;
	private FlowLayout thursdayContainer;
	private FlowLayout fridayContainer;
	private FlowLayout saturdayContainer;
	private FlowLayout sundayContainer;
	
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
		
		mondayContainer    = (FlowLayout) findViewById(R.id.mondayDateContainer);
		tuesdayContainer   = (FlowLayout) findViewById(R.id.tuesdayDateContainer);
		wednesdayContainer = (FlowLayout) findViewById(R.id.wednesdayDateContainer);
		thursdayContainer  = (FlowLayout) findViewById(R.id.thursdayDateContainer);
		fridayContainer    = (FlowLayout) findViewById(R.id.fridayDateContainer);
		saturdayContainer  = (FlowLayout) findViewById(R.id.saturdayDateContainer);
		sundayContainer    = (FlowLayout) findViewById(R.id.sundayDateContainer);
		
		FlowLayout[] containers = {sundayContainer, mondayContainer, tuesdayContainer,
				wednesdayContainer, thursdayContainer, fridayContainer, saturdayContainer};
		
		TextView[] dayTextViews = {mondayTextView, tuesdayTextView, wednesdayTextView,
				thursdayTextView, fridayTextView, saturdayTextView, sundayTextView};
		
		try
		{
			Bundle e = getIntent().getExtras();
			final String eventName = e.getString("event_name");
			final String eventLocation = e.getString("event_address");
			String iCalURL = e.getString("ical_url");
			String startDate = e.getString("event_start_date");
			
			eventNameTextView.setText(eventName);
			
			int[] headerColors = {Color.parseColor("#67226D"), Color.parseColor("#D8ACE0")};
			
			GradientDrawable headerGradient = new GradientDrawable(Orientation.BOTTOM_TOP, headerColors);
			headerGradient.setDither(true);
			
			findViewById(R.id.weekHeader).setBackgroundDrawable(headerGradient);
			
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
				leftDaytext.add((new SimpleDateFormat("EEEEE, dd MMMMM yyyy")).format(f.getTime()));
			}
			
			int[] subHeaderColors = {Color.parseColor("#BBBBBB"), Color.parseColor("#DDDDDD"), Color.parseColor("#FFFFFF")};
			
			GradientDrawable subHeaderGradient = new GradientDrawable(Orientation.BOTTOM_TOP, subHeaderColors);
			subHeaderGradient.setDither(true);

			for(int i = 0; i < leftDaytext.size(); i++)
			{
				dayTextViews[i].setText(leftDaytext.get(i));
				dayTextViews[i].setBackgroundDrawable(subHeaderGradient);
			}
			
			findViewById(R.id.footerView).setBackgroundDrawable(subHeaderGradient);
			
			int[] containerColors = {Color.parseColor("#444444"), Color.parseColor("#333333"), Color.parseColor("#222222")};
			
			GradientDrawable containerGradient = new GradientDrawable(Orientation.BOTTOM_TOP, containerColors);
			containerGradient.setDither(true);
			
			for(FlowLayout layout : containers) layout.setBackgroundDrawable(containerGradient);
			
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
						b.setBackgroundDrawable(getResources().getDrawable(R.drawable.time_button));
						b.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.plus, 0);
						b.setCompoundDrawablePadding(5);
						
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
			
			for(int i = 0; i < containers.length; i++)
				if(containers[i].getChildCount() == 0)
				{
					containers[i].setVisibility(View.GONE);
					dayTextViews[(containers.length + i - 1) % containers.length].setVisibility(View.GONE);
				}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
	}
}
