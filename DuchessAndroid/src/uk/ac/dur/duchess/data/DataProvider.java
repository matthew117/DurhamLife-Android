package uk.ac.dur.duchess.data;

import java.util.List;

import uk.ac.dur.duchess.entity.Event;
import uk.ac.dur.duchess.entity.Society;

public class DataProvider
{
	public enum EventLoadMethod
	{
		DOWNLOAD, DATABASE, UNKNOWN;
	}

	private EventLoadMethod method = EventLoadMethod.UNKNOWN;

	public List<Event> getAllEvents(){return null;}
	
	public List<Event> getEventsByColleges(List<String> colleges) {return null;}
	
	public List<Event> getEventsBySociety(String society) {return null;}
	
	public List<Event> getEventsBySocieties(List<String> societies) {return null;}
	
	public List<Society> getSocieties() {return null;}

	public EventLoadMethod getLoadMethod()
	{
		return method;
	}
	
	public void setLoadMethod(EventLoadMethod method)
	{
		this.method = method;
	}
}
