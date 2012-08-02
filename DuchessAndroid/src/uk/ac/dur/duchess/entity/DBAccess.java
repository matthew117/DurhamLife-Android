package uk.ac.dur.duchess.entity;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBAccess
{
	public static final String KEY_EVENT_ID = "eventID";
	public static final String KEY_FEATURED = "featured";
	
	public static final String KEY_NAME = "name";
	public static final String KEY_DESCRIPTION_HEADER = "descriptionHeader";
	public static final String KEY_DESCRIPTION_BODY = "descriptionBody";
	
	public static final String KEY_START_DATE = "startDate";
	public static final String KEY_END_DATE = "endDate";
	public static final String KEY_ICAL_URL = "iCalURL";
	
	public static final String KEY_SCOPE = "scope";
	public static final String KEY_ASSOCIATED_COLLEGE = "associatedCollege";
	public static final String KEY_ASSOCIATED_SOCIETY = "associatedSociety";
	
	public static final String KEY_CONTACT_TELEPHONE_NUMBER = "contactTelephoneNumber";
	public static final String KEY_CONTACT_EMAIL_ADDRESS = "contactEmailAddress";
	public static final String KEY_WEB_ADDRESS = "webAddress";
	
	public static final String KEY_ACCESSIBILITY_INFORMATION = "accessibilityInformation";
	
	public static final String KEY_CATEGORY = "category";
	
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
	
	private static final String TAG = "DBAdapter";

	private static final String DATABASE_NAME = "duchessDB";
	private static final int DATABASE_VERSION = 1;
	
	private static final String EVENT_TABLE = "events";
	private static final String LOCATION_TABLE = "locations";
	

	private static final String EVENT_CREATE_STATEMENT =
		"CREATE TABLE events("
			+  KEY_EVENT_ID + " INTEGER PRIMARY KEY, "
			+ "featured INTEGER NOT NULL, "
			+ "name TEXT NOT NULL, "
			+ "descriptionHeader TEXT NOT NULL, "
			+ "descriptionBody TEXT NOT NULL, "
			+ "startDate DATE NOT NULL, "
			+ "endDate DATE NOT NULL, "
			+ KEY_ICAL_URL + " TEXT, "
			+ "locationID INTEGER NOT NULL, "
			+ "scope TEXT NOT NULL, "
			+ "associatedCollege TEXT, "
			+ "associatedSociety TEXT, "
			+ KEY_CONTACT_TELEPHONE_NUMBER + " TEXT, "
			+ KEY_CONTACT_EMAIL_ADDRESS + " TEXT, "
			+ KEY_WEB_ADDRESS + " TEXT, "
			+ "accessibilityInformation TEXT, "			
			+ "category TEXT NOT NULL, "
			+ "imageURL TEXT, "
			+ KEY_AD_IMAGE_URL + " TEXT, "
			+ KEY_REVIEW_SCORE + " INTEGER, "
			+ KEY_NUM_OF_REVIEWS + " INTEGER)";
			
	private static final String LOCATION_CREATE_STATEMENT =
		"CREATE TABLE locations("
			+ "locationID INTEGER PRIMARY KEY, "
			+ "address1 TEXT NOT NULL, "
			+ "address2 TEXT NOT NULL, "
			+ "city TEXT NOT NULL, "
			+ "postcode TEXT NOT NULL, "
			+ "latitude TEXT NOT NULL, "
			+ "longitude TEXT NOT NULL)";

	private final Context context;

	private DatabaseHelper DBHelper;
	private SQLiteDatabase db;

	public DBAccess(Context context)
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
			db.execSQL("DROP TABLE IF EXISTS events");
			db.execSQL("DROP TABLE IF EXISTS locations");
			onCreate(db);
		}
	}

	public DBAccess open() throws SQLException
	{
		db = DBHelper.getWritableDatabase();
		return this;
	}

	public void close()
	{
		DBHelper.close();
	}

	public long insertEvent(Event event)
	{
		ContentValues values = new ContentValues();
		
		values.put(KEY_EVENT_ID, event.getEventID());
		values.put(KEY_FEATURED, event.isFeatured());
		
		values.put(KEY_NAME, event.getName());
		values.put(KEY_DESCRIPTION_HEADER, event.getDescriptionHeader());
		values.put(KEY_DESCRIPTION_BODY, event.getDescriptionBody());
		
		values.put(KEY_START_DATE, event.getStartDate());
		values.put(KEY_END_DATE, event.getEndDate());
		values.put(KEY_ICAL_URL, event.getICalURL());
		
		values.put(KEY_LOCATION_ID, event.getLocation().getLocationID());
		
		if(!containsLocation(event.getLocation().getLocationID()))
			insertLocation(event.getLocation());
		
		values.put(KEY_SCOPE, (event.getScope() != null) ? event.getScope().name() : "OPEN");
		values.put(KEY_ASSOCIATED_COLLEGE, event.getAssociatedCollege());
		values.put(KEY_ASSOCIATED_SOCIETY, event.getAssociatedSociety());
		
		values.put(KEY_CONTACT_TELEPHONE_NUMBER, event.getContactTelephoneNumber());
		values.put(KEY_CONTACT_EMAIL_ADDRESS, event.getContactEmailAddress());
		values.put(KEY_WEB_ADDRESS, event.getWebAddress());
		
		values.put(KEY_ACCESSIBILITY_INFORMATION, event.getAccessibilityInformation());
		
		values.put(KEY_CATEGORY, event.getCategoryTags().toString());
		
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

	public boolean deleteEvent(long eventID)
	{
		return db.delete(EVENT_TABLE, KEY_EVENT_ID + "=" + eventID, null) > 0;
	}
	
	public boolean deleteLocation(long locationID)
	{
		return db.delete(LOCATION_TABLE, KEY_LOCATION_ID + "=" + locationID, null) > 0;
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

	public Cursor getEventSummaries()
	{
		return db.query(EVENT_TABLE, new String[] { KEY_EVENT_ID, KEY_NAME, KEY_START_DATE,
				KEY_END_DATE, KEY_DESCRIPTION_HEADER }, null, null, null, null, null);
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
		
		Event event = new Event();
		
		event.setEventID(eventID);
		
		event.setName(row.getString(row.getColumnIndex(KEY_NAME)));
		event.setDescriptionHeader(row.getString(row.getColumnIndex(KEY_DESCRIPTION_HEADER)));
		event.setDescriptionBody(row.getString(row.getColumnIndex(KEY_DESCRIPTION_BODY)));
		
		event.setStartDate(row.getString(row.getColumnIndex(KEY_START_DATE)));
		event.setEndDate(row.getString(row.getColumnIndex(KEY_END_DATE)));
		if(row.getColumnIndex(KEY_ICAL_URL) != -1) event.setICalURL(row.getString(row.getColumnIndex(KEY_ICAL_URL)));
		
		event.setLocation(getLocation(row.getLong(row.getColumnIndex(KEY_LOCATION_ID))));
		
		event.setScope(row.getString(row.getColumnIndex(KEY_SCOPE)));
		if(row.getColumnIndex(KEY_ASSOCIATED_COLLEGE) != -1) event.setAssociatedCollege(row.getString(row.getColumnIndex(KEY_ASSOCIATED_COLLEGE)));
		if(row.getColumnIndex(KEY_ASSOCIATED_SOCIETY) != -1) event.setAssociatedSociety(row.getString(row.getColumnIndex(KEY_ASSOCIATED_SOCIETY)));
		
		if(row.getColumnIndex(KEY_CONTACT_TELEPHONE_NUMBER) != -1) event.setContactTelephoneNumber(row.getString(row.getColumnIndex(KEY_CONTACT_TELEPHONE_NUMBER)));
		if(row.getColumnIndex(KEY_CONTACT_EMAIL_ADDRESS) != -1) event.setContactEmailAddress(row.getString(row.getColumnIndex(KEY_CONTACT_EMAIL_ADDRESS)));
		if(row.getColumnIndex(KEY_WEB_ADDRESS) != -1) event.setWebAddress(row.getString(row.getColumnIndex(KEY_WEB_ADDRESS)));
		
		if(row.getColumnIndex(KEY_ACCESSIBILITY_INFORMATION) != -1) event.setAccessibilityInformation(row.getString(row.getColumnIndex(KEY_ACCESSIBILITY_INFORMATION)));
		
		event.setCategoryTags(row.getString(row.getColumnIndex(KEY_CATEGORY)));
		
		if(row.getColumnIndex(KEY_IMAGE_URL) != -1) event.setImageURL(row.getString(row.getColumnIndex(KEY_IMAGE_URL)));
		if(row.getColumnIndex(KEY_AD_IMAGE_URL) != -1) event.setAdImageURL(row.getString(row.getColumnIndex(KEY_AD_IMAGE_URL)));
		
		if(row.getColumnIndex(KEY_REVIEW_SCORE) != -1) event.setReviewScore(row.getInt(row.getColumnIndex(KEY_REVIEW_SCORE)));
		if(row.getColumnIndex(KEY_NUM_OF_REVIEWS) != -1) event.setNumberOfReviews(row.getInt(row.getColumnIndex(KEY_NUM_OF_REVIEWS)));
		
		row.close();
		
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
		
		EventLocation location = new EventLocation();
		
		location.setLocationID(locationID);
		location.setAddress1(row.getString(row.getColumnIndex(KEY_ADDRESS_1)));
		location.setAddress2(row.getString(row.getColumnIndex(KEY_ADDRESS_2)));
		location.setCity(row.getString(row.getColumnIndex(KEY_CITY)));
		location.setPostcode(row.getString(row.getColumnIndex(KEY_POSTCODE)));
		location.setLatitude(row.getString(row.getColumnIndex(KEY_LATITUDE)));
		location.setLongitude(row.getString(row.getColumnIndex(KEY_LONGITUDE)));
		
		row.close();
		
		return location;
	}
}
