package uk.ac.dur.duchess.ui.activity;

import uk.ac.dur.duchess.GlobalApplicationData;
import uk.ac.dur.duchess.R;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;

import com.bugsense.trace.BugSenseHandler;

public class RegisterActivity extends Activity
{
	private Button nowButton;
	private Button laterButton;
	private CheckBox usageDataCheckBox;
	private CheckBox bugReportCheckBox;
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.register_layout);

		nowButton = (Button) findViewById(R.id.registerNowButton);
		laterButton = (Button) findViewById(R.id.registerLaterButton);
		usageDataCheckBox = (CheckBox) findViewById(R.id.usageDataCheckBox);
		bugReportCheckBox = (CheckBox) findViewById(R.id.bugReportCheckBox);
		
		nowButton.setOnClickListener(new OnClickListener()
		{	
			@Override
			public void onClick(View v)
			{
				setPermissions();
				setResult(RESULT_OK);
				finish();
			}
		});
		
		laterButton.setOnClickListener(new OnClickListener()
		{	
			@Override
			public void onClick(View v)
			{
				setPermissions();
				setResult(RESULT_CANCELED);
				finish();
			}
		});
	}
	
	private void setPermissions()
	{
		if(bugReportCheckBox.isChecked())
		{
			GlobalApplicationData.setBugsensePermission(true, RegisterActivity.this);

			BugSenseHandler.setup(RegisterActivity.this,
					GlobalApplicationData.BUGSENSE_API_KEY);
		}
		else
		{
			GlobalApplicationData.setBugsensePermission(false, RegisterActivity.this);
		}
		
		GlobalApplicationData.setAnalyticsPermission(usageDataCheckBox.isChecked(), RegisterActivity.this);
	}
}