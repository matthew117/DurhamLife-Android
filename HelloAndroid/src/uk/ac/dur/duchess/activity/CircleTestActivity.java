package uk.ac.dur.duchess.activity;

import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.lang.Math.toRadians;
import uk.ac.dur.duchess.R;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;
import android.widget.AbsoluteLayout;
import android.widget.Button;
import android.widget.TextView;

@SuppressWarnings("deprecation")
public class CircleTestActivity extends Activity
{
	private AbsoluteLayout layout;
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		layout = new AbsoluteLayout(this);
		
		Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
		
		int width  = display.getWidth(); 
		int height = display.getHeight();
		
		TextView centre = new TextView(this);
		centre.setLayoutParams(new AbsoluteLayout.LayoutParams(-2, -2, width / 2, height / 2));
		
		layout.addView(centre);
		
		for(int i = 0; i < 360; i += 72)
		{
			TextView b = new TextView(this);
			
			int x = (int) ((width  / 2) + (width * 0.4) * cos(toRadians(i - 90)));
			int y = (int) ((height / 2) + (width * 0.4) * sin(toRadians(i - 90)));
			
			Log.d("POSITION", x + ", " + y);
			
			b.setBackgroundDrawable(getResources().getDrawable(R.drawable.full_star));
			b.setLayoutParams(new AbsoluteLayout.LayoutParams(-2, -2, x, y));
			
			layout.addView(b);
		}
		
		setContentView(layout);
	}
}
