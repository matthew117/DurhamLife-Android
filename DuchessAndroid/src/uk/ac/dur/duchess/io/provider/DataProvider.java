package uk.ac.dur.duchess.io.provider;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import uk.ac.dur.duchess.io.xml.ReviewXMLParser;
import uk.ac.dur.duchess.io.xml.SocietyXMLParser;
import uk.ac.dur.duchess.model.Event;
import uk.ac.dur.duchess.model.EventLocation;
import uk.ac.dur.duchess.model.Review;
import uk.ac.dur.duchess.model.Society;
import uk.ac.dur.duchess.webservice.EventAPI;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

public class DataProvider
{
	public static final String CACHE_SHARED_PREFERENCES_KEY = "duchess_cache";
	private static final String CACHE_LAST_MODIFIED_KEY = "last_modified";
	private static final String CACHE_EXPIRES_KEY = "expires";
	
	private static final int CACHE_EVENTS_FOR = 24 * 60 * 60 * 1000; /*milliseconds*/

	private boolean memoryCacheIsValid = false;
	private boolean databaseCacheIsValid = false;
	
	private boolean societyCacheIsValid = false;
	
	private boolean eventDatabaseLock = false;
	private boolean societydatabaseLock = false;

	private List<Event> eventList;

	public DataProvider()
	{
		// Set validity of caches
	}

	public List<Event> getAllEvents(final Context context)
	{
		calculateCacheValidity(context);

		// This suggests that an up to date event list is stored in memory
		if (eventList != null && memoryCacheIsValid)
		{
			Log.d("CACHE", "Loading from Memory");
			return new ArrayList<Event>(eventList);
		}
		// This suggests that an up to date event list is stored in a local database
		else if (!memoryCacheIsValid && databaseCacheIsValid)
		{
			Log.d("CACHE", "Loading from Database");
			DatabaseHandler database = new DatabaseHandler(context);
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
				Log.d("CACHE", "Downloading from Web Service");
				eventList = EventAPI.downloadAllEvents();
				memoryCacheIsValid = true;
				// add the downloaded events to the database in the background
				// and return the event list as soon as possible
				Thread addEventsToDatabase = new Thread(new Runnable()
				{
					@Override
					public void run()
					{
						DatabaseHandler database = new DatabaseHandler(context);
						database.open();
						for (Event event : eventList)
						{
							if (!database.containsEvent(event.getEventID()))
								database.insertEvent(event);
						}
						database.close();
						databaseCacheIsValid = true;
						
						/** This is set manually to overide the value given by the webservice for testing purposes */
						setCacheExpiresAt(context, System.currentTimeMillis() + 24*60*60*1000);
						
						eventDatabaseLock = false;
					}
				});
				if (!eventDatabaseLock)
				{ 
					eventDatabaseLock = true;
					addEventsToDatabase.start();
				}
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
				if (associatedCollege != null && associatedCollege.equals(college))
					collegeEventList.add(event);
			}
			return collegeEventList;
		}
		// This suggests that an up to date college event list is stored in a local database
		else if (!memoryCacheIsValid && databaseCacheIsValid)
		{
			List<Event> collegeEventList = new ArrayList<Event>();

			DatabaseHandler database = new DatabaseHandler(context);
			database.open();
			collegeEventList = database.getEventsByCollege(college);
			database.close();

			/**
			 * memoryCacheIsValid = true; // Can't say this anymore
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
				 * memoryCacheIsValid = true; // Can no longer say that
				 */

				// add the downloaded events to the database in the background
				// and return the event list as soon as possible
				Thread addEventsToDatabase = new Thread(new Runnable()
				{
					@Override
					public void run()
					{
						DatabaseHandler database = new DatabaseHandler(context);
						database.open();
						for (Event event : eventList)
						{
							if (!database.containsEvent(event.getEventID()))
								database.insertEvent(event);
						}
						database.close();

						/**
						 * databaseCacheIsValid = true; Can no longer say that
						 */
					}
				});
				/**addEventsToDatabase.start(); There is no separate cache to make use of these*/
				return collegeEventList;
			}
			catch (IOException e)
			{
				e.printStackTrace();
				return null;
			}
		}
	}

	public List<Event> getEventsByColleges(Context context, List<String> colleges)
	{
		List<Event> collegeEventList = new ArrayList<Event>();
		for (String college : colleges)
			collegeEventList.addAll(getEventsByCollege(context, college));
		return collegeEventList;
	}

	public List<Event> getEventsBySociety(Context context, String society)
	{
		List<Event> eventList = getAllEvents(context);
		List<Event> societyEventList = new ArrayList<Event>();

		for (Event event : eventList)
		{
			if (event.getAssociatedSociety() != null ? event.getAssociatedSociety().equals(society) : false) societyEventList.add(event);
		}

		return societyEventList;
	}

	public List<Event> getEventsBySocieties(Context context, List<String> societies)
	{
		List<Event> societyEventList = new ArrayList<Event>();
		for (String society : societies)
			societyEventList.addAll(getEventsBySociety(context, society));
		return societyEventList;
	}

	public List<Society> getSocieties(final Context context)
	{
		calculateCacheValidity(context);

		// the list of societies is stored in the database so use it
		if (societyCacheIsValid)
		{
			List<Society> societyList = new ArrayList<Society>();
			
			DatabaseHandler database = new DatabaseHandler(context);
			database.open();
			societyList = database.getSocieties();
			database.close();
			
			return societyList;
		}
		// the database is empty or old so download and update
		else
		{
			final List<Society> societyList = new ArrayList<Society>();
			try
			{
				SAXParserFactory factory = SAXParserFactory.newInstance();
				SAXParser parser = factory.newSAXParser();
				XMLReader reader = parser.getXMLReader();
				URL url = new URL("http://www.dur.ac.uk/cs.seg01/duchess/api/v1/societies.php");
				SocietyXMLParser myXMLHandler = new SocietyXMLParser(societyList);
				reader.setContentHandler(myXMLHandler);
				reader.parse(new InputSource(url.openStream()));
			}
			catch (Exception ex)
			{
				return societyList;
			}
			
			Thread addSocietiesToDatabase = new Thread(new Runnable()
			{
				@Override
				public void run()
				{
					DatabaseHandler database = new DatabaseHandler(context);
					database.open();
					for (Society society : societyList)
					{
						if (!database.containsSociety(society.getSocietyID()))
							database.insertSociety(society);
					}
					database.close();
					societyCacheIsValid = true;
					societydatabaseLock = false;
				}
			});
			if (!societydatabaseLock)
			{ 
				societydatabaseLock = true;
				addSocietiesToDatabase.start();
			}
			return societyList;
		}
	}

	public List<Review> getReviews(Context context, int eventID)
	{
		List<Review> reviewList = new ArrayList<Review>();

		try
		{
			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser parser = factory.newSAXParser();
			XMLReader reader = parser.getXMLReader();

			URL url = new URL("http://www.dur.ac.uk/cs.seg01/duchess/api/v1/reviews.php/" + eventID);

			ReviewXMLParser reviewXMLParser = new ReviewXMLParser(reviewList);

			reader.setContentHandler(reviewXMLParser);

			reader.parse(new InputSource(url.openStream()));

			return reviewList;
		}
		catch (Exception ex)
		{
			return reviewList;
		}
	}

	public List<EventLocation> getLocations(Context context)
	{
		List<EventLocation> locations = new ArrayList<EventLocation>();
		
		DatabaseHandler database = new DatabaseHandler(context);
		database.open();
		locations = database.getAllLocations();
		database.close();
		
		return locations;
	}

	private void calculateCacheValidity(Context context)
	{
		SharedPreferences sharedPrefs = context.getSharedPreferences(CACHE_SHARED_PREFERENCES_KEY, Context.MODE_PRIVATE);
		long expiresAt = sharedPrefs.getLong(CACHE_EXPIRES_KEY, 0);

		Log.d("CACHE", String.format("expires: %d", expiresAt));

		if (System.currentTimeMillis() < expiresAt)
		{
			databaseCacheIsValid = true;
		}
		else
		{
			databaseCacheIsValid = false;
			memoryCacheIsValid = false;
		}
	}
	
	public void invalidateCache(Context context)
	{
		Editor editor = context.getSharedPreferences(CACHE_SHARED_PREFERENCES_KEY,
				Context.MODE_PRIVATE).edit();
		editor.putLong(CACHE_EXPIRES_KEY, 0);
		editor.commit();
	}

	public void setCacheExpiresAt(Context context, long timestamp)
	{
		Editor editor = context.getSharedPreferences(CACHE_SHARED_PREFERENCES_KEY,
				Context.MODE_PRIVATE).edit();
		editor.putLong(CACHE_EXPIRES_KEY, timestamp);
		editor.commit();		
	}
}
