package uk.ac.dur.duchess.entity;

import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import android.util.Log;

public class EventXMLParser extends DefaultHandler
{
	private boolean isName = false;
	private boolean isDescriptionHeader = false;
	private boolean isDescriptionBody = false;

	private boolean isStartDate = false;
	private boolean isEndDate = false;
	private boolean isICalURL = false;
	
	private boolean isAssociatedCollege = false;
	private boolean isEventScope = false;
	private boolean isAssociatedSociety = false;

	private boolean isContactTelephoneNumber = false;
	private boolean isContactEmailAddress = false;
	private boolean isWebAddress = false;

	private boolean isAddress1 = false;
	private boolean isAddress2 = false;
	private boolean isCity = false;
	private boolean isPostcode = false;
	private boolean isLatitude = false;
	private boolean isLongitude = false;

	private boolean isCategory = false;

	private boolean isAccessibilityInformation = false;

	private boolean isImageURL = false;
	private boolean isAdImageURL = false;

	private boolean isReviewScore = false;

	private List<Event> list;

	private Event event;
	private EventLocation location;
	private List<String> categoryTags;

	private long tagID = -1;

	public EventXMLParser(List<Event> events)
	{
		list = events;
		event = new Event();
		location = new EventLocation();
		categoryTags = new ArrayList<String>();
	}

	@Override
	public void startElement(String URI, String localName, String qName, Attributes attributes)
	{
		if (localName.equalsIgnoreCase("event"))
		{
			Log.d("EventXMLParser", "Parser sees event Tag");
			event = new Event();
			location = new EventLocation();
			categoryTags = new ArrayList<String>();

			String isFeatured = attributes.getValue("featured");
			event.setFeatured(isFeatured != null && isFeatured.equalsIgnoreCase("true"));

			String id = attributes.getValue("id");
			long eventID = (id != null) ? Long.parseLong(id) : -1;
			if (eventID != -1) event.setEventID(eventID);
		}
		else if (localName.equalsIgnoreCase("name")) isName = true;
		else if (localName.equalsIgnoreCase("descriptionHeader")) isDescriptionHeader = true;
		else if (localName.equalsIgnoreCase("descriptionBody")) isDescriptionBody = true;

		else if (localName.equalsIgnoreCase("startDate")) isStartDate = true;
		else if (localName.equalsIgnoreCase("endDate")) isEndDate = true;
		else if (localName.equalsIgnoreCase("iCalURL")) isICalURL = true;
		
		else if (localName.equalsIgnoreCase("associatedCollege")) isAssociatedCollege = true;
		else if (localName.equalsIgnoreCase("eventScope")) isEventScope = true;
		else if (localName.equalsIgnoreCase("associatedSociety")) isAssociatedSociety = true;
		
		else if (localName.equalsIgnoreCase("contactTelephoneNumber")) isContactTelephoneNumber = true;
		else if (localName.equalsIgnoreCase("contactEmailAddress")) isContactEmailAddress = true;
		else if (localName.equalsIgnoreCase("webAddress")) isWebAddress = true;

		else if (localName.equalsIgnoreCase("location"))
		{
			String id = attributes.getValue("id");
			long locationID = (id != null) ? Long.parseLong(id) : -1;
			if (locationID != -1) location.setLocationID(locationID);
		}
		else if (localName.equalsIgnoreCase("address1")) isAddress1 = true;
		else if (localName.equalsIgnoreCase("address2")) isAddress2 = true;
		else if (localName.equalsIgnoreCase("city")) isCity = true;
		else if (localName.equalsIgnoreCase("postcode")) isPostcode = true;
		else if (localName.equalsIgnoreCase("latitude")) isLatitude = true;
		else if (localName.equalsIgnoreCase("longitude")) isLongitude = true;

		else if (localName.equalsIgnoreCase("category"))
		{
			String id = attributes.getValue("id");
			tagID = (id != null) ? Long.parseLong(id) : -1;

			isCategory = true;
		}

		else if (localName.equalsIgnoreCase("accessibilityInformation")) isAccessibilityInformation = true;

		else if (localName.equalsIgnoreCase("imageURL")) isImageURL = true;
		else if (localName.equalsIgnoreCase("adImageURL")) isAdImageURL = true;
		else if (localName.equalsIgnoreCase("reviewScore"))
		{
			isReviewScore = true;
			event.setNumberOfReviews(Integer.parseInt(attributes.getValue("n")));
		}
	}

	@Override
	public void endElement(String URI, String localName, String qName)
	{
		if (localName.equalsIgnoreCase("name")) isName = false;
		else if (localName.equalsIgnoreCase("descriptionHeader")) isDescriptionHeader = false;
		else if (localName.equalsIgnoreCase("descriptionBody")) isDescriptionBody = false;

		else if (localName.equalsIgnoreCase("startDate")) isStartDate = false;
		else if (localName.equalsIgnoreCase("endDate")) isEndDate = false;
		else if (localName.equalsIgnoreCase("iCalURL")) isICalURL = false;
		
		else if (localName.equalsIgnoreCase("associatedCollege")) isAssociatedCollege = false;
		else if (localName.equalsIgnoreCase("eventScope")) isEventScope = false;
		else if (localName.equalsIgnoreCase("associatedSociety")) isAssociatedSociety = false;

		else if (localName.equalsIgnoreCase("contactTelephoneNumber")) isContactTelephoneNumber = false;
		else if (localName.equalsIgnoreCase("contactEmailAddress")) isContactEmailAddress = false;
		else if (localName.equalsIgnoreCase("webAddress")) isWebAddress = false;

		else if (localName.equalsIgnoreCase("address1")) isAddress1 = false;
		else if (localName.equalsIgnoreCase("address2")) isAddress2 = false;
		else if (localName.equalsIgnoreCase("city")) isCity = false;
		else if (localName.equalsIgnoreCase("postcode")) isPostcode = false;
		else if (localName.equalsIgnoreCase("latitude")) isLatitude = false;
		else if (localName.equalsIgnoreCase("longitude")) isLongitude = false;

		else if (localName.equalsIgnoreCase("category")) isCategory = false;

		else if (localName.equalsIgnoreCase("accessibilityInformation")) isAccessibilityInformation = false;

		else if (localName.equalsIgnoreCase("imageURL")) isImageURL = false;
		else if (localName.equalsIgnoreCase("adImageURL")) isAdImageURL = false;
		else if (localName.equalsIgnoreCase("reviewScore")) isReviewScore = false;
		else if (localName.equalsIgnoreCase("event"))
		{
			event.setCategoryTags(categoryTags);
			event.setLocation(location);
			Log.d("EventXMLParser", "About to add to list.");
			Log.d("EventXMLParser", "event.getName() => " + event.getName());
			list.add(event);
		}
	}

	@Override
	public void characters(char[] ch, int start, int length)
	{
		if (isName) event.setName(new String(ch, start, length));
		else if (isDescriptionHeader) event.setDescriptionHeader(new String(ch, start, length));
		else if (isDescriptionBody) event.setDescriptionBody(new String(ch, start, length));

		else if (isStartDate) event.setStartDate(new String(ch, start, length));
		else if (isEndDate) event.setEndDate(new String(ch, start, length));
		else if (isICalURL) event.setICalURL(new String(ch, start, length));
		
		else if (isAssociatedCollege) event.setAssociatedCollege(new String(ch, start, length));
		else if (isEventScope) event.setScope(new String(ch, start, length));
		else if (isAssociatedSociety) event.setAssociatedSociety(new String(ch, start, length));

		else if (isContactTelephoneNumber) event.setContactTelephoneNumber(new String(ch, start,
				length));
		else if (isContactEmailAddress) event.setContactEmailAddress(new String(ch, start, length));
		else if (isWebAddress) event.setWebAddress(new String(ch, start, length));

		else if (isAddress1) location.setAddress1(new String(ch, start, length));
		else if (isAddress2) location.setAddress2(new String(ch, start, length));
		else if (isCity) location.setCity(new String(ch, start, length));
		else if (isPostcode) location.setPostcode(new String(ch, start, length));
		else if (isLatitude) location.setLatitude(new String(ch, start, length));
		else if (isLongitude) location.setLongitude(new String(ch, start, length));

		else if (isCategory && tagID != -1) categoryTags.add(new String(ch, start, length));

		else if (isAccessibilityInformation) event.setAccessibilityInformation(new String(ch,
				start, length));

		else if (isAdImageURL) event.setAdImageURL(new String(ch, start, length));

		else if (isReviewScore) event.setReviewScore(Integer
				.parseInt(new String(ch, start, length)));

		else if (isImageURL)
		{
			event.setImageURL(new String(ch, start, length));
		}
	}

}
