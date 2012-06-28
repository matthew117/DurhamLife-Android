package uk.ac.dur.duchess;

import java.util.HashMap;
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
	private boolean isLinkedAccount = false;
	
	private User user;
	
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
		}
		else if (localName.equalsIgnoreCase("forename")) isForename = true;
		else if (localName.equalsIgnoreCase("surname")) isSurname= true;
		
		else if (localName.equalsIgnoreCase("password")) isPassword = true;
		
		else if (localName.equalsIgnoreCase("dateJoined")) isDateJoined = true;
		
		else if (localName.equalsIgnoreCase("department")) isDepartment = true;
		else if (localName.equalsIgnoreCase("college")) isCollege = true;
		else if (localName.equalsIgnoreCase("linkedAccount")) isLinkedAccount = true;
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
		else if (localName.equalsIgnoreCase("linkedAccount")) isLinkedAccount = false;
	}

	@Override
	public void characters(char[] ch, int start, int length)
	{		
		if      (isForename) user.setForename(new String(ch, start, length));
		else if (isSurname) user.setSurname(new String(ch, start, length));
		else if (isPassword) user.setPassword(new String(ch, start, length));
		else if (isDateJoined) user.setDateJoined(new String(ch, start, length));
		else if (isDepartment) user.setDepartmentID(Long.parseLong(new String(ch, start, length)));
		else if (isCollege) user.setCollegeID(Long.parseLong(new String(ch, start, length)));
		else if (isLinkedAccount) user.setEmailAddress(new String(ch, start, length));
	}
	
	@Override
	public void endDocument()
	{
		
	}

	
}
