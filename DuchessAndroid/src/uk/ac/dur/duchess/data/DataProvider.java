package uk.ac.dur.duchess.data;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import uk.ac.dur.duchess.entity.DBAccess;
import uk.ac.dur.duchess.entity.Event;
import uk.ac.dur.duchess.entity.EventLocation;
import uk.ac.dur.duchess.entity.Review;
import uk.ac.dur.duchess.entity.ReviewXMLParser;
import uk.ac.dur.duchess.entity.Society;
import uk.ac.dur.duchess.entity.SocietyXMLParser;
import uk.ac.dur.duchess.webservice.EventAPI;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

public class DataProvider
{
	public static final String CACHE_SHARED_PREFERENCES_KEY = "duchess_cache";
	private static final String CACHE_LAST_MODIFIED_KEY = "last_modified";
	private static final String CACHE_EXPIRES_IN_KEY = "expires_in";

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
		if (eventList != null && memoryCacheIsValid)
		{
			Log.d("CACHE", "Loading from Memory");
			return new ArrayList<Event>(eventList);
		}
		// This suggests that an up to date event list is stored in a local database
		else if (!memoryCacheIsValid && databaseCacheIsValid)
		{
			Log.d("CACHE", "Loading from Database");
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
						DBAccess database = new DBAccess(context);
						database.open();
						for (Event event : eventList)
						{
							if (!database.containsEvent(event.getEventID()))
								database.insertEvent(event);
						}
						database.close();
						databaseCacheIsValid = true;
						updateCacheLastModified(context, System.currentTimeMillis());
						setCacheExpiresIn(context, 60 * 60 * 1000 /* milliseconds */);
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
				if (associatedCollege != null && associatedCollege.equals(college))
					collegeEventList.add(event);
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
						DBAccess database = new DBAccess(context);
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

	public List<Society> getSocieties(Context context)
	{
		List<Society> societyList = new ArrayList<Society>();

		try
		{
			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser parser = factory.newSAXParser();
			final XMLReader reader = parser.getXMLReader();

			final URL url = new URL("http://www.dur.ac.uk/cs.seg01/duchess/api/v1/societies.php");

			SocietyXMLParser myXMLHandler = new SocietyXMLParser(societyList);

			reader.setContentHandler(myXMLHandler);

			reader.parse(new InputSource(url.openStream()));
		}
		catch (Exception ex)
		{}

		return societyList;
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
		
		DBAccess database = new DBAccess(context);
		database.open();
		locations = database.getAllLocations();
		database.close();
		
		return locations;
	}

	private void calculateCacheValidity(Context context)
	{
		SharedPreferences sharedPrefs = context.getSharedPreferences(CACHE_SHARED_PREFERENCES_KEY,
				Context.MODE_PRIVATE);
		long lastModified = sharedPrefs.getLong(CACHE_LAST_MODIFIED_KEY, 0);
		long expiresIn = sharedPrefs.getLong(CACHE_EXPIRES_IN_KEY, 0);

		Log.d("CACHE",
				String.format("modified: %d\nexpires:  %d", lastModified, lastModified + expiresIn));

		if (System.currentTimeMillis() < lastModified + expiresIn)
		{
			databaseCacheIsValid = true;
		}
		else
		{
			databaseCacheIsValid = false;
			memoryCacheIsValid = false;
		}
	}

	private void updateCacheLastModified(Context context, long timeStamp)
	{
		Editor editor = context.getSharedPreferences(CACHE_SHARED_PREFERENCES_KEY,
				Context.MODE_PRIVATE).edit();
		editor.putLong(CACHE_LAST_MODIFIED_KEY, timeStamp);
		editor.commit();
	}

	public void setCacheExpiresIn(Context context, long timeStamp)
	{
		Editor editor = context.getSharedPreferences(CACHE_SHARED_PREFERENCES_KEY,
				Context.MODE_PRIVATE).edit();
		editor.putLong(CACHE_EXPIRES_IN_KEY, timeStamp);
		editor.commit();
	}
	
	public void invalidateCache(Context context)
	{
		Editor editor = context.getSharedPreferences(CACHE_SHARED_PREFERENCES_KEY,
				Context.MODE_PRIVATE).edit();
		editor.putLong(CACHE_EXPIRES_IN_KEY, 0);
		editor.commit();
	}
}
