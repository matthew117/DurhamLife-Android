package uk.ac.dur.duchess;

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
	private String department;
	private String college;
	private List<String> categoryPreferences;
	private Map<Long, String> subcategoryPreferences;

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
		return college;
	}

	public void setCollege(String college)
	{
		this.college = college;
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
