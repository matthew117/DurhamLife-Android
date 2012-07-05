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
	private DatePicker afterDatePicker;
	private DatePicker beforeDatePicker;
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.dateframe_layout);
		
		confirmButton = (Button) findViewById(R.id.confirmDateFrameButton);
		cancelButton = (Button) findViewById(R.id.cancelDateFrameButton);
		afterDatePicker = (DatePicker) findViewById(R.id.afterDatePicker);
		beforeDatePicker = (DatePicker) findViewById(R.id.beforeDatePicker);
		
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
				
				String afterDate
					= afterDatePicker.getYear() + "-"
					+ afterDatePicker.getMonth() + "-"
					+ afterDatePicker.getDayOfMonth();
				
				String beforeDate
					= beforeDatePicker.getYear() + "-"
					+ beforeDatePicker.getMonth() + "-"
					+ beforeDatePicker.getDayOfMonth();
				
				returnIntent.putExtra("after_date", afterDate);
				returnIntent.putExtra("before_date", beforeDate);
				
				setResult(RESULT_OK, returnIntent);
				finish();
			}
		});
	}
}
