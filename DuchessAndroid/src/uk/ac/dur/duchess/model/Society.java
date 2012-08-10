package uk.ac.dur.duchess.model;

public class Society
{
	private long societyID;
	private String name;
	private String website;
	private String email;
	private String constitution;
	
	public long getSocietyID()
	{
		return societyID;
	}
	public void setSocietyID(long societyID)
	{
		this.societyID = societyID;
	}
	public String getName()
	{
		return name;
	}
	public void setName(String name)
	{
		this.name = name;
	}
	public String getWebsite()
	{
		return website;
	}
	public void setWebsite(String website)
	{
		this.website = website;
	}
	public String getEmail()
	{
		return email;
	}
	public void setEmail(String email)
	{
		this.email = email;
	}
	public String getConstitution()
	{
		return constitution;
	}
	public void setConstitution(String constitution)
	{
		this.constitution = constitution;
	}
	@Override
	public String toString()
	{
		return this.name;
	}
}
