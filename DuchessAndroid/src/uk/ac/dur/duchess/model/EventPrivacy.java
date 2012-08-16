package uk.ac.dur.duchess.model;

public enum EventPrivacy
{
	OPEN,
	PRIVATE;

	public static EventPrivacy parsePrivacy(String privacy)
	{
		for (EventPrivacy e : EventPrivacy.values())
			if (e.toString().equalsIgnoreCase(privacy)) return e;

		return OPEN;
	}
}
