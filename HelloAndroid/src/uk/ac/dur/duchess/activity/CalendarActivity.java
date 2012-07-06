package uk.ac.dur.duchess.activity;

import java.util.Calendar;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;

public class CalendarActivity extends Activity
{
	private TableLayout table;
	private Calendar calendar;
	private int lowerBound;
	private int upperBound;
	
	private LinearLayout layout;
	
	private Button leftButton;
	private Button rightButton;
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		layout = new LinearLayout(this);
		table = new TableLayout(this);
		calendar = Calendar.getInstance();
		
		setMonthBounds(calendar.get(Calendar.MONTH));
		
		int cell = 1 - lowerBound;
		
		for(int row = 0; row < 6; row++)
		{
			TableRow tableRow = new TableRow(this);
			
			for(int col = 0; col < 7; col++)
			{
				if(cell < 1) tableRow.addView(getMonthButton(cell, Color.LTGRAY));
				else if(cell > upperBound) tableRow.addView(getMonthButton(cell, Color.LTGRAY));
				else tableRow.addView(getMonthButton(cell, Color.BLACK));
				
				cell++;
			}
			
			table.addView(tableRow);
		}
		
		layout.setGravity(Gravity.CENTER_HORIZONTAL);
		layout.addView(table);
		
		setContentView(layout);
		
	}
	
	private Button getMonthButton(final int day, int color)
	{
		CalendarButton button = new CalendarButton(this, color);
		
		button.setText(String.valueOf(day));
		button.setTextColor(color);
		button.setBackgroundColor(Color.WHITE);
		
		int bottom = button.getPaddingBottom();
		int left = button.getPaddingLeft();
		int right = button.getPaddingRight();
		int top = button.getPaddingTop();
		
		button.setPadding(left/2, 1 + top/2, right/2, bottom/2);
		
		button.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				CalendarButton b = (CalendarButton) v;
				
				if(b.isClicked())
				{
					b.setClicked(false);
					b.setTextColor(b.getColor());
					v.setBackgroundColor(Color.WHITE);
				}
				else
				{
					b.setClicked(true);
					b.setTextColor(Color.parseColor("#7E317B"));
					v.setBackgroundColor(Color.parseColor("#D8ACE0"));
				}
			}
			
		});

		return button;
	}
	
	private class CalendarButton extends Button
	{
		private boolean clicked = false;
		private int color;
		
		public CalendarButton(Context context, int color)
		{
			super(context);
			this.color = color;
		}
		
		public boolean isClicked() { return clicked; }
		
		public void setClicked(boolean clicked) { this.clicked = clicked; }
		
		public int getColor() { return color; }
		
	}
	
	private void setMonthBounds(int month)
	{
		Calendar calendar = Calendar.getInstance();
		
		calendar.set(month, 1);
		
		lowerBound = (calendar.get(Calendar.DAY_OF_WEEK) + 5) % 7;
		upperBound = calendar.getMaximum(Calendar.DAY_OF_MONTH);
	}
}
