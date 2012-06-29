package uk.ac.dur.duchess.data;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Calendar;

import uk.ac.dur.duchess.Event;
import uk.ac.dur.duchess.Review;

public class CalendarFunctions
{
	public static String getEventDate(Event e)
	{
		try
		{
			SimpleDateFormat sourceFormat = new SimpleDateFormat("yyyy-MM-dd");
			SimpleDateFormat destinationFormat = new SimpleDateFormat("d MMMMM yyyy");
			
			Date startDate = sourceFormat.parse(e.getStartDate());
			Date endDate   = sourceFormat.parse(e.getEndDate());
			
			String _startDate = destinationFormat.format(startDate);
			String _endDate   = destinationFormat.format(endDate);
			
			Calendar today     = Calendar.getInstance();
			Calendar tomorrow  = Calendar.getInstance(); tomorrow.roll(Calendar.DATE, true);
			Calendar yesterday = Calendar.getInstance(); tomorrow.roll(Calendar.DATE, false);
			
			Date _today = today.getTime();
			Date _tomorrow = tomorrow.getTime();
			Date _yesterday = yesterday.getTime();
						
			if(startDate.getMonth() == _today.getMonth())
			{
				if(startDate.getDate()     == _today.getDate()) _startDate = "Today";
				if(startDate.getDate() - 1 == _today.getDate()) _startDate = "Tomorrow";
				if(startDate.getDate() + 1 == _today.getDate()) _startDate = "Yesterday";
			}
			
			if(endDate.getMonth() == _today.getMonth())
			{
				if(endDate.getDate()     == _today.getDate()) _endDate = "Today";
				if(endDate.getDate() - 1 == _today.getDate()) _endDate = "Tomorrow";
				if(endDate.getDate() + 1 == _today.getDate()) _endDate = "Yesterday";
			}
			
			if(isSameDate(startDate, _tomorrow)) _startDate = "Tomorrow";
			if(isSameDate(endDate, _tomorrow)) _endDate = "Tomorrow";
			if(isSameDate(startDate, _yesterday)) _startDate = "Yesterday";
			if(endDate.before(_today))
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
	
	private static boolean isSameDate(Date d1, Date d2)
	{
		return d1.getMonth() == d2.getMonth() &&
			   d1.getDate()  == d2.getDate();
	}
	
	public static String getReviewTime(Review r)
	{
		try
		{
			SimpleDateFormat sourceFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			SimpleDateFormat destinationFormat = new SimpleDateFormat("d MMMMM yyyy");
			
			Date timeStamp = sourceFormat.parse(r.getTimestamp());
			Calendar today = Calendar.getInstance();
			
			
			int days = today.get(Calendar.DATE) - timeStamp.getDate(); 
			int hours = today.get(Calendar.HOUR_OF_DAY) - timeStamp.getHours();  
			int minutes = today.get(Calendar.MINUTE) - timeStamp.getMinutes();
					
			if(days == 0 && hours == 0) return minutes + " minutes ago";
			else if(days == 0 && hours == 1 && minutes < 0) return minutes + " minutes ago";
			else if(days == 0) return hours + " hours ago";
			else if(days == 1 && hours < 0) return hours + " hours ago";
			
			return destinationFormat.format(timeStamp);
		}
		catch(ParseException pe) { return "Time of Post Unavailable"; }
	}
}
