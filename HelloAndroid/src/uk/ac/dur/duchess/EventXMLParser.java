package uk.ac.dur.duchess;

import java.util.HashMap;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

public class EventXMLParser extends DefaultHandler
{
	private boolean isName = false;
	private boolean isDescriptionHeader = false;
	private boolean isDescriptionBody = false;
	
	private boolean isStartDate = false;
	private boolean isEndDate = false;
	
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
	private boolean isSubcategory = false;
	
	private boolean isAccessibilityInformation = false;
	
	private boolean isImageURL = false;

	private List<Event> list;
	
	private Event event;
	private HashMap<Long, String> categoryTags;
	private HashMap<Long, String> subcategoryTags;
	private long tagID = -1;

	public EventXMLParser(List<Event> events)
	{
		list = events;
		event = new Event();
		categoryTags = new HashMap<Long, String>();
		subcategoryTags = new HashMap<Long, String>();
	}

	@Override
	public void startElement(String URI, String localName, String qName, Attributes attributes)
	{
		if (localName.equalsIgnoreCase("event"))
		{
			event = new Event();
			categoryTags = new HashMap<Long, String>();
			subcategoryTags = new HashMap<Long, String>();
			
			String isFeatured = attributes.getValue("featured");
			event.setFeatured(isFeatured != null && isFeatured.equalsIgnoreCase("true"));
			
			String id = attributes.getValue("id");
			long eventID = (id != null) ? Long.parseLong(id) : -1;
			if(eventID != -1) event.setEventID(eventID);
		}
		else if (localName.equalsIgnoreCase("name")) isName = true;
		else if (localName.equalsIgnoreCase("descriptionHeader")) isDescriptionHeader = true;
		else if (localName.equalsIgnoreCase("descriptionBody")) isDescriptionBody = true;
		
		else if (localName.equalsIgnoreCase("startDate")) isStartDate = true;
		else if (localName.equalsIgnoreCase("endDate")) isEndDate = true;
		
		else if (localName.equalsIgnoreCase("contactTelephoneNumber")) isContactTelephoneNumber = true;
		else if (localName.equalsIgnoreCase("contactEmailAddress")) isContactEmailAddress = true;
		else if (localName.equalsIgnoreCase("webAddress")) isWebAddress = true;
		
		else if (localName.equalsIgnoreCase("location"))
		{
			String id = attributes.getValue("id");
			long locationID = (id != null) ? Long.parseLong(id) : -1;
			if(locationID != -1) event.setLocationID(locationID);
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
		else if (localName.equalsIgnoreCase("subcategory"))
		{
			String id = attributes.getValue("id");
			tagID = (id != null) ? Long.parseLong(id) : -1;
			
			isSubcategory = true;
		}
		
		else if (localName.equalsIgnoreCase("accessibilityInformation")) isAccessibilityInformation = true;
		
		else if (localName.equalsIgnoreCase("imageURL")) isImageURL = true;	
	}

	@Override
	public void endElement(String URI, String localName, String qName)
	{		
		if      (localName.equalsIgnoreCase("name")) isName = false;
		else if (localName.equalsIgnoreCase("descriptionHeader")) isDescriptionHeader = false;
		else if (localName.equalsIgnoreCase("descriptionBody")) isDescriptionBody = false;
		
		else if (localName.equalsIgnoreCase("startDate")) isStartDate = false;
		else if (localName.equalsIgnoreCase("endDate")) isEndDate = false;
		
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
		else if (localName.equalsIgnoreCase("subcategory")) isSubcategory = false;
		
		else if (localName.equalsIgnoreCase("accessibilityInformation")) isAccessibilityInformation = false;
		
		else if (localName.equalsIgnoreCase("imageURL")) isImageURL = false;
	}

	@Override
	public void characters(char[] ch, int start, int length)
	{		
		if      (isName) event.setName(new String(ch, start, length));
		else if (isDescriptionHeader) event.setDescriptionHeader(new String(ch, start, length));
		else if (isDescriptionBody) event.setDescriptionBody(new String(ch, start, length));
		
		else if (isStartDate) event.setStartDate(new String(ch, start, length));
		else if (isEndDate) event.setEndDate(new String(ch, start, length));
		
		else if (isContactTelephoneNumber) event.setContactTelephoneNumber(new String(ch, start, length));
		else if (isContactEmailAddress) event.setContactEmailAddress(new String(ch, start, length));
		else if (isWebAddress) event.setWebAddress(new String(ch, start, length));
		
		else if (isAddress1) event.setAddress1(new String(ch, start, length));
		else if (isAddress2) event.setAddress2(new String(ch, start, length));
		else if (isCity) event.setCity(new String(ch, start, length));
		else if (isPostcode) event.setPostcode(new String(ch, start, length));
		else if (isLatitude) event.setLatitude(new String(ch, start, length));
		else if (isLongitude) event.setLongitude(new String(ch, start, length));
		
		else if (isCategory && tagID != -1) categoryTags.put(tagID, new String(ch, start, length));
		else if (isSubcategory && tagID != -1) subcategoryTags.put(tagID, new String(ch, start, length));
		
		else if (isAccessibilityInformation) event.setAccessibilityInformation(new String(ch, start, length));
		
		else if (isImageURL)
		{
			event.setImageURL(new String(ch, start, length));
			
			event.setCategoryTags(categoryTags);
			event.setSubcategoryTags(subcategoryTags);
			list.add(event);
		}
	}
	
	@Override
	public void endDocument()
	{
		
	}

}
