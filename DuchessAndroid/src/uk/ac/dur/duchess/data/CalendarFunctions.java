package uk.ac.dur.duchess.data;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Hours;
import org.joda.time.LocalDate;
import org.joda.time.Minutes;
import org.joda.time.Seconds;
import org.joda.time.format.DateTimeFormat;

import uk.ac.dur.duchess.entity.Event;
import uk.ac.dur.duchess.entity.Review;

public class CalendarFunctions
{
	public static String getEventDate(Event event)
	{
		return getEventDate(event.getStartDate(), event.getEndDate());
	}
	
	public static String getEventDate(String start, String end)
	{
		try
		{
			SimpleDateFormat sourceFormat = new SimpleDateFormat("yyyy-MM-dd");
			SimpleDateFormat destinationFormat = new SimpleDateFormat("d MMMMM yyyy");
			SimpleDateFormat yearlessFormat = new SimpleDateFormat("d MMMMM");
			
			boolean isOneDayEvent = isSameDate(start, end);
			
			Calendar _now = Calendar.getInstance();
			String now = sourceFormat.format(_now.getTime());
			
			if (isSameDate(now, start) && isOneDayEvent) return "Today";
			if (compareByDay(now, start) == 1 && isOneDayEvent) return "Tomorrow";
			if (isSameDate(now, start) && compareByDay(now, end) == 1) return "Today & Tomorrow";
			
			String transition = (compareByDay(start, end) == 1) ? " & " : " until ";
			
			Calendar _start = Calendar.getInstance(); _start.setTime(sourceFormat.parse(start));
			Calendar _end   = Calendar.getInstance(); _end.setTime(sourceFormat.parse(end));
			
			boolean startsInThisYear = _now.get(Calendar.YEAR) == _start.get(Calendar.YEAR);
			boolean endsInThisYear = _now.get(Calendar.YEAR) == _end.get(Calendar.YEAR);
			
			String startDate, endDate;
			
			if(startsInThisYear && endsInThisYear)
			{
				startDate = yearlessFormat.format(_start.getTime());	
				endDate   = yearlessFormat.format(_end.getTime());
			}
			else
			{
				startDate = destinationFormat.format(_start.getTime());	
				endDate   = destinationFormat.format(_end.getTime());
			}
			
			if (compareByDay(now, start) > 1 && isOneDayEvent) return startDate;
			if (compareByDay(now, end) < 0) return "This event has ended";
						
			return startDate + transition + endDate;
		}
		catch (ParseException e1)
		{
			return "Data Unavailable";
		}
	}
	
	public static boolean inRange(String start, String end, String from, String to)
	{
		SimpleDateFormat range = new SimpleDateFormat("d MMMMM yyyy");
		SimpleDateFormat event = new SimpleDateFormat("yyyy-MM-dd");
		
		try
		{
			Calendar fromDate  = Calendar.getInstance();
			if(from.matches("\\d{4}-\\d{1,2}-\\d{1,2}")) fromDate.setTime(event.parse(from));
			else fromDate.setTime(range.parse(from));
			
			Calendar toDate    = Calendar.getInstance();
			if(to.matches("\\d{4}-\\d{1,2}-\\d{1,2}")) toDate.setTime(event.parse(to));
			else toDate.setTime(range.parse(to));

			
			Calendar startDate = Calendar.getInstance(); startDate.setTime(event.parse(start));
			Calendar endDate   = Calendar.getInstance(); endDate.setTime(event.parse(end));
			
			//the event must start before the 'until' date and end after (or on the same date as) the 'from' date 
			return (startDate.compareTo(toDate) < 0) && (endDate.compareTo(fromDate) >= 0);
		}
		catch (ParseException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}
	
	public static boolean isSameDate(Calendar a, Calendar b)
	{
		return a.get(Calendar.YEAR) == b.get(Calendar.YEAR)
				&& a.get(Calendar.DAY_OF_YEAR) == b.get(Calendar.DAY_OF_YEAR);
	}
	
	public static int compareByDay(String from, String to)
	{
		LocalDate a = LocalDate.parse(from, DateTimeFormat.forPattern("yyyy-MM-dd"));
		LocalDate b = LocalDate.parse(to, DateTimeFormat.forPattern("yyyy-MM-dd"));
		
		return Days.daysBetween(a, b).getDays();
	}
	
	public static boolean isSameDate(String a, String b)
	{
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		
		try
		{
			Calendar date1 = Calendar.getInstance(); date1.setTime(sdf.parse(a));
			Calendar date2 = Calendar.getInstance(); date2.setTime(sdf.parse(b));
			
			return isSameDate(date1, date2);
		}
		catch (ParseException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}
	
	public static int compareDates(String d1, String d2)
	{
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		
		try
		{
			Calendar date1 = Calendar.getInstance(); date1.setTime(sdf.parse(d1));
			Calendar date2 = Calendar.getInstance(); date2.setTime(sdf.parse(d2));
			
			return date1.compareTo(date2);
		}
		catch (ParseException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			return 0;
		}
	}
	
	public static String getReviewTime(Review review)
	{
		return getReviewTime(review.getTimestamp());
	}
	
	public static String getReviewTime(String timeStamp)
	{
			DateTime now = new DateTime();
			DateTime reviewTime = DateTime.parse(timeStamp, DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss"));
			
			int days = Days.daysBetween(reviewTime, now).getDays();
			int hours = Hours.hoursBetween(reviewTime, now).getHours();
			int minutes = Minutes.minutesBetween(reviewTime, now).getMinutes();
			long seconds = Seconds.secondsBetween(reviewTime, now).getSeconds();
						
			if(days == 0 && hours == 0 && (minutes == 0 || seconds < 60)) return "Less than 1 minute ago";
			else if(days == 0 && hours == 0) return minutes + " minute" + ((minutes != 1) ? "s" : "") + " ago";
			else if(days == 0 && hours == 1 && minutes < 0) return 60 + minutes + " minute" + ((minutes != 1) ? "s" : "") + " ago";
			else if(days == 0) return hours + " hour" + ((hours != 1) ? "s" : "") + " ago";
			else if(days == 1 && hours < 0) return 24 + hours + " hour" + ((hours != 1) ? "s" : "") + " ago";

			return reviewTime.toString(DateTimeFormat.forPattern("d MMMMM yyyy"));
	}
}
