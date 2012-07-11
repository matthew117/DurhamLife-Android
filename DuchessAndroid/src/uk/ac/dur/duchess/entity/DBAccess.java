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
	
	public static final String KEY_CONTACT_TELEPHONE_NUMBER = "contactTelephoneNumber";
	public static final String KEY_CONTACT_EMAIL_ADDRESS = "contactEmailAddress";
	public static final String KEY_WEB_ADDRESS = "webAddress";
	
	public static final String KEY_LOCATION_ID = "locationID";
	public static final String KEY_ADDRESS_1 = "address1";
	public static final String KEY_ADDRESS_2 = "address1";
	public static final String KEY_CITY = "city";
	public static final String KEY_POSTCODE = "postcode";
	public static final String KEY_LATITUDE = "latitude";
	public static final String KEY_LONGITUDE = "longitude";
	
	public static final String KEY_ACCESSIBILITY_INFORMATION = "accessibilityInformation";
	
	public static final String KEY_CATEGORY = "category";
	
	public static final String KEY_IMAGE_URL = "imageURL";

	private static final String TAG = "DBAdapter";

	private static final String DATABASE_NAME = "duchessDB";
	private static final int DATABASE_VERSION = 1;
	
	private static final String EVENT_TABLE = "events";
	private static final String LOCATION_TABLE = "locations";
	

	private static final String DATABASE_CREATE_STATEMENT =
		"CREATE TABLE events("
			+ "eventID INTEGER PRIMARY KEY, "
			+ "featured INTEGER NOT NULL, "
			+ "name TEXT NOT NULL, "
			+ "descriptionHeader TEXT NOT NULL, "
			+ "descriptionBody TEXT NOT NULL, "
			+ "startDate DATE NOT NULL, "
			+ "endDate DATE NOT NULL, "
			+ "locationID INTEGER NOT NULL, "
			+ "accessibilityInformation TEXT NOT NULL, "			
			+ "category TEXT NOT NULL, "
			+ "imageURL TEXT); " +
			
		"CREATE TABLE locations("
			+ "locationID INTEGER PRIMARY KEY, "
			+ "address1 TEXT NOT NULL, "
			+ "address2 TEXT NOT NULL, "
			+ "city TEXT NOT NULL, "
			+ "postcode TEXT NOT NULL, "
			+ "latitude TEXT NOT NULL, "
			+ "longitude TEXT NOT NULL);";

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
				db.execSQL(DATABASE_CREATE_STATEMENT);
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
		ContentValues initialValues = new ContentValues();
		
		initialValues.put(KEY_EVENT_ID, event.getEventID());
		initialValues.put(KEY_FEATURED, event.isFeatured());
		
		initialValues.put(KEY_NAME, event.getName());
		initialValues.put(KEY_DESCRIPTION_HEADER, event.getDescriptionHeader());
		initialValues.put(KEY_DESCRIPTION_BODY, event.getDescriptionBody());
		
		initialValues.put(KEY_START_DATE, event.getStartDate());
		initialValues.put(KEY_END_DATE, event.getEndDate());
		
		initialValues.put(KEY_LOCATION_ID, event.getLocationID());
		
		if(containsLocation(event.getLocationID())) insertLocation(event);
		
		initialValues.put(KEY_ACCESSIBILITY_INFORMATION, event.getAccessibilityInformation());
		
		initialValues.put(KEY_CATEGORY, event.getCategoryTags().get(0));
		
		initialValues.put(KEY_IMAGE_URL, event.getImageURL());
		
		return db.insert(EVENT_TABLE, null, initialValues);
	}
	
	public long insertLocation(Event event)
	{
		ContentValues initialValues = new ContentValues();
		
		initialValues.put(KEY_LOCATION_ID, event.getLocationID());
		initialValues.put(KEY_ADDRESS_1, event.getAddress1());
		initialValues.put(KEY_ADDRESS_2, event.getAddress2());
		initialValues.put(KEY_CITY, event.getCity());
		initialValues.put(KEY_POSTCODE, event.getPostcode());
		initialValues.put(KEY_LATITUDE, event.getLatitude());
		initialValues.put(KEY_LONGITUDE, event.getLongitude());
		
		return db.insert(LOCATION_TABLE, null, initialValues);
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
		return db.query(LOCATION_TABLE, new String[] {KEY_LOCATION_ID},
				KEY_LOCATION_ID + "=" + locationID, null, null, null, null).getCount() == 0;
	}

	public Cursor getEventSummaries()
	{
		return db.query(EVENT_TABLE, new String[] { KEY_EVENT_ID, KEY_NAME, KEY_START_DATE,
				KEY_END_DATE, KEY_DESCRIPTION_HEADER }, null, null, null, null, null);
	}
	
	public Cursor getEvent(long eventID)
	{
		return db.query(LOCATION_TABLE, null, KEY_EVENT_ID + "=" + eventID,
				null, null, null, null);
	}
	
	public Cursor getLocation(long locationID)
	{
		return db.query(LOCATION_TABLE, null, KEY_LOCATION_ID + "=" + locationID,
				null, null, null, null);
	}
}
