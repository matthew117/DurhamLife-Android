package uk.ac.dur.duchess.model;

public enum DurhamAffiliation
{
	 GUEST,
	 STUDENT,
	 STAFF;
	 
	 public static DurhamAffiliation parseScope(String affiliation)
	 {
		 for(DurhamAffiliation a : DurhamAffiliation.values())
			 if(a.toString().equalsIgnoreCase(affiliation)) return a;
		 
		 return GUEST;
	 }
}
