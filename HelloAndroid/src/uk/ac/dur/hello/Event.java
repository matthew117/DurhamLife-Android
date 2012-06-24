package uk.ac.dur.hello;

import java.util.HashMap;

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
	
	private String contactTelephoneNumber;
	private String contactEmailAddress;
	private String webAddress;
	
	private String address1;
	private String address2;
	private String city;
	private String postcode;
	private String latitude;
	private String longitude;
	
	private String accessibilityInformation;
	
	private HashMap<Long, String> categoryTags;
	private HashMap<Long, String> subcategoryTags;
	
	private String imageURL;
	
	public long getEventID() {
		return eventID;
	}
	public void setEventID(long eventID) {
		this.eventID = eventID;
	}
	public String getDescriptionBody() {
		return descriptionBody;
	}
	public void setDescriptionBody(String descriptionBody) {
		this.descriptionBody = descriptionBody;
	}
	public String getContactTelephoneNumber() {
		return contactTelephoneNumber;
	}
	public void setContactTelephoneNumber(String contactTelephoneNumber) {
		this.contactTelephoneNumber = contactTelephoneNumber;
	}
	public String getContactEmailAddress() {
		return contactEmailAddress;
	}
	public void setContactEmailAddress(String contactEmailAddress) {
		this.contactEmailAddress = contactEmailAddress;
	}
	public String getWebAddress() {
		return webAddress;
	}
	public void setWebAddress(String webAddress) {
		this.webAddress = webAddress;
	}
	public String getAddress1() {
		return address1;
	}
	public void setAddress1(String address1) {
		this.address1 = address1;
	}
	public String getAddress2() {
		return address2;
	}
	public void setAddress2(String address2) {
		this.address2 = address2;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getPostcode() {
		return postcode;
	}
	public void setPostcode(String postcode) {
		this.postcode = postcode;
	}
	public String getLatitude() {
		return latitude;
	}
	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}
	public String getLongitude() {
		return longitude;
	}
	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}
	public String getAccessibilityInformation() {
		return accessibilityInformation;
	}
	public void setAccessibilityInformation(String accessibilityInformation) {
		this.accessibilityInformation = accessibilityInformation;
	}
	public HashMap<Long, String> getCategoryTags() {
		return categoryTags;
	}
	public void setCategoryTags(HashMap<Long, String> categoryTags) {
		this.categoryTags = categoryTags;
	}
	public HashMap<Long, String> getSubcategoryTags() {
		return subcategoryTags;
	}
	public void setSubcategoryTags(HashMap<Long, String> subcategoryTags) {
		this.subcategoryTags = subcategoryTags;
	}
	public String getImageURL() {
		return imageURL;
	}
	public void setImageURL(String imageURL) {
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
	
}
