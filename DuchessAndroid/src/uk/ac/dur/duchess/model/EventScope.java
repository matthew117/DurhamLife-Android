package uk.ac.dur.duchess.model;

public enum EventScope 
{
	 PUBLIC,
	 UNIVERSITY,
	 COLLEGE,
	 SOCIETY;
	 
	 public static EventScope parseScope(String scope)
	 {
		 for(EventScope e : EventScope.values())
			 if(e.toString().equalsIgnoreCase(scope)) return e;
		 
		 return PUBLIC;
	 }
}
