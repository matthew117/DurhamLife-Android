package uk.ac.dur.duchess;

import java.util.Map;

public class User
{
	private long id;
	private String forename;
	private String surname;
	private String password;
	private String emailAddress;
	private String dateJoined;
	private long departmentID;
	private long collegeID;
	private Map<Long, String> categoryPreferences;
	private Map<Long, String> subcategoryPreferences;

	public long getId()
	{
		return id;
	}

	public void setId(long id)
	{
		this.id = id;
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

	public long getDepartmentID()
	{
		return departmentID;
	}

	public void setDepartmentID(long departmentID)
	{
		this.departmentID = departmentID;
	}

	public long getCollegeID()
	{
		return collegeID;
	}

	public void setCollegeID(long collegeID)
	{
		this.collegeID = collegeID;
	}

	public Map<Long, String> getCategoryPreferences()
	{
		return categoryPreferences;
	}

	public void setCategoryPreferences(Map<Long, String> categoryPreferences)
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
