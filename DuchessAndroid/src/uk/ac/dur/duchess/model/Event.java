package uk.ac.dur.duchess.model;

import java.util.Arrays;
import java.util.List;

public class Event
{
	public Event() {}

	private long eventID;
	private boolean isFeatured;

	private String name;
	private String descriptionHeader;
	private String descriptionBody;

	private String startDate;
	private String endDate;
	private String iCalURL;
	
	private EventLocation location;
	
	private EventScope scope;
	private String associatedCollege;
	private String associatedSociety;

	private String contactTelephoneNumber;
	private String contactEmailAddress;
	private String webAddress;

	private String accessibilityInformation;

	private List<String> categoryTags;
	
	private String adImageURL;
	private String imageURL;
	
	private int reviewScore;
	private int numberOfReviews;
	
	public String getICalURL()
	{
		return iCalURL;
	}
	
	public void setICalURL(String iCalURL)
	{
		this.iCalURL = iCalURL;
	}

	public long getEventID()
	{
		return eventID;
	}

	public void setEventID(long eventID)
	{
		this.eventID = eventID;
	}

	public String getDescriptionBody()
	{
		return descriptionBody;
	}

	public void setDescriptionBody(String descriptionBody)
	{
		this.descriptionBody = descriptionBody;
	}
	
	public EventScope getScope()
	{
		return scope;
	}
	
	public void setScope(EventScope scope)
	{
		this.scope = scope;
	}
	
	public void setScope(String scope)
	{
		this.scope = EventScope.parseScope(scope);
	}

	public String getContactTelephoneNumber()
	{
		return contactTelephoneNumber;
	}

	public void setContactTelephoneNumber(String contactTelephoneNumber)
	{
		this.contactTelephoneNumber = contactTelephoneNumber;
	}

	public String getContactEmailAddress()
	{
		return contactEmailAddress;
	}

	public void setContactEmailAddress(String contactEmailAddress)
	{
		this.contactEmailAddress = contactEmailAddress;
	}

	public String getWebAddress()
	{
		return webAddress;
	}

	public void setWebAddress(String webAddress)
	{
		this.webAddress = webAddress;
	}

	public String getAccessibilityInformation()
	{
		return accessibilityInformation;
	}

	public void setAccessibilityInformation(String accessibilityInformation)
	{
		this.accessibilityInformation = accessibilityInformation;
	}

	public List<String> getCategoryTags()
	{
		return categoryTags;
	}

	public void setCategoryTags(List<String> categoryTags)
	{
		this.categoryTags = categoryTags;
	}
	
	public void setCategoryTags(String categories)
	{
		categories = categories.substring(1, categories.length() - 1);
		categories = categories.replaceAll(", ", ",");
		
		categoryTags = Arrays.asList(categories.split(","));
	}

	public String getImageURL()
	{
		return imageURL;
	}

	public void setImageURL(String imageURL)
	{
		this.imageURL = imageURL;
	}

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
	
	public String getAdImageURL()
	{
		return adImageURL;
	}

	public void setAdImageURL(String adImageURL)
	{
		this.adImageURL = adImageURL;
	}

	public int getReviewScore()
	{
		return reviewScore;
	}

	public void setReviewScore(int reviewScore)
	{
		this.reviewScore = reviewScore;
	}

	public int getNumberOfReviews()
	{
		return numberOfReviews;
	}

	public void setNumberOfReviews(int numberOfReviews)
	{
		this.numberOfReviews = numberOfReviews;
	}

	public String getAssociatedCollege() {
		return associatedCollege;
	}

	public void setAssociatedCollege(String associatedCollege) {
		this.associatedCollege = associatedCollege;
	}

	public String getAssociatedSociety() {
		return associatedSociety;
	}

	public void setAssociatedSociety(String associatedSociety) {
		this.associatedSociety = associatedSociety;
	}

	public EventLocation getLocation() {
		return location;
	}

	public void setLocation(EventLocation location) {
		this.location = location;
	}

}
