package uk.ac.dur.duchess.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class User
{
	private long userID;
	private String forename;
	private String surname;
	private String password;
	private String emailAddress;
	private String dateJoined;
	private DurhamAffiliation affiliation;
	private String department;
	private List<String> colleges = new ArrayList<String>();
	private List<String> categoryPreferences;
	private Map<Long, String> subcategoryPreferences;
	private List<String> societies;
	private List<Long> events = new ArrayList<Long>();
	
	public List<Long> getEvents() { return events; }
	
	public void setEvents(List<Long> events) { this.events = events; }
	
	public void addEvent(Long i) { events.add(i); }
	
	public void removeEvent(Long i) { events.remove(i); }
	
	public boolean hasPinnedEvent(Long i) { return events.contains(i); }
	
	public boolean hasAnyBookmarkedEvents() { return events.size() > 0; }
	
	public DurhamAffiliation getAffiliation()
	{
		return affiliation;
	}
	
	public void setAffiliation(DurhamAffiliation affiliation)
	{
		this.affiliation = affiliation;
	}
	
	public void setAffiliation(String affiliation)
	{
		this.affiliation = DurhamAffiliation.parseScope(affiliation);
	}
	
	public boolean isAffiliated() { return affiliation != DurhamAffiliation.VISITOR; }

	public List<String> getSocieties()
	{
		return societies;
	}

	public void setSocieties(List<String> societies)
	{
		this.societies = societies;
	}
	
	public boolean isSubscribedToSociety(String societyName) { return societies.contains(societyName); }

	public long getUserID()
	{
		return userID;
	}

	public void setUserID(long id)
	{
		this.userID = id;
	}

	public String getForename()
	{
		return forename;
	}

	public void setForename(String forename)
	{
		this.forename = forename;
	}

	public String getSurname()
	{
		return surname;
	}

	public void setSurname(String surname)
	{
		this.surname = surname;
	}

	public String getPassword()
	{
		return password;
	}

	public void setPassword(String password)
	{
		this.password = password;
	}

	public String getEmailAddress()
	{
		return emailAddress;
	}

	public void setEmailAddress(String emailAddress)
	{
		this.emailAddress = emailAddress;
	}

	public String getDateJoined()
	{
		return dateJoined;
	}

	public void setDateJoined(String dateJoined)
	{
		this.dateJoined = dateJoined;
	}

	public String getDepartment()
	{
		return department;
	}

	public void setDepartment(String department)
	{
		this.department = department;
	}

	public String getCollege()
	{
		return colleges.get(0);
	}

	public void setCollege(String college)
	{
		colleges.clear();
		colleges.add(college);
	}
	
	public List<String> getColleges()
	{
		return colleges;
	}
	
	public void setColleges(List<String> colleges)
	{
		this.colleges = colleges;
	}

	public List<String> getCategoryPreferences()
	{
		return categoryPreferences;
	}

	public void setCategoryPreferences(List<String> categoryPreferences)
	{
		this.categoryPreferences = categoryPreferences;
	}

	public Map<Long, String> getSubcategoryPreferences()
	{
		return subcategoryPreferences;
	}

	public void setSubcategoryPreferences(Map<Long, String> subcategoryPreferences)
	{
		this.subcategoryPreferences = subcategoryPreferences;
	}
}
