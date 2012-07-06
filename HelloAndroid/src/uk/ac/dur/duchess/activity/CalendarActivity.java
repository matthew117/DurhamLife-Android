package uk.ac.dur.duchess.activity;

import java.util.Calendar;
import java.util.GregorianCalendar;

import uk.ac.dur.duchess.R;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.ToggleButton;

public class CalendarActivity extends Activity
{
	private TableLayout table;
	private Calendar calendar;
	private int lowerBound;
	private int upperBound;
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		table = new TableLayout(this);
		calendar = Calendar.getInstance();
		
		setMonthBounds(calendar.get(Calendar.MONTH));
		
		int cell = 1 - lowerBound;
		
		for(int row = 0; row < 6; row++)
		{
			TableRow tableRow = new TableRow(this);
			
			for(int col = 0; col < 7; col++)
			{
				if(cell < 1) tableRow.addView(new ToggleButton(this));
				else if(cell > upperBound) tableRow.addView(new ToggleButton(this));
				else tableRow.addView(new ToggleButton(this));
				
				cell++;
			}
			
			table.addView(tableRow);
		}
		
		setContentView(table);
		
	}
	
	private void setMonthBounds(int month)
	{
		Calendar calendar = Calendar.getInstance();
		
		calendar.set(month, 1);
		
		lowerBound = (calendar.get(Calendar.DAY_OF_WEEK) + 5) % 7;
		upperBound = calendar.getMaximum(Calendar.DAY_OF_MONTH);
	}
}
