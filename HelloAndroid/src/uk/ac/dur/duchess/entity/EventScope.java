package uk.ac.dur.duchess.entity;

public enum EventScope 
{
	 OPEN,
	 UNIVERSITY,
	 COLLEGE,
	 SOCIETY;
	 
	 public static EventScope parseScope(String scope)
	 {
		 for(EventScope e : EventScope.values())
			 if(e.toString().equalsIgnoreCase(scope)) return e;
		 
		 return OPEN;
	 }
}
