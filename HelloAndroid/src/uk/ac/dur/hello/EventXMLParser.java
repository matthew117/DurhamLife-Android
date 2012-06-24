package uk.ac.dur.hello;

import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

public class EventXMLParser extends DefaultHandler
{
	private boolean isName = false;
	private boolean isDescriptionHeader = false;
	private boolean isStartDate = false;
	private boolean isEndDate = false;

	private List<Event> list;
	
	private Event event;

	public EventXMLParser(List<Event> events)
	{
		list = events;
		event = new Event();
	}

	@Override
	public void startElement(String URI, String localName, String qName, Attributes attributes)
	{
		if (localName.equalsIgnoreCase("event"))
		{
			event = new Event();
			String isFeatured = attributes.getValue("featured");
			if (isFeatured != null && isFeatured.equalsIgnoreCase("true"))
			{
				event.setFeatured(true);
			}
			else
			{
				event.setFeatured(false);
			}
		}
		else if (localName.equalsIgnoreCase("name")) isName = true;
		else if (localName.equalsIgnoreCase("descriptionHeader")) isDescriptionHeader = true;
		else if (localName.equalsIgnoreCase("startDate")) isStartDate = true;
		else if (localName.equalsIgnoreCase("endDate")) isEndDate = true;
	}

	@Override
	public void endElement(String URI, String localName, String qName)
	{		
		if (localName.equalsIgnoreCase("name")) isName = false;
		else if (localName.equalsIgnoreCase("descriptionHeader")) isDescriptionHeader = false;
		else if (localName.equalsIgnoreCase("startDate")) isStartDate = false;
		else if (localName.equalsIgnoreCase("endDate")) isEndDate = false;
	}

	@Override
	public void characters(char[] ch, int start, int length)
	{		
		if (isName)
		{
			event.setName(new String(ch, start, length));
		}
		else if (isDescriptionHeader) event.setDescriptionHeader(new String(ch, start, length));
		else if (isStartDate) event.setStartDate(new String(ch, start, length));
		else if (isEndDate)
		{
			event.setEndDate(new String(ch, start, length));
			list.add(event);
		}
	}
	
	@Override
	public void endDocument()
	{
		
	}

}
