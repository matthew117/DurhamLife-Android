package uk.ac.dur.duchess.activity;

import uk.ac.dur.duchess.R;
import android.app.Activity;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

public class TimeActivity extends Activity
{	
	private TextView dateRangeTextView;
	
	private TextView mondayTextView;
	private TextView tuesdayTextView;
	private TextView wednesdayTextView;
	private TextView thursdayTextView;
	private TextView fridayTextView;
	private TextView saturdayTextView;
	private TextView sundayTextView;
	
	private LinearLayout mondayContainer;
	private LinearLayout tuesdayContainer;
	private LinearLayout wednesdayContainer;
	private LinearLayout thursdayContainer;
	private LinearLayout fridayContainer;
	private LinearLayout saturdayContainer;
	private LinearLayout sundayContainer;
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.time_chooser_layout);
		
		dateRangeTextView = (TextView) findViewById(R.id.timeViewDateRange);
		
		mondayTextView = (TextView) findViewById(R.id.mondayTextView);
		tuesdayTextView = (TextView) findViewById(R.id.tuesdayTextView);
		wednesdayTextView = (TextView) findViewById(R.id.wednesdayTextView);
		thursdayTextView = (TextView) findViewById(R.id.thursdayTextView);
		fridayTextView = (TextView) findViewById(R.id.fridayTextView);
		saturdayTextView = (TextView) findViewById(R.id.saturdayTextView);
		sundayTextView = (TextView) findViewById(R.id.saturdayTextView);
		
		mondayContainer = (LinearLayout) findViewById(R.id.mondayDateContainer);
		tuesdayContainer = (LinearLayout) findViewById(R.id.tuesdayDateContainer);
		wednesdayContainer = (LinearLayout) findViewById(R.id.wednesdayDateContainer);
		thursdayContainer = (LinearLayout) findViewById(R.id.thursdayDateContainer);
		fridayContainer = (LinearLayout) findViewById(R.id.fridayDateContainer);
		saturdayContainer = (LinearLayout) findViewById(R.id.saturdayDateContainer);
		sundayContainer = (LinearLayout) findViewById(R.id.sundayDateContainer);
		
		
	}
}
