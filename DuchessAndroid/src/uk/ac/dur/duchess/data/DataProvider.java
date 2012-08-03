package uk.ac.dur.duchess.data;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import uk.ac.dur.duchess.entity.DBAccess;
import uk.ac.dur.duchess.entity.Event;
import uk.ac.dur.duchess.entity.Society;
import uk.ac.dur.duchess.webservice.EventAPI;
import android.content.Context;

public class DataProvider
{	
	private boolean memoryCacheIsValid = false;
	private boolean databaseCacheIsValid = false;
	
	private List<Event> eventList;
	
	public DataProvider()
	{
		// Set validity of caches
	}

	public List<Event> getAllEvents(final Context context)
	{
		calculateCacheValidity(context);
		
		// This suggests that an up to date event list is stored in memory
		if (eventList != null && memoryCacheIsValid) return new ArrayList<Event>(eventList);
		// This suggests that an up to date event list is stored in a local database
		else if (!memoryCacheIsValid && databaseCacheIsValid)
		{
			DBAccess database = new DBAccess(context);
			database.open();
			eventList = database.getAllEvents();
			database.close();
			
			memoryCacheIsValid = true;
			return new ArrayList<Event>(eventList);
		}
		// No valid cache exists so events need to be downloaded
		else
		{
			try
			{
				eventList = EventAPI.downloadAllEvents();
				memoryCacheIsValid = true;
				// add the downloaded events to the database in the background
				// and return the event list as soon as possible
				Thread addEventsToDatabase = new Thread(new Runnable()
				{		
					@Override
					public void run()
					{
						DBAccess database = new DBAccess(context);
						database.open();
						for (Event event : eventList)
						{
							if(!database.containsEvent(event.getEventID())) database.insertEvent(event);
						}
						database.close();
						databaseCacheIsValid = true;
					}
				});
				addEventsToDatabase.start();
				return new ArrayList<Event>(eventList);
			}
			catch (IOException e)
			{
				e.printStackTrace();
				return null;
			}
		}
	}
	
	public List<Event> getEventsByCollege(final Context context, String college)
	{
		calculateCacheValidity(context);
		
		// Every event is stored in memory so the college events can just be filtered out
		if (eventList != null && memoryCacheIsValid)
		{
			List<Event> collegeEventList = new ArrayList<Event>();
			for (Event event : eventList)
			{
				String associatedCollege = event.getAssociatedCollege();
				if (associatedCollege != null && associatedCollege.equals(college)) collegeEventList.add(event);
			}
			return collegeEventList;
		}
		// This suggests that an up to date college event list is stored in a local database
		else if (!memoryCacheIsValid && databaseCacheIsValid)
		{
			List<Event> collegeEventList = new ArrayList<Event>();
			
			DBAccess database = new DBAccess(context);
			database.open();
			collegeEventList = database.getEventsByCollege(college);
			database.close();
			
			/**
			memoryCacheIsValid = true; // Can't say this anymore
			*/
			
			return collegeEventList;
		}
		// No valid cache exists so events need to be downloaded
		else
		{
			try
			{
				List<Event> collegeEventList = EventAPI.downloadEventsByCollege(college);
				
				if (!memoryCacheIsValid) eventList = new ArrayList<Event>(collegeEventList);
				
				/**
				memoryCacheIsValid = true; // Can no longer say that
				*/
				
				// add the downloaded events to the database in the background
				// and return the event list as soon as possible
				Thread addEventsToDatabase = new Thread(new Runnable()
				{		
					@Override
					public void run()
					{
						DBAccess database = new DBAccess(context);
						database.open();
						for (Event event : eventList)
						{
							if(!database.containsEvent(event.getEventID())) database.insertEvent(event);
						}
						database.close();
						
						/**
						databaseCacheIsValid = true; Can no longer say that
						*/
					}
				});
				addEventsToDatabase.start();
				return collegeEventList;
			}
			catch (IOException e)
			{
				e.printStackTrace();
				return null;
			}
		}
	}

	public List<Event> getEventsByColleges(List<String> colleges) {return null;}
	
	public List<Event> getEventsBySociety(String society) {return null;}
	
	public List<Event> getEventsBySocieties(List<String> societies) {return null;}
	
	public List<Society> getSocieties() {return null;}
	
	private void calculateCacheValidity(Context context)
	{
		// TODO Auto-generated method stub
		
	}
}
