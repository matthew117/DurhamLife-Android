package uk.ac.dur.duchess.activity;

import uk.ac.dur.duchess.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;

public class DateFrameActivity extends Activity
{
	private Button confirmButton;
	private Button cancelButton;
	private DatePicker fromDatePicker;
	private DatePicker toDatePicker;
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.dateframe_layout);
		
		confirmButton = (Button) findViewById(R.id.confirmDateFrameButton);
		cancelButton = (Button) findViewById(R.id.cancelDateFrameButton);
		fromDatePicker = (DatePicker) findViewById(R.id.fromDatePicker);
		toDatePicker = (DatePicker) findViewById(R.id.toDatePicker);
		
		cancelButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v) { setResult(RESULT_CANCELED); finish(); }
		});
		
		confirmButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				Intent returnIntent = new Intent();
				
				String fromDate
					= fromDatePicker.getYear() + "-"
					+ fromDatePicker.getMonth() + "-"
					+ fromDatePicker.getDayOfMonth();
				
				String toDate
					= toDatePicker.getYear() + "-"
					+ toDatePicker.getMonth() + "-"
					+ toDatePicker.getDayOfMonth();
				
				returnIntent.putExtra("from_date", fromDate);
				returnIntent.putExtra("to_date", toDate);
				
				setResult(RESULT_OK, returnIntent);
				finish();
			}
		});
	}
}
