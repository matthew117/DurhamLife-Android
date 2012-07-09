package uk.ac.dur.duchess.activity;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

public class CalendarActivity extends Activity
{
	private Calendar calendar;
	private int lowerBound;
	private int upperBound;
	private int lastBound;
	
	private LinearLayout layout;
	private LayoutParams params;
	private LinearLayout header;
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		layout = new LinearLayout(this);
		params = new LayoutParams(-1, -2, 0);
		
		Display display = ((android.view.WindowManager) getSystemService(WINDOW_SERVICE)).getDefaultDisplay();
		int screenOrientation = display.getOrientation();
		
		if(screenOrientation != 0) params = new LayoutParams(-1, -1, 1);
		
		layout.setLayoutParams(params);
		
		calendar = Calendar.getInstance();
		
		setMonthBounds(calendar.get(Calendar.MONTH));
		
		int cell = 1 - lowerBound;
		
		header = new LinearLayout(this);
		header.setLayoutParams(new LinearLayout.LayoutParams(-1, -2, 0));
		header.setOrientation(1);
		
		TextView monthText = new TextView(this);
		
		SimpleDateFormat month = new SimpleDateFormat("MMMMM yyyy");
		
		monthText.setText(month.format(calendar.getTime()));
		monthText.setTextColor(Color.BLACK);
		monthText.setTextSize(15);
		monthText.setTypeface(Typeface.SERIF, Typeface.BOLD);
		monthText.setPadding(5, 5, 5, 5);
		monthText.setGravity(Gravity.CENTER_HORIZONTAL);
		
		int[] colors = {Color.parseColor("#7E317B"), Color.parseColor("#D8ACE0")};
		
		GradientDrawable gradient = new GradientDrawable(Orientation.BOTTOM_TOP, colors);
		gradient.setDither(true);
		
		header.setBackgroundDrawable(gradient);
		
		header.addView(monthText);
		
		
		LinearLayout days = new LinearLayout(this);
		days.setLayoutParams(new LinearLayout.LayoutParams(-1, -2, 0));
		
		String[] dayStrings = {"Mo", "Tu", "We", "Th", "Fr", "Sa", "Su"};
		
		for(int i = 0; i < 7; i++)
		{
			TextView day = new TextView(this);
			day.setText(dayStrings[i]);
			day.setGravity(Gravity.CENTER);
			day.setPadding(0, 0, 0, 5);
			day.setTextColor(Color.BLACK);
			days.addView(day, new LinearLayout.LayoutParams(0, -2, 1));
		}
		
		header.addView(days);
		
		layout.addView(header);
		
		for(int row = 0; row < 6; row++)
		{
			LinearLayout tableRow = new LinearLayout(this);
			tableRow.setLayoutParams(new LinearLayout.LayoutParams(-1, -2, 0));
			
			for(int col = 0; col < 7; col++)
			{
				if(cell < 1) tableRow.addView(getMonthButton(lastBound + cell, Color.GRAY), new LinearLayout.LayoutParams(0, -2, 1)); 
				else if(cell > upperBound) tableRow.addView(getMonthButton(cell % upperBound, Color.GRAY), new LinearLayout.LayoutParams(0, -2, 1));
				else tableRow.addView(getMonthButton(cell, Color.BLACK), new LinearLayout.LayoutParams(0, -2, 1));
				
				cell++;
			}
			
			layout.addView(tableRow);
		}
		
		layout.setOrientation(1);
		
		setContentView(layout);
	}
	
	private Button getMonthButton(final int day, int color)
	{
		CalendarButton button = new CalendarButton(this, color);
		
		button.setText(String.valueOf(((day < 10) ? " " : "") + day));
		button.setTextColor(color);
		button.setBackgroundColor(Color.WHITE);
		
		int bottom = button.getPaddingBottom();
		int left = button.getPaddingLeft();
		int right = button.getPaddingRight();
		int top = button.getPaddingTop();
		
		button.setPadding(left, top + 2, right, bottom);
		
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
		
		calendar.set(Calendar.MONTH, month);
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		
		lowerBound = (calendar.get(Calendar.DAY_OF_WEEK) + 5) % 7;
		upperBound = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
		
		calendar.set(Calendar.MONTH, month - 1);
		
		lastBound = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
	}
}
