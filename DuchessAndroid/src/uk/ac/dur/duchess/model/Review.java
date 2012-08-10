package uk.ac.dur.duchess.model;

public class Review
{
	private long reviewID;
	private long eventID;
	private long reviewerID;
	private int rating;
	private String comment;
	private String timestamp;

	public long getReviewID()
	{
		return reviewID;
	}

	public void setReviewID(long reviewID)
	{
		this.reviewID = reviewID;
	}

	public long getEventID()
	{
		return eventID;
	}

	public void setEventID(long eventID)
	{
		this.eventID = eventID;
	}

	public long getReviewerID()
	{
		return reviewerID;
	}

	public void setReviewerID(long reviewerID)
	{
		this.reviewerID = reviewerID;
	}

	public int getRating()
	{
		return rating;
	}

	public void setRating(int rating)
	{
		this.rating = rating;
	}

	public String getComment()
	{
		return comment;
	}

	public void setComment(String comment)
	{
		this.comment = comment;
	}

	public String getTimestamp()
	{
		return timestamp;
	}

	public void setTimestamp(String timestamp)
	{
		this.timestamp = timestamp;
	}

}
