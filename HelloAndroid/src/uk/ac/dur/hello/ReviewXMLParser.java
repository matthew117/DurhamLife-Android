package uk.ac.dur.hello;

import java.util.ArrayList;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

public class ReviewXMLParser extends DefaultHandler
{

	private Review review;
	private ArrayList<Review> reviewList;

	private boolean isRating = false;
	private boolean isPost = false;
	private boolean isTimeStamp = false;

	public ReviewXMLParser(ArrayList<Review> reviewList)
	{
		review = new Review();
		this.reviewList = reviewList;
	}

	@Override
	public void startElement(String URI, String localName, String qName, Attributes attributes)
	{
		if (localName.equalsIgnoreCase("review"))
		{
			review = new Review();
			review.setReviewID(Long.parseLong(attributes.getValue("reviewID")));
			review.setEventID(Long.parseLong(attributes.getValue("eventID")));
			review.setReviewerID(Long.parseLong(attributes.getValue("userID")));
		}
		else if (localName.equalsIgnoreCase("rating")) isRating = true;
		else if (localName.equalsIgnoreCase("post")) isPost = true;
		else if (localName.equalsIgnoreCase("timestamp")) isTimeStamp = true;
	}

	@Override
	public void endElement(String URI, String localName, String qName)
	{
		if (localName.equalsIgnoreCase("rating")) isRating = false;
		else if (localName.equalsIgnoreCase("post")) isPost = false;
		else if (localName.equalsIgnoreCase("timestamp")) isTimeStamp = false;
		else if (localName.equalsIgnoreCase("review"))
		{
			reviewList.add(review);
		}
	}

	@Override
	public void characters(char[] ch, int start, int length)
	{
		if (isPost) review.setComment(new String(ch, start, length));
		else if (isRating) review.setRating(Integer.parseInt(new String(ch, start, length)));
		else if (isTimeStamp) review.setTimestamp(new String(ch, start, length));
	}

}
