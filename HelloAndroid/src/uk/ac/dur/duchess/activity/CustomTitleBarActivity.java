package uk.ac.dur.duchess.activity;

import uk.ac.dur.duchess.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;

public class CustomTitleBarActivity extends Activity
{
	protected ImageButton titleBarButton1;
	protected ImageButton titleBarButton2;
	protected ImageButton titleBarButton3;
	protected ImageButton titleBarButton4;
	
	private ImageView titleBarLogo;
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);

		setContentView(R.layout.custom_title_bar);

		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.custom_title_bar);
		
		titleBarButton1 = (ImageButton) findViewById(R.id.titleBarButton1);
		titleBarButton2 = (ImageButton) findViewById(R.id.titleBarButton2);
		titleBarButton3 = (ImageButton) findViewById(R.id.titleBarButton3);
		titleBarButton4 = (ImageButton) findViewById(R.id.titleBarButton4);
		
		titleBarLogo = (ImageView) findViewById(R.id.titleBarLogo);
		titleBarLogo.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				Intent i = new Intent(v.getContext(), MainActivity.class);
				startActivity(i);
				finish();
			}
		});
	}
}
