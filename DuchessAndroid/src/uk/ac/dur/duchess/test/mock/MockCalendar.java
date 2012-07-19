package uk.ac.dur.duchess.test.mock;

import java.util.Calendar;

public abstract class MockCalendar {

	private static final long serialVersionUID = 1153306472509249694L;
	
	public static java.util.Calendar mockInstance;
	
	public static java.util.Calendar getInstance()
	{
		if (mockInstance != null) return Calendar.getInstance();
		return (Calendar) mockInstance.clone();
	}

}
