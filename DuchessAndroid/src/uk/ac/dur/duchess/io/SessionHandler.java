package uk.ac.dur.duchess.io;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import uk.ac.dur.duchess.model.User;
import android.app.Activity;
import android.content.SharedPreferences;

public class SessionHandler
{
	private static final String USER_ID_KEY = "userID";
	private static final String FORENAME_KEY = "forename";
	private static final String SURNAME_KEY = "surname";
	private static final String PASSWORD_KEY = "password";
	private static final String EMAIL_ADDRESS_KEY = "emailAddress";
	private static final String DATE_JOINED_KEY = "dateJoined";
	private static final String AFFILIATION_KEY = "affiliation";
	private static final String DEPARTMENT_KEY = "department";
	private static final String COLLEGES_KEY = "colleges";
	private static final String PREFERENCES_KEY = "categoryPreferences";
	private static final String SOCIETIES_KEY = "societies";
	private static final String EVENTS_KEY = "events";
	
	private static Map<Integer, String> categoryMap = new HashMap<Integer, String>();
	
	static
	{
		categoryMap.put(0, "University");
		categoryMap.put(1, "College");
		categoryMap.put(2, "Music");
		categoryMap.put(3, "Theatre");
		categoryMap.put(4, "Exhibitions");
		categoryMap.put(5, "Sport");
		categoryMap.put(6, "Conferences");
		categoryMap.put(7, "Community");
	}
	
	/**
	 * Called when the user successfully authenticates to create a persistent
	 * user session.
	 * 
	 * @param user
	 *            - the user object representing the user to log in.
	 */
	public static void saveUserPreferences(Activity activity, User user)
	{
		SharedPreferences prefs = activity.getSharedPreferences("UserSession", Activity.MODE_PRIVATE);
		SharedPreferences.Editor editor = prefs.edit();
		
		editor.putLong(USER_ID_KEY, user.getUserID());
		editor.putString(FORENAME_KEY, user.getForename());
		editor.putString(SURNAME_KEY, user.getSurname());
		editor.putString(PASSWORD_KEY, user.getPassword());
		editor.putString(EMAIL_ADDRESS_KEY, user.getEmailAddress());
		editor.putString(DATE_JOINED_KEY, user.getDateJoined());
		editor.putString(AFFILIATION_KEY, (user.getAffiliation() != null) ? user.getAffiliation().name() : "VISITOR");
		editor.putString(DEPARTMENT_KEY, user.getDepartment());
		editor.putString(COLLEGES_KEY, user.getColleges().toString());
		editor.putString(PREFERENCES_KEY, getPreferencesBitString(user.getCategoryPreferences()));
		editor.putString(SOCIETIES_KEY, user.getSocieties().toString());
		editor.putString(EVENTS_KEY, user.getEvents().toString());
		
		editor.commit();
	}
	
	private static String getPreferencesBitString(List<String> categoryPreferences)
	{
		String[] categories = {"University", "College", "Music", "Theatre",
				"Exhibitions", "Sport", "Conferences", "Community"};
		
		char[] preferences = {'0', '0', '0', '0', '0', '0', '0', '0'};
		
		for(int i = 0; i < categories.length; i++)
			if(categoryPreferences.contains(categories[i])) preferences[i] = '1';
		
		return new String(preferences);
	}
	
	private static List<String> getPreferencesFromBitString(String preferences)
	{
		List<String> categoryPreferences = new ArrayList<String>();
		
		for(int i = 0; i < preferences.length(); i++)
			if(preferences.charAt(i) == '1') categoryPreferences.add(categoryMap.get(i));
		
		return categoryPreferences;
	}

	/**
	 * Uses by parts of the application that require information about the
	 * current user.
	 * 
	 * @return a user object representing the current session or
	 *         <code>null</code> if no user is currently signed in.
	 */
	public static User getCurrentUser(Activity activity)
	{
		User user = new User();
		
		SharedPreferences prefs = activity.getSharedPreferences("UserSession", Activity.MODE_PRIVATE);
		
		user.setUserID(prefs.getLong(USER_ID_KEY, -1));
		user.setForename(prefs.getString(FORENAME_KEY, "Guest"));
		user.setSurname(prefs.getString(SURNAME_KEY, ""));
		user.setPassword(prefs.getString(PASSWORD_KEY, ""));
		user.setEmailAddress(prefs.getString(EMAIL_ADDRESS_KEY, ""));
		user.setDateJoined(prefs.getString(DATE_JOINED_KEY, ""));
		user.setAffiliation(prefs.getString(AFFILIATION_KEY, "VISITOR"));
		user.setDepartment(prefs.getString(DEPARTMENT_KEY, ""));
		user.setColleges(getCollegesFromString(prefs.getString(COLLEGES_KEY, "")));
		user.setCategoryPreferences(getPreferencesFromBitString(prefs.getString(PREFERENCES_KEY, "11111111")));
		user.setSocieties(getSocietiesFromString(prefs.getString(SOCIETIES_KEY, "")));
		user.setEvents(getEventsFromString(prefs.getString(EVENTS_KEY, "")));
		
		return user;
	}

	/**
	 * Called when the user logs out to end their session and remove their
	 * personal information.
	 */
	public static void endUserSession(Activity activity)
	{
		SharedPreferences prefs = activity.getSharedPreferences("UserSession", Activity.MODE_PRIVATE);
		SharedPreferences.Editor editor = prefs.edit();
		
		editor.clear();
		editor.commit();
	}
	
	private static List<String> getSocietiesFromString(String s)
	{
		if (s.equals("") || s.equals("[]"))
		{
			return new ArrayList<String>();
		}
		else
		{
			List<String> societies = new ArrayList<String>();
			String[] sa = s.substring(1,s.length()-1).split(", ");
			
			for (String society : sa)
			{
				societies.add(society);
			}
			
			return societies;
		}
	}
	
	private static List<Long> getEventsFromString(String s)
	{
		if (s.equals("") || s.equals("[]"))
		{
			return new ArrayList<Long>();
		}
		else
		{
			List<Long> events = new ArrayList<Long>();
			String[] sa = s.substring(1,s.length()-1).split(", ");
			
			for (String event : sa)
			{
				events.add(Long.parseLong(event));
			}
			
			return events;
		}
	}
	
	public static List<String> getCollegesFromString(String s)
	{
		if (s.equals("") || s.equals("[]"))
		{
			return new ArrayList<String>();
		}
		else
		{
			List<String> colleges = new ArrayList<String>();
			String[] sa = s.substring(1, s.length() - 1).split(", ");
			
			for(String str : sa) colleges.add(str);
			
			return colleges;
		}
	}
}
