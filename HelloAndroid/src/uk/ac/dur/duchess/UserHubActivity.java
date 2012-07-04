package uk.ac.dur.duchess;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class UserHubActivity extends Activity
{
	private TextView browseButton;
	private TextView collegeEventButton;
	private TextView myEventsButton;
	private TextView newsButton;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.user_hub_table);
		
		browseButton = (TextView) findViewById(R.id.userHubBrowse);
		collegeEventButton = (TextView) findViewById(R.id.userHubCollege);
		myEventsButton = (TextView) findViewById(R.id.userHubMyEvents);
		newsButton = (TextView) findViewById(R.id.userHubNews);
		
		browseButton.setOnClickListener(new View.OnClickListener()
		{		
			@Override
			public void onClick(View v)
			{
				Intent i = new Intent(v.getContext(), MainActivity.class);
				startActivity(i);				
			}
		});
		
		collegeEventButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				Toast.makeText(v.getContext(), "Displays a list of events run by the user's college", Toast.LENGTH_LONG).show();
				
			}
		});
		
		myEventsButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				Toast.makeText(v.getContext(), "Displays a list of events that the user is going to", Toast.LENGTH_LONG).show();				
			}
		});
		
		newsButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				Toast.makeText(v.getContext(), "Displays a news feed about Durham events", Toast.LENGTH_LONG).show();				
			}
		});

	}
}
