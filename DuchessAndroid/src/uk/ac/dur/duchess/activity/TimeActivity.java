package uk.ac.dur.duchess.activity;

import uk.ac.dur.duchess.R;
import android.app.Activity;
import android.os.Bundle;

public class TimeActivity extends Activity
{	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.time_chooser_layout);
	}
}
