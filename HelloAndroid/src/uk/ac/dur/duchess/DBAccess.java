package uk.ac.dur.duchess;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBAccess
{
	public static final String KEY_ROWID = "eventID";
	public static final String KEY_NAME = "name";
	public static final String KEY_START_DATE = "startDate";
	public static final String KEY_END_DATE = "endDate";
	public static final String KEY_DESCRIPTION_HEADER = "descriptionHeader";

	private static final String TAG = "DBAdapter";

	private static final String DATABASE_NAME = "duchessDB";
	private static final String EVENT_TABLE = "events";
	private static final int DATABASE_VERSION = 1;

	private static final String DATABASE_CREATE_STATEMENT = "CREATE TABLE events("
			+ "eventID INTEGER PRIMARY KEY AUTOINCREMENT, " + "name TEXT NOT NULL, "
			+ "startDate DATE NOT NULL, " + "endDate DATE NOT NULL, "
			+ "descriptionHeader TEXT NOT NULL);";

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
		initialValues.put(KEY_NAME, event.getName());
		initialValues.put(KEY_DESCRIPTION_HEADER, event.getDescriptionHeader());
		initialValues.put(KEY_START_DATE, event.getStartDate());
		initialValues.put(KEY_END_DATE, event.getEndDate());
		return db.insert(EVENT_TABLE, null, initialValues);
	}

	public boolean deleteEvent(long eventID)
	{
		return db.delete(EVENT_TABLE, KEY_ROWID + "=" + eventID, null) > 0;
	}

	public Cursor getAllEvents()
	{
		return db.query(EVENT_TABLE, new String[] { KEY_ROWID, KEY_NAME, KEY_START_DATE,
				KEY_END_DATE, KEY_DESCRIPTION_HEADER }, null, null, null, null, null);
	}
}
