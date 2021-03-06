package uk.ac.dur.duchess.io.xml;

import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import uk.ac.dur.duchess.model.Society;

public class SocietyXMLParser extends DefaultHandler
{
	
	private List<Society> societyList;
	private Society s;
	
	private boolean isNameTag;
	private boolean isWebsiteTag;
	private boolean isEmailTag;
	private boolean isConstitutionTag;

	public SocietyXMLParser(List<Society> societyList2)
	{
		this.societyList = societyList2;
		s = new Society();
	}
	
	@Override
	public void startElement(String URI, String localName, String qName, Attributes attributes)
	{
		if (localName.equalsIgnoreCase("society"))
		{
			s = new Society();
			s.setSocietyID(Long.parseLong(attributes.getValue("id")));
		}
		else if (localName.equalsIgnoreCase("name")) isNameTag = true;
		else if (localName.equalsIgnoreCase("website")) isWebsiteTag = true;
		else if (localName.equalsIgnoreCase("email")) isEmailTag = true;
		else if (localName.equalsIgnoreCase("constitution")) isConstitutionTag = true;
	}
	
	@Override
	public void endElement(String URI, String localName, String qName)
	{
		if (localName.equalsIgnoreCase("society"))
		{
			societyList.add(s);
		}
		else if (localName.equalsIgnoreCase("name")) isNameTag = false;
		else if (localName.equalsIgnoreCase("website")) isWebsiteTag = false;
		else if (localName.equalsIgnoreCase("email")) isEmailTag = false;
		else if (localName.equalsIgnoreCase("constitution")) isConstitutionTag = false;

	}
	
	@Override
	public void characters(char[] ch, int start, int length)
	{
		if (isNameTag)
		{
			if (s.getName() == null) s.setName(new String(ch, start, length));
			else s.setName(s.getName() + (new String(ch, start, length)));

		}
		else if (isWebsiteTag) s.setWebsite(new String(ch,start,length));
		else if (isEmailTag) s.setEmail(new String(ch,start,length));
		else if (isConstitutionTag)
		{
			if (s.getConstitution() == null)
				s.setConstitution(new String(ch, start, length));
			else
				s.setConstitution(s.getConstitution() + (new String(ch, start, length)));
		}
	}

}
