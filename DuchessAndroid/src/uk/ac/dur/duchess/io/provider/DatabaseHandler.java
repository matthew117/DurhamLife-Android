package uk.ac.dur.duchess.io.provider;

import java.util.ArrayList;
import java.util.List;

import uk.ac.dur.duchess.model.Event;
import uk.ac.dur.duchess.model.EventLocation;
import uk.ac.dur.duchess.model.Society;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHandler
{
	public static final String KEY_EVENT_ID = "eventID";
	public static final String KEY_FEATURED = "featured";
	
	public static final String KEY_EVENT_NAME = "name";
	public static final String KEY_DESCRIPTION_HEADER = "descriptionHeader";
	public static final String KEY_DESCRIPTION_BODY = "descriptionBody";
	
	public static final String KEY_START_DATE = "startDate";
	public static final String KEY_END_DATE = "endDate";
	public static final String KEY_START_TIME = "startTime";
	public static final String KEY_END_TIME = "endTime";
	public static final String KEY_ICAL_URL = "iCalURL";
	
	public static final String KEY_SCOPE = "scope";
	public static final String KEY_PRIVACY = "privacy";
	public static final String KEY_ASSOCIATED_COLLEGE = "associatedCollege";
	public static final String KEY_ASSOCIATED_SOCIETY = "associatedSociety";
	
	public static final String KEY_CONTACT_TELEPHONE_NUMBER = "contactTelephoneNumber";
	public static final String KEY_CONTACT_EMAIL_ADDRESS = "contactEmailAddress";
	public static final String KEY_WEB_ADDRESS = "webAddress";
	
	public static final String KEY_ACCESSIBILITY_INFORMATION = "accessibilityInformation";
	
	public static final String KEY_CATEGORIES = "category";
	
	public static final String KEY_IMAGE_URL = "imageURL";
	public static final String KEY_AD_IMAGE_URL = "adImageURL";
	
	public static final String KEY_REVIEW_SCORE = "reviewScore";
	public static final String KEY_NUM_OF_REVIEWS = "numberOfReviews";
	
	public static final String KEY_LOCATION_ID = "locationID";
	public static final String KEY_ADDRESS_1 = "address1";
	public static final String KEY_ADDRESS_2 = "address2";
	public static final String KEY_CITY = "city";
	public static final String KEY_POSTCODE = "postcode";
	public static final String KEY_LATITUDE = "latitude";
	public static final String KEY_LONGITUDE = "longitude";
	
	public static final String KEY_SOCIETY_ID = "societyID";
	public static final String KEY_SOCIETY_NAME = "name";
	public static final String KEY_SOCIETY_WEBSITE = "website";
	public static final String KEY_SOCIETY_EMAIL = "email";
	public static final String KEY_CONSTITUTION = "constitution";
	
	private static final String TAG = "DBAdapter";

	private static final String DATABASE_NAME = "duchessDB";
	private static final int DATABASE_VERSION = 1;
	
	private static final String EVENT_TABLE = "events";
	private static final String LOCATION_TABLE = "locations";
	private static final String SOCIETY_TABLE = "societies";
	

	private static final String EVENT_CREATE_STATEMENT =
		"CREATE TABLE " + EVENT_TABLE + "("
			+ KEY_EVENT_ID + " INTEGER PRIMARY KEY, "
			+ KEY_FEATURED + " INTEGER, "
			+ KEY_EVENT_NAME + " TEXT NOT NULL, "
			+ KEY_DESCRIPTION_HEADER + " TEXT NOT NULL, "
			+ KEY_DESCRIPTION_BODY + " TEXT NOT NULL, "
			+ KEY_START_DATE + " DATE NOT NULL, "
			+ KEY_END_DATE + " DATE NOT NULL, "
			+ KEY_START_TIME + " DATE, "
			+ KEY_END_TIME + " DATE, "
			+ KEY_ICAL_URL + " TEXT, "
			+ KEY_LOCATION_ID + " INTEGER NOT NULL, "
			+ KEY_SCOPE + " TEXT, "
			+ KEY_PRIVACY + " TEXT, "
			+ KEY_ASSOCIATED_COLLEGE + " TEXT, "
			+ KEY_ASSOCIATED_SOCIETY + " TEXT, "
			+ KEY_CONTACT_TELEPHONE_NUMBER + " TEXT, "
			+ KEY_CONTACT_EMAIL_ADDRESS + " TEXT, "
			+ KEY_WEB_ADDRESS + " TEXT, "
			+ KEY_ACCESSIBILITY_INFORMATION + " TEXT, "			
			+ KEY_CATEGORIES + " TEXT NOT NULL, "
			+ KEY_IMAGE_URL + " TEXT, "
			+ KEY_AD_IMAGE_URL + " TEXT, "
			+ KEY_REVIEW_SCORE + " INTEGER, "
			+ KEY_NUM_OF_REVIEWS + " INTEGER)";
			
	private static final String LOCATION_CREATE_STATEMENT =
		"CREATE TABLE " + LOCATION_TABLE + "("
			+ KEY_LOCATION_ID + " INTEGER PRIMARY KEY, "
			+ KEY_ADDRESS_1 + " TEXT, "
			+ KEY_ADDRESS_2 + " TEXT, "
			+ KEY_CITY + " TEXT, "
			+ KEY_POSTCODE + " TEXT, "
			+ KEY_LATITUDE + " TEXT, "
			+ KEY_LONGITUDE + " TEXT)";
	
	private static final String SOCIETY_CREATE_STATEMENT =
		"CREATE TABLE " + SOCIETY_TABLE + "("
			+ KEY_SOCIETY_ID + " INTEGER PRIMARY KEY, "
			+ KEY_SOCIETY_NAME + " TEXT NOT NULL, "
			+ KEY_SOCIETY_WEBSITE + " TEXT, "
			+ KEY_SOCIETY_EMAIL + " TEXT, "
			+ KEY_CONSTITUTION + " TEXT)";

	private final Context context;

	private DatabaseHelper DBHelper;
	private SQLiteDatabase db;

	public DatabaseHandler(Context context)
	{
		this.context = context;
		DBHelper = new DatabaseHelper(this.context);
	}

	private static class DatabaseHelper extends SQLiteOpenHelper
	{
		public DatabaseHelper(Context context)
		{
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db)
		{
			try
			{
				db.execSQL(EVENT_CREATE_STATEMENT);
				db.execSQL(LOCATION_CREATE_STATEMENT);
				db.execSQL(SOCIETY_CREATE_STATEMENT);
			}
			catch (SQLException e)
			{
				e.printStackTrace();
			}
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
		{
			Log.w(TAG, "Upgrading database from version " + oldVersion + " to " + newVersion
					+ ", which will destroy all old data");
			
			db.execSQL("DROP TABLE IF EXISTS " + EVENT_TABLE);
			db.execSQL("DROP TABLE IF EXISTS " + LOCATION_TABLE);
			db.execSQL("DROP TABLE IF EXISTS " + SOCIETY_TABLE);
			onCreate(db);
		}
	}

	public DatabaseHandler open() throws SQLException
	{
		db = DBHelper.getWritableDatabase();
		return this;
	}

	public void close()
	{
		DBHelper.close();
	}
	
	public boolean eventTableIsEmpty()
	{
		Cursor c = db.query(EVENT_TABLE, new String[] {KEY_EVENT_ID},
				null, null, null, null, null);
			
		int rows = c.getCount();
			
		c.close();
			
		return rows == 0;
	}

	public long insertEvent(Event event)
	{
		ContentValues values = new ContentValues();
		
		values.put(KEY_EVENT_ID, event.getEventID());
		values.put(KEY_FEATURED, event.isFeatured());
		
		values.put(KEY_EVENT_NAME, event.getName());
		values.put(KEY_DESCRIPTION_HEADER, event.getDescriptionHeader());
		values.put(KEY_DESCRIPTION_BODY, event.getDescriptionBody());
		
		values.put(KEY_START_DATE, event.getStartDate());
		values.put(KEY_END_DATE, event.getEndDate());
		values.put(KEY_START_TIME, event.getStartTime());
		values.put(KEY_END_TIME, event.getEndTime());
		values.put(KEY_ICAL_URL, event.getICalURL());
		
		values.put(KEY_LOCATION_ID, event.getLocation().getLocationID());
		
		if(!containsLocation(event.getLocation().getLocationID()))
			insertLocation(event.getLocation());
		
		values.put(KEY_SCOPE, (event.getScope() != null) ? event.getScope().name() : "PUBLIC");
		values.put(KEY_PRIVACY, (event.getPrivacy() != null) ? event.getPrivacy().name() : "OPEN");
		values.put(KEY_ASSOCIATED_COLLEGE, event.getAssociatedCollege());
		values.put(KEY_ASSOCIATED_SOCIETY, event.getAssociatedSociety());
		
		values.put(KEY_CONTACT_TELEPHONE_NUMBER, event.getContactTelephoneNumber());
		values.put(KEY_CONTACT_EMAIL_ADDRESS, event.getContactEmailAddress());
		values.put(KEY_WEB_ADDRESS, event.getWebAddress());
		
		values.put(KEY_ACCESSIBILITY_INFORMATION, event.getAccessibilityInformation());
		
		values.put(KEY_CATEGORIES, event.getCategoryTags().toString());
		
		values.put(KEY_IMAGE_URL, event.getImageURL());
		values.put(KEY_AD_IMAGE_URL, event.getAdImageURL());
		
		values.put(KEY_REVIEW_SCORE, event.getReviewScore());
		values.put(KEY_NUM_OF_REVIEWS, event.getNumberOfReviews());
		
		return db.insert(EVENT_TABLE, null, values);
	}
	
	public long insertLocation(EventLocation location)
	{
		ContentValues values = new ContentValues();
		
		values.put(KEY_LOCATION_ID, location.getLocationID());
		values.put(KEY_ADDRESS_1, location.getAddress1());
		values.put(KEY_ADDRESS_2, location.getAddress2());
		values.put(KEY_CITY, location.getCity());
		values.put(KEY_POSTCODE, location.getPostcode());
		values.put(KEY_LATITUDE, location.getLatitude());
		values.put(KEY_LONGITUDE, location.getLongitude());
		
		return db.insert(LOCATION_TABLE, null, values);
	}
	
	public long insertSociety(Society society)
	{
		ContentValues values = new ContentValues();
		
		values.put(KEY_SOCIETY_ID, society.getSocietyID());
		values.put(KEY_SOCIETY_NAME, society.getName());
		values.put(KEY_SOCIETY_WEBSITE, society.getWebsite());
		values.put(KEY_SOCIETY_EMAIL, society.getEmail());
		values.put(KEY_CONSTITUTION, society.getConstitution());
		
		return db.insert(SOCIETY_TABLE, null, values);
	}

	public boolean deleteEvent(long eventID)
	{
		return db.delete(EVENT_TABLE, KEY_EVENT_ID + "=" + eventID, null) > 0;
	}
	
	public boolean deleteLocation(long locationID)
	{
		return db.delete(LOCATION_TABLE, KEY_LOCATION_ID + "=" + locationID, null) > 0;
	}
	
	public boolean deleteSociety(long societyID)
	{
		return db.delete(SOCIETY_TABLE, KEY_SOCIETY_ID + "=" + societyID, null) > 0;
	}
	
	public boolean containsLocation(long locationID)
	{
		Cursor c = db.query(LOCATION_TABLE, new String[] {KEY_LOCATION_ID},
			KEY_LOCATION_ID + "=" + locationID, null, null, null, null);
		
		int rows = c.getCount();
		
		c.close();
		
		return rows != 0;
	}
	
	public boolean containsEvent(long eventID)
	{
		Cursor c = db.query(EVENT_TABLE, new String[] {KEY_EVENT_ID},
			KEY_EVENT_ID + "=" + eventID, null, null, null, null);
		
		int rows = c.getCount();
		
		c.close();
		
		return rows != 0;
	}
	
	public boolean containsSociety(long societyID)
	{
		Cursor c = db.query(SOCIETY_TABLE, new String[] {KEY_SOCIETY_ID},
			KEY_SOCIETY_ID + "=" + societyID, null, null, null, null);
		
		int rows = c.getCount();
		
		c.close();
		
		return rows != 0;
	}
	
	public Event getEvent(long eventID)
	{
		Cursor row = db.query(EVENT_TABLE, null, KEY_EVENT_ID + "=" + eventID, 
				null, null, null, null);
		
		if(row.getCount() == 0)
		{
			row.close();
			return null;
		}
		else row.moveToFirst();
		
		Event event = toEvent(row);
		
		row.close();
		
		return event;
	}
	
	public List<Event> queryEventTable(String query)
	{
		List<Event> events = new ArrayList<Event>();
		
		Cursor row = db.query(EVENT_TABLE, null, query, null, null, null, null);

		if(row.getCount() == 0)
		{
			row.close();
			return events;
		}
		else row.moveToFirst();

		while(!row.isAfterLast())
		{
			events.add(toEvent(row));
			row.moveToNext();
		}

		row.close();

		return events;
	}
	
	public List<Event> getAllEvents()
	{
		return queryEventTable(null);
	}
	
	public List<Event> getEventsByCollege(String college)
	{
		return queryEventTable(KEY_ASSOCIATED_COLLEGE + "=\"" + college + "\"");
	}
	
	public List<Event> getEventsByColleges(List<String> colleges)
	{
		StringBuilder query = new StringBuilder();
		
		for(int i = 0; i < colleges.size(); i++)
		{
			query.append(KEY_ASSOCIATED_COLLEGE + "=\"" + colleges.get(i) +
					(i == colleges.size() - 1 ? "\"" : "\" OR "));
		}
		
		return queryEventTable(query.toString());
	}
	
	public List<Event> getEventsBySociety(String society)
	{
		return queryEventTable(KEY_ASSOCIATED_SOCIETY + "=\"" + society + "\"");
	}
	
	public List<Event> getEventsBySocieties(List<String> societies)
	{
		StringBuilder query = new StringBuilder();
		
		for(int i = 0; i < societies.size(); i++)
		{
			query.append(KEY_ASSOCIATED_SOCIETY + "=\"" + societies.get(i) +
					(i == societies.size() - 1 ? "\"" : "\" OR "));
		}
		
		return queryEventTable(query.toString());
	}

	private Event toEvent(Cursor row)
	{
		Event event = new Event();
		
		event.setEventID(row.getLong(row.getColumnIndex(KEY_EVENT_ID)));
		
		event.setName(row.getString(row.getColumnIndex(KEY_EVENT_NAME)));
		event.setDescriptionHeader(row.getString(row.getColumnIndex(KEY_DESCRIPTION_HEADER)));
		event.setDescriptionBody(row.getString(row.getColumnIndex(KEY_DESCRIPTION_BODY)));
		
		event.setStartDate(row.getString(row.getColumnIndex(KEY_START_DATE)));
		event.setEndDate(row.getString(row.getColumnIndex(KEY_END_DATE)));
		if(row.getColumnIndex(KEY_START_TIME) != -1)
			event.setStartTime(row.getString(row.getColumnIndex(KEY_START_TIME)));
		if(row.getColumnIndex(KEY_END_TIME) != -1)
			event.setEndTime(row.getString(row.getColumnIndex(KEY_END_TIME)));
		
		if(row.getColumnIndex(KEY_ICAL_URL) != -1)
			event.setICalURL(row.getString(row.getColumnIndex(KEY_ICAL_URL)));
		
		EventLocation location = getLocation(row.getLong(row.getColumnIndex(KEY_LOCATION_ID)));
		event.setLocation(location);
		
		event.setScope(row.getString(row.getColumnIndex(KEY_SCOPE)));
		event.setPrivacy(row.getString(row.getColumnIndex(KEY_PRIVACY)));
		if(row.getColumnIndex(KEY_ASSOCIATED_COLLEGE) != -1)
			event.setAssociatedCollege(row.getString(row.getColumnIndex(KEY_ASSOCIATED_COLLEGE)));
		if(row.getColumnIndex(KEY_ASSOCIATED_SOCIETY) != -1)
			event.setAssociatedSociety(row.getString(row.getColumnIndex(KEY_ASSOCIATED_SOCIETY)));
		
		if(row.getColumnIndex(KEY_CONTACT_TELEPHONE_NUMBER) != -1)
			event.setContactTelephoneNumber(row.getString(row.getColumnIndex(KEY_CONTACT_TELEPHONE_NUMBER)));
		if(row.getColumnIndex(KEY_CONTACT_EMAIL_ADDRESS) != -1)
			event.setContactEmailAddress(row.getString(row.getColumnIndex(KEY_CONTACT_EMAIL_ADDRESS)));
		if(row.getColumnIndex(KEY_WEB_ADDRESS) != -1)
			event.setWebAddress(row.getString(row.getColumnIndex(KEY_WEB_ADDRESS)));
		
		if(row.getColumnIndex(KEY_ACCESSIBILITY_INFORMATION) != -1)
			event.setAccessibilityInformation(row.getString(row.getColumnIndex(KEY_ACCESSIBILITY_INFORMATION)));
		
		event.setCategoryTags(row.getString(row.getColumnIndex(KEY_CATEGORIES)));
		
		if(row.getColumnIndex(KEY_IMAGE_URL) != -1)
			event.setImageURL(row.getString(row.getColumnIndex(KEY_IMAGE_URL)));
		if(row.getColumnIndex(KEY_AD_IMAGE_URL) != -1)
			event.setAdImageURL(row.getString(row.getColumnIndex(KEY_AD_IMAGE_URL)));
		
		if(row.getColumnIndex(KEY_REVIEW_SCORE) != -1)
			event.setReviewScore(row.getInt(row.getColumnIndex(KEY_REVIEW_SCORE)));
		if(row.getColumnIndex(KEY_NUM_OF_REVIEWS) != -1)
			event.setNumberOfReviews(row.getInt(row.getColumnIndex(KEY_NUM_OF_REVIEWS)));
		
		return event;
	}
	
	public EventLocation getLocation(long locationID)
	{
		Cursor row = db.query(LOCATION_TABLE, null, KEY_LOCATION_ID + "=" + locationID,
				null, null, null, null);
		
		if(row.getCount() == 0)
		{
			row.close();
			return null;
		}
		else row.moveToFirst();
		
		EventLocation location = toLocation(row);
		
		row.close();
		
		return location;
	}
	
	public List<EventLocation> getAllLocations()
	{
		List<EventLocation> locations = new ArrayList<EventLocation>();
		
		Cursor row = db.query(LOCATION_TABLE, null, null, null, null, null, null);
		
		if(row.getCount() == 0)
		{
			row.close();
			return locations;
		}
		else row.moveToFirst();

		while(!row.isAfterLast())
		{
			locations.add(toLocation(row));
			row.moveToNext();
		}

		row.close();

		return locations;
	}
	
	private EventLocation toLocation(Cursor row)
	{
		EventLocation location = new EventLocation();
		
		location.setLocationID(row.getLong(row.getColumnIndex(KEY_LOCATION_ID)));
		location.setAddress1(row.getString(row.getColumnIndex(KEY_ADDRESS_1)));
		location.setAddress2(row.getString(row.getColumnIndex(KEY_ADDRESS_2)));
		location.setCity(row.getString(row.getColumnIndex(KEY_CITY)));
		location.setPostcode(row.getString(row.getColumnIndex(KEY_POSTCODE)));
		location.setLatitude(row.getString(row.getColumnIndex(KEY_LATITUDE)));
		location.setLongitude(row.getString(row.getColumnIndex(KEY_LONGITUDE)));
		
		return location;
	}
	
	public List<Society> getSocieties()
	{
		List<Society> societies = new ArrayList<Society>();
		
		Cursor row = db.query(SOCIETY_TABLE, null, null, null, null, null, null);

		if(row.getCount() == 0)
		{
			row.close();
			return societies;
		}
		else row.moveToFirst();

		while(!row.isAfterLast())
		{
			societies.add(toSociety(row));
			row.moveToNext();
		}

		row.close();

		return societies;
	}
	
	public Society getSociety(String name)
	{
		Cursor row = db.query(SOCIETY_TABLE, null, KEY_SOCIETY_NAME + "=\"" + name + "\"",
				null, null, null, null);
		
		if(row.getCount() == 0)
		{
			row.close();
			return null;
		}
		else row.moveToFirst();
		
		Society society = toSociety(row);
		
		row.close();
		
		return society;
	}
	
	private Society toSociety(Cursor row)
	{
		Society society = new Society();
		
		society.setSocietyID(row.getLong(row.getColumnIndex(KEY_SOCIETY_ID)));
		
		society.setName(row.getString(row.getColumnIndex(KEY_SOCIETY_NAME)));

		if(row.getColumnIndex(KEY_SOCIETY_WEBSITE) != -1)
			society.setWebsite(row.getString(row.getColumnIndex(KEY_SOCIETY_WEBSITE)));
		
		if(row.getColumnIndex(KEY_SOCIETY_EMAIL) != -1)
			society.setEmail(row.getString(row.getColumnIndex(KEY_SOCIETY_EMAIL)));
		
		if(row.getColumnIndex(KEY_CONSTITUTION) != -1)
			society.setConstitution(row.getString(row.getColumnIndex(KEY_CONSTITUTION)));
		
		return society;
	}
}
