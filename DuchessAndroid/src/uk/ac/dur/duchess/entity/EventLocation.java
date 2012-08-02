package uk.ac.dur.duchess.entity;

public class EventLocation
{
	private long locationID;
	private String address1;
	private String address2;
	private String city;
	private String postcode;
	private String latitude;
	private String longitude;
	
	public EventLocation() {}
	
	public long getLocationID()
	{
		return locationID;
	}

	public void setLocationID(long locationID)
	{
		this.locationID = locationID;
	}
	
	public String getAddress1()
	{
		return address1;
	}

	public void setAddress1(String address1)
	{
		this.address1 = address1;
	}

	public String getAddress2()
	{
		return address2;
	}

	public void setAddress2(String address2)
	{
		this.address2 = address2;
	}

	public String getCity()
	{
		return city;
	}

	public void setCity(String city)
	{
		this.city = city;
	}

	public String getPostcode()
	{
		return postcode;
	}

	public void setPostcode(String postcode)
	{
		this.postcode = postcode;
	}

	public String getLatitude()
	{
		return latitude;
	}

	public void setLatitude(String latitude)
	{
		this.latitude = latitude;
	}

	public String getLongitude()
	{
		return longitude;
	}

	public void setLongitude(String longitude)
	{
		this.longitude = longitude;
	}
}
