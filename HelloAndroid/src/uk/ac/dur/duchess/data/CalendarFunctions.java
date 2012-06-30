package uk.ac.dur.duchess.data;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

import uk.ac.dur.duchess.Event;
import uk.ac.dur.duchess.Review;

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
			
			Calendar startDate = Calendar.getInstance(); startDate.setTime(sourceFormat.parse(start));
			Calendar endDate   = Calendar.getInstance(); endDate.setTime(sourceFormat.parse(end));
			
			String _startDate = destinationFormat.format(startDate.getTime());
			String _endDate   = destinationFormat.format(endDate.getTime());
			
			Calendar today     = Calendar.getInstance();
			Calendar tomorrow  = Calendar.getInstance(); tomorrow.roll(Calendar.DAY_OF_YEAR, true);
			Calendar yesterday = Calendar.getInstance(); yesterday.roll(Calendar.DAY_OF_YEAR, false);
						
			if(startDate.get(Calendar.MONTH) == today.get(Calendar.MONTH))
			{
				if(startDate.get(Calendar.DATE)     == today.get(Calendar.DATE)) _startDate = "Today";
				if(startDate.get(Calendar.DATE) - 1 == today.get(Calendar.DATE)) _startDate = "Tomorrow";
				if(startDate.get(Calendar.DATE) + 1 == today.get(Calendar.DATE)) _startDate = "Yesterday";
			}
			
			if(endDate.get(Calendar.MONTH) == today.get(Calendar.MONTH))
			{
				if(endDate.get(Calendar.DATE)     == today.get(Calendar.DATE)) _endDate = "Today";
				if(endDate.get(Calendar.DATE) - 1 == today.get(Calendar.DATE)) _endDate = "Tomorrow";
				if(endDate.get(Calendar.DATE) + 1 == today.get(Calendar.DATE)) _endDate = "Yesterday";
			}
			
			if(isSameDate(startDate, tomorrow)) _startDate = "Tomorrow";
			if(isSameDate(endDate, tomorrow)) _endDate = "Tomorrow";
			if(isSameDate(startDate, yesterday)) _startDate = "Yesterday";
			if(endDate.before(today))
			{
				_startDate = "This event has ended";
				_endDate = "";
			}
			
			if(isSameDate(startDate, endDate)) _endDate = "";
			
			return _startDate + ((_endDate.equals("")) ? "" : " until " + _endDate);
		}
		catch (ParseException e1)
		{
			return "Data Unavailable";
		}
	}
	
	public static boolean isSameDate(Calendar a, Calendar b)
	{
		return a.get(Calendar.YEAR) == b.get(Calendar.YEAR)
				&& a.get(Calendar.DAY_OF_YEAR) == b.get(Calendar.DAY_OF_YEAR);
	}
	
	public static String getReviewTime(Review review)
	{
		return getReviewTime(review.getTimestamp());
	}
	
	public static String getReviewTime(String timeStamp)
	{
		try
		{
			SimpleDateFormat sourceFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			SimpleDateFormat destinationFormat = new SimpleDateFormat("d MMMMM yyyy");

			Calendar today = Calendar.getInstance();
			Calendar _timeStamp = Calendar.getInstance();
			_timeStamp.setTime(sourceFormat.parse(timeStamp));

			int days = today.get(Calendar.DATE) - _timeStamp.get(Calendar.DATE); 
			int hours = today.get(Calendar.HOUR_OF_DAY) - _timeStamp.get(Calendar.HOUR_OF_DAY);  
			int minutes = today.get(Calendar.MINUTE) - _timeStamp.get(Calendar.MINUTE);

			System.out.println("Days: " + days + ", Hours: " + hours + ", Minutes: " + minutes);

			if(days == 0 && hours == 0 && minutes == 0) return "Less than 1 minute ago";
			else if(days == 0 && hours == 0) return minutes + " minute" + ((minutes != 1) ? "s" : "") + " ago";
			else if(days == 0 && hours == 1 && minutes < 0) return 60 + minutes + " minute" + ((minutes != 1) ? "s" : "") + " ago";
			else if(days == 0) return hours + " hour" + ((hours != 1) ? "s" : "") + " ago";
			else if(days == 1 && hours < 0) return 24 + hours + " hour" + ((hours != 1) ? "s" : "") + " ago";

			return destinationFormat.format(_timeStamp.getTime());
		}
		catch(ParseException pe) { return "Time of Post Unavailable"; }
	}
}
