package uk.ac.dur.hello;

public class Event
{
	public Event() {}
	
	private String name;
	private String descriptionHeader;
	private String startDate;
	private String endDate;
	private boolean isFeatured;
	
	public String getName()
	{
		return name;
	}
	public void setName(String name)
	{
		this.name = name;
	}
	public String getDescriptionHeader()
	{
		return descriptionHeader;
	}
	public void setDescriptionHeader(String descriptionHeader)
	{
		this.descriptionHeader = descriptionHeader;
	}
	public String getStartDate()
	{
		return startDate;
	}
	public void setStartDate(String startDate)
	{
		this.startDate = startDate;
	}
	public String getEndDate()
	{
		return endDate;
	}
	public void setEndDate(String endDate)
	{
		this.endDate = endDate;
	}
	
	public boolean isFeatured()
	{
		return isFeatured;
	}
	
	public void setFeatured(boolean isFeatured)
	{
		this.isFeatured = isFeatured;
	}
	
}
