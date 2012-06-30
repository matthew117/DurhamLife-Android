package uk.ac.dur.duchess.data;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import uk.ac.dur.duchess.Event;
import uk.ac.dur.duchess.User;

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
		sdf.format(Calendar.getInstance(), xml, null);
		
		xml.append("</dateJoined>");
		xml.append("<department>" + user.getDepartment() + "</department>");
		xml.append("<college>" + user.getCollege() + "</college>");
		
		List<String> v = user.getCategoryPreferences();
		
		xml.append("<preferences>");
		
		for (int i = 0; i < v.size(); i++)
		{
			String category = (String) v.get(i);
			xml.append("<category id=\"" + getCategoryID(category) + "\">" + category + "</category>");
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
	
	public static List<Event> filterByPreferences(User user, List<Event> eventList)
	{
		List<Event> newList = new ArrayList<Event>();
		List<String> preferences = user.getCategoryPreferences();
		
		for(Event e : eventList)
			if(preferences.contains(e.getCategoryTags().get(0)))
				newList.add(e);
		
		return newList;
	}

}
