package uk.ac.dur.duchess.activity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

import uk.ac.dur.duchess.R;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;

public class DateFrameActivity extends Activity
{
	private static final int FROM_DATE_DIALOG_ID = 1;
	private static final int TO_DATE_DIALOG_ID = 2;
	
	private Button confirmButton;
	private Button cancelButton;
	private Button fromDatePicker;
	private Button toDatePicker;
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.dateframe_layout);
		
		confirmButton  = (Button) findViewById(R.id.confirmDateFrameButton);
		cancelButton   = (Button) findViewById(R.id.cancelDateFrameButton);
		fromDatePicker = (Button) findViewById(R.id.fromDatePicker);
		toDatePicker   = (Button) findViewById(R.id.toDatePicker);
		
		Calendar c = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("d MMMMM yyyy");
		
		fromDatePicker.setText(sdf.format(c.getTime()));
		toDatePicker.setText(sdf.format(c.getTime()));
		
		fromDatePicker.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				showDialog(FROM_DATE_DIALOG_ID);
			}
		});
		
		toDatePicker.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				showDialog(TO_DATE_DIALOG_ID);
			}
		});
		
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
				
				String fromDate = fromDatePicker.getText().toString();
				String toDate = toDatePicker.getText().toString();
				
				returnIntent.putExtra("from_date", fromDate);
				returnIntent.putExtra("to_date", toDate);
				
				setResult(RESULT_OK, returnIntent);
				finish();
			}
		});
	}

	@Override
	protected Dialog onCreateDialog(int id)
	{
		switch (id)
		{
			case FROM_DATE_DIALOG_ID:
			{
				Calendar c = Calendar.getInstance();
				int year = c.get(Calendar.YEAR);
				int month = c.get(Calendar.MONTH);
				int day = c.get(Calendar.DATE);
				
				return new DatePickerDialog(this, fromDateSetListener, year, month, day);
			}
			case TO_DATE_DIALOG_ID:
			{
				Calendar c = Calendar.getInstance();
				int year = c.get(Calendar.YEAR);
				int month = c.get(Calendar.MONTH);
				int day = c.get(Calendar.DATE);
				
				return new DatePickerDialog(this, toDateSetListener, year, month, day);
			}
		}
		return null;
	}
	
	private OnDateSetListener fromDateSetListener = new OnDateSetListener()
	{
		public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth)
		{
			Calendar c = new GregorianCalendar(year, monthOfYear, dayOfMonth);
			SimpleDateFormat sdf = new SimpleDateFormat("d MMMMM yyyy");
			
			fromDatePicker.setText(sdf.format(c.getTime()));
		}
	};
	
	private OnDateSetListener toDateSetListener = new OnDateSetListener()
	{
		public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth)
		{
			Calendar c = new GregorianCalendar(year, monthOfYear, dayOfMonth);
			SimpleDateFormat sdf = new SimpleDateFormat("d MMMMM yyyy");
			
			toDatePicker.setText(sdf.format(c.getTime()));
		}
	};
}
