package uk.ac.dur.duchess.ui.activity;

import uk.ac.dur.duchess.R;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.TextView;

public class AboutBoxActivity extends Activity
{
	private TextView versionText;
	private Button okButton;
	private Button termsButton;
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		setContentView(R.layout.about_app_layout);
		getWindow().setLayout(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		
		versionText = (TextView) findViewById(R.id.aboutBoxVersion);
		okButton = (Button) findViewById(R.id.aboutBoxButtonOK);
		termsButton = (Button) findViewById(R.id.aboutBoxButtonTerms);
		
		versionText.setText("Version " + getVersion(this));
		
		okButton.setOnClickListener(new View.OnClickListener()
		{	
			@Override
			public void onClick(View v)
			{
				finish();				
			}
		});
		
	}
	
	private String getVersion(Context context)
	{
		try
		{
			return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
		}
		catch (NameNotFoundException e)
		{
			return "Unknown";
		}
	}
}
