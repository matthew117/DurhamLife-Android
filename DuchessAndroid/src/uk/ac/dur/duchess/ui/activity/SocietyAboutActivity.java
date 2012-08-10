package uk.ac.dur.duchess.ui.activity;

import uk.ac.dur.duchess.R;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class SocietyAboutActivity extends Activity
{
	private TextView societyDescription;
	private Button emailContactButton;
	private Button websiteContactButton;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		setTitle(getIntent().getExtras().getString("society_name"));
		
		setContentView(R.layout.about_society_layout);
		
		societyDescription = (TextView) findViewById(R.id.textViewSocietyDescription);
		emailContactButton = (Button) findViewById(R.id.emailContactButton);
		websiteContactButton = (Button) findViewById(R.id.websiteButton);
		
		Bundle s = getIntent().getExtras();
		
		String name = s.getString("society_name");
		final String webAddress = s.getString("society_website");
		final String contactEmail = s.getString("society_email");
		String constitution = s.getString("society_constitution");
		
		if(constitution != null) societyDescription.setText(constitution);
		
		if(contactEmail != null) emailContactButton.setText(contactEmail);
		else emailContactButton.setVisibility(View.GONE);
		
		emailContactButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				Intent intent = new Intent(Intent.ACTION_SEND);
				intent.setType("plain/text");
				intent.putExtra(Intent.EXTRA_EMAIL,new String[] { contactEmail });
				intent.putExtra(Intent.EXTRA_SUBJECT, "");
				intent.putExtra(Intent.EXTRA_TEXT, "");

				startActivity(Intent.createChooser(intent, "Send E-mail"));
			}
		});
		
		if(webAddress != null) websiteContactButton.setText(webAddress);
		else websiteContactButton.setVisibility(View.GONE);
		
		websiteContactButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				Intent i = new Intent(Intent.ACTION_VIEW);
				i.setData(Uri.parse(webAddress));
				startActivity(i);
			}
		});
	}
	
}
