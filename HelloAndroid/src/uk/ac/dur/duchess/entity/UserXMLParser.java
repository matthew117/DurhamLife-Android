package uk.ac.dur.duchess.entity;

import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

public class UserXMLParser extends DefaultHandler
{
	private boolean isForename = false;
	private boolean isSurname = false;
	
	private boolean isPassword = false;
	
	private boolean isDateJoined = false;
	
	private boolean isDepartment = false;
	private boolean isCollege = false;
	private boolean isEmail = false;
	
	private boolean isCategory = false;
	private boolean isSociety = false;
	
	private User user;
	private List<String> preferences;
	private List<String> societies;
	
	public UserXMLParser(User user)
	{
		this.user = user;
	}

	@Override
	public void startElement(String URI, String localName, String qName, Attributes attributes)
	{
		if (localName.equalsIgnoreCase("user"))
		{
			String id = attributes.getValue("id");
			long userID = (id != null) ? Long.parseLong(id) : -1;
			if(userID != -1) user.setUserID(userID);
			
			preferences = new ArrayList<String>();
			societies = new ArrayList<String>();
		}
		else if (localName.equalsIgnoreCase("forename")) isForename = true;
		else if (localName.equalsIgnoreCase("surname")) isSurname= true;
		
		else if (localName.equalsIgnoreCase("password")) isPassword = true;
		
		else if (localName.equalsIgnoreCase("dateJoined")) isDateJoined = true;
		
		else if (localName.equalsIgnoreCase("department")) isDepartment = true;
		else if (localName.equalsIgnoreCase("college")) isCollege = true;
		else if (localName.equalsIgnoreCase("emailAddress")) isEmail = true;
		
		else if (localName.equalsIgnoreCase("category")) isCategory = true;
		
		else if (localName.equalsIgnoreCase("society")) isSociety = true;
	}

	@Override
	public void endElement(String URI, String localName, String qName)
	{		
		if      (localName.equalsIgnoreCase("forename")) isForename = false;
		else if (localName.equalsIgnoreCase("surname")) isSurname= false;
		
		else if (localName.equalsIgnoreCase("password")) isPassword = false;
		
		else if (localName.equalsIgnoreCase("dateJoined")) isDateJoined = false;
		
		else if (localName.equalsIgnoreCase("department")) isDepartment = false;
		else if (localName.equalsIgnoreCase("college")) isCollege = false;
		else if (localName.equalsIgnoreCase("emailAddress")) isEmail = false;
		
		else if (localName.equalsIgnoreCase("category")) isCategory = false;
		
		else if (localName.equalsIgnoreCase("preferences"))
		{
			user.setCategoryPreferences(preferences);
		}
		
		else if (localName.equalsIgnoreCase("society")) isSociety = false;
		
		else if (localName.equalsIgnoreCase("societies"))
		{
			user.setSocieties(societies);
		}
	}

	@Override
	public void characters(char[] ch, int start, int length)
	{		
		if      (isForename) user.setForename(new String(ch, start, length));
		else if (isSurname) user.setSurname(new String(ch, start, length));
		else if (isPassword) user.setPassword(new String(ch, start, length));
		else if (isDateJoined) user.setDateJoined(new String(ch, start, length));
		else if (isDepartment) user.setDepartment(new String(ch, start, length));
		else if (isCollege) user.setCollege(new String(ch, start, length));
		else if (isEmail) user.setEmailAddress(new String(ch, start, length));
		else if (isSociety) societies.add(new String(ch, start, length));
		else if (isCategory) preferences.add(new String(ch, start, length));
	}
	
	@Override
	public void endDocument()
	{
		user.setCategoryPreferences(preferences);
	}
	
}
