package uk.ac.dur.duchess.data;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import uk.ac.dur.duchess.Event;
import uk.ac.dur.duchess.User;
import android.util.Log;

public class UserFunctions
{
	public static String getUserXML(User user)
	{
		StringBuffer xml = new StringBuffer();
		xml.append("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n");
		xml.append("<user>");
		xml.append("<forename>" + user.getForename() + "</forename>");
		xml.append("<surname>" + user.getSurname() + "</surname>");
		xml.append("<password>" + user.getPassword() + "</password>");
		xml.append("<emailAddress>" + user.getEmailAddress() + "</emailAddress>");

		xml.append("<dateJoined>");

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		xml.append(sdf.format(Calendar.getInstance().getTime()));

		xml.append("</dateJoined>");
		xml.append("<department>" + user.getDepartment() + "</department>");
		xml.append("<college>" + user.getCollege() + "</college>");

		List<String> v = user.getCategoryPreferences();

		xml.append("<preferences>");

		for (int i = 0; i < v.size(); i++)
		{
			String category = v.get(i);
			xml.append("<category id=\"" + getCategoryID(category) + "\">" + category
					+ "</category>");
		}

		xml.append("</preferences>");
		xml.append("</user>");

		return xml.toString();
	}

	private static int getCategoryID(String category)
	{
		if (category.equals("University")) return 1;
		if (category.equals("College")) return 2;
		if (category.equals("Theatre")) return 3;
		if (category.equals("Music")) return 4;
		if (category.equals("Exhibitions")) return 5;
		if (category.equals("Conferences")) return 6;
		if (category.equals("Community")) return 7;
		if (category.equals("Sport")) return 8;
		return 1;
	}

	public static ArrayList<Event> filterByPreferences(User user, ArrayList<Event> eventList)
	{
		ArrayList<Event> newList = new ArrayList<Event>();
		ArrayList<String> preferences = (ArrayList<String>) user.getCategoryPreferences();

		Log.d("PREFERENCES", preferences.toString());
		
		for (Event e : eventList)
		{
			List<String> eventCategories = e.getCategoryTags();
			if (preferences.contains(e.getCategoryTags().get(0))) newList.add(e);
		}
		
		eventList.clear();
		
		for (Event e : newList)
		{
			eventList.add(e);
		}

		return newList;
	}
	
	public static String md5(String in) {
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("MD5");
            digest.reset();
            digest.update(in.getBytes());
            byte[] a = digest.digest();
            int len = a.length;
            StringBuilder sb = new StringBuilder(len << 1);
            for (int i = 0; i < len; i++) {
                sb.append(Character.forDigit((a[i] & 0xf0) >> 4, 16));
                sb.append(Character.forDigit(a[i] & 0x0f, 16));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) { e.printStackTrace(); }
        return null;
    }

}
