package uk.ac.dur.duchess.util;

import static java.util.Calendar.*;

import java.io.IOException;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Scanner;

import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.Period;
import net.fortuna.ical4j.model.PeriodList;
import net.fortuna.ical4j.model.component.VEvent;
import android.util.Log;

public class TimeUtils
{
	private List<String> eventTimestamps;

	public TimeUtils(String iCalFileURL) throws IOException, ParserException
	{
		List<String> eventTimestamps = new ArrayList<String>();

		parseICalFromURL(iCalFileURL);

	}

	public static net.fortuna.ical4j.model.Calendar parseICalFromURL(String iCalFileURL) throws IOException, MalformedURLException,
			ParserException
	{
		StringBuilder sb = new StringBuilder();
		HttpURLConnection conn = (HttpURLConnection) (new URL(iCalFileURL)).openConnection();
		Scanner sc = new Scanner(conn.getInputStream());
		while (sc.hasNextLine())
		{
			sb.append(sc.nextLine() + "\r\n");
		}
		StringReader reader = new StringReader(sb.toString());

		net.fortuna.ical4j.data.CalendarBuilder builder = new CalendarBuilder();
		net.fortuna.ical4j.model.Calendar calendar = builder.build(reader);
		
		return calendar;
	}
	
	public static Calendar getClosestFutureWeek(Calendar c)
	{
		Calendar week = Calendar.getInstance(Locale.UK);
		
		if(week.get(WEEK_OF_YEAR) < c.get(WEEK_OF_YEAR) &&
				week.get(YEAR) <= c.get(YEAR)) week.setTime(c.getTime());
		/* the start date of the event is on a week later than the current week
		 * so set the time table to the first week of the event
		 */
		
		return week;
	}

	public static Calendar getMondayOfGivenWeek(Calendar c)
	{
		Calendar monday = Calendar.getInstance(Locale.UK);
		monday.setTime(c.getTime());

		for (; monday.get(DAY_OF_WEEK) != MONDAY; monday.add(DAY_OF_WEEK, -1));

		monday.set(HOUR_OF_DAY, 0);
		monday.set(MINUTE, 0);
		monday.set(SECOND, 0);
		monday.get(DATE);

		return monday;
	}

	public static Calendar getSundayOfGivenWeek(Calendar c)
	{
		Calendar sunday = getMondayOfGivenWeek(c);
		sunday.add(DAY_OF_WEEK, 6);

		return sunday;
	}

	public static List<String> getDatesBetween(Calendar start, Calendar end)
	{
		List<String> dateList = new ArrayList<String>();

		Calendar copyStart = Calendar.getInstance();
		copyStart.setTime(start.getTime());

		copyStart.set(HOUR_OF_DAY, 0);
		copyStart.set(MINUTE, 0);
		copyStart.set(SECOND, 0);
		copyStart.get(DATE);

		for (; !copyStart.after(end); copyStart.add(DAY_OF_YEAR, 1))
		{
			dateList.add((new SimpleDateFormat("yyyy-MM-dd")).format(copyStart.getTime()));
		}

		return dateList;
	}

	public static int dayOfTheWeekFromTimestamp(String timeStamp) throws ParseException
	{
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Log.d("TEST", sdf.parse(timeStamp).toGMTString());
		Calendar c = Calendar.getInstance(Locale.UK);
		c.setTime(sdf.parse(timeStamp));
		return c.get(DAY_OF_WEEK);
	}

	public static Map<Integer, List<String>> groupEventsByDay(List<String> timeStampList)
			throws ParseException
	{
		Map<Integer, List<String>> map = new HashMap<Integer, List<String>>();

		for (String timeStamp : timeStampList)
		{
			Log.d("TEST", timeStamp.substring(0,10));
			Integer dayOfWeek = dayOfTheWeekFromTimestamp(timeStamp.substring(0,10));
			if (!map.containsKey(dayOfWeek))
			{
				map.put(dayOfWeek, new ArrayList<String>(Arrays.asList(timeStamp)));
			}
			else
			{
				List<String> dayTimeStamps = map.get(dayOfWeek);
				dayTimeStamps.add(timeStamp);
			}
		}

		return map;
	}

	public static String getEarliestEvent(net.fortuna.ical4j.model.Calendar calendar, String eventName)
	{
		List<String> timeStamps = new ArrayList<String>();

		for (Object o : calendar.getComponents("VEVENT"))
		{
			VEvent vEvent = (VEvent) o;
			if (vEvent.getSummary().getValue().toString().equalsIgnoreCase(eventName))
			{
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				Calendar javaCal = Calendar.getInstance(Locale.UK);
				javaCal.setTime(vEvent.getStartDate().getDate());
				
				timeStamps.add(sdf.format(javaCal.getTime()));
			}
		}

		Collections.sort(timeStamps);
		return timeStamps.isEmpty() ? null : timeStamps.get(0);
	}

	public static List<String> getRecurrenceSetForGivenWeek(net.fortuna.ical4j.model.Calendar calendar, String eventName, String weekTimeStamp) throws ParseException
	{
		List<String> eventTimeStamps = new ArrayList<String>();
		
		Calendar week = Calendar.getInstance(Locale.UK);
		week.setTime(new SimpleDateFormat("yyyy-MM-dd").parse(weekTimeStamp));
		
		Calendar monday = getMondayOfGivenWeek(week);
		Calendar sunday = getSundayOfGivenWeek(week);
		sunday.add(DAY_OF_YEAR, 1);
		
		Log.d("SIZE", "Number of VEVENTS: " + calendar.getComponents("VEVENT").size());
		
		for (Object o : calendar.getComponents("VEVENT"))
		{
			VEvent vEvent = (VEvent) o;
			if (vEvent.getSummary().getValue().toString().equalsIgnoreCase(eventName))
			{				
				PeriodList recurrenceSet = vEvent.calculateRecurrenceSet(new Period(new DateTime(monday.getTime()), new DateTime(sunday.getTime())));
				
				Log.d("SIZE", "Size of RecurrenceSet: " + recurrenceSet.size());
				Log.d("SIZE", "RecurrenceSet: " + recurrenceSet.toString());
				
				for (Object obj : recurrenceSet)
				{
					Log.d("SIZE", "Hi");
					Period p = (Period) obj;
					
					DateTime start = p.getStart();
					DateTime end = p.getEnd();
					
					Calendar s = Calendar.getInstance(Locale.UK); s.setTime(start);
					Calendar e = Calendar.getInstance(Locale.UK); e.setTime(end);
					
					eventTimeStamps.add(String.format("%04d-%02d-%02d %02d:%02d:%02d/%02d:%02d:%02d",
							s.get(YEAR), s.get(MONTH) + 1, s.get(DATE), s.get(HOUR_OF_DAY), s.get(MINUTE), s.get(SECOND),
							e.get(HOUR_OF_DAY), e.get(MINUTE), e.get(SECOND)));
				}
			}
		}
		
		return eventTimeStamps;
	}

}
