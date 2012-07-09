package uk.ac.dur.duchess.activity;

import uk.ac.dur.duchess.R;
import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.widget.LinearLayout;

public class CustomTitleBarActivity extends Activity
{
	private LinearLayout buttonContainer;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);

		setContentView(R.layout.custom_title_bar);
		
		buttonContainer = (LinearLayout) findViewById(R.id.titleBarButtonContainer);

		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.custom_title_bar);
	}
	
	public LinearLayout getButtonContainer()
	{
		return buttonContainer;
		
	}
}
