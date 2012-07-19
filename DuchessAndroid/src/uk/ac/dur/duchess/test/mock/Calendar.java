package uk.ac.dur.duchess.test.mock;

public abstract class Calendar {

	private static final long serialVersionUID = 1153306472509249694L;
	
	public static java.util.Calendar mockInstance;
	
	public static java.util.Calendar getInstance()
	{
		return mockInstance;
	}

}
