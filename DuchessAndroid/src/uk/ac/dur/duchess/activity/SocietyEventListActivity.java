package uk.ac.dur.duchess.activity;

import java.io.IOException;
import java.io.InputStream;

import uk.ac.dur.duchess.EventListView;
import uk.ac.dur.duchess.R;
import uk.ac.dur.duchess.data.NetworkFunctions;
import uk.ac.dur.duchess.data.SessionFunctions;
import uk.ac.dur.duchess.entity.User;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class SocietyEventListActivity extends CustomTitleBarActivity
{
	private EventListView listView;
	private User user;
	private TextView societyNameText;

	private Button aboutButton;
	private Button subscribeButton;

	private String societyName;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.society_event_list_layout);

		user = SessionFunctions.getCurrentUser(this);
		
		societyNameText = (TextView) findViewById(R.id.nameOnSocietyEventList);

		aboutButton = (Button) findViewById(R.id.societyAboutButton);
		subscribeButton = (Button) findViewById(R.id.societyButtonSubscribe);

		final Bundle s = getIntent().getExtras();

		aboutButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				Intent i = new Intent(v.getContext(), SocietyAboutActivity.class);
				Bundle societyInfo = getIntent().getExtras();

				i.putExtra("society_id", societyInfo.getString("society_id"));
				i.putExtra("society_name", societyInfo.getString("society_name"));
				i.putExtra("society_constitution", societyInfo.getString("society_constitution"));
				i.putExtra("society_website", societyInfo.getString("society_website"));
				i.putExtra("society_email", societyInfo.getString("society_email"));

				startActivity(i);

			}
		});

		if (s.getString("society_name") != null)
		{
			societyName = s.getString("society_name");
			societyNameText.setText(societyName);
		}
		
		listView = (EventListView) findViewById(R.id.societyEventListView);
		
		Bundle b = new Bundle();
		b.putString("society_name", societyName);
		
		listView.loadAllEvents(this, b);

		if(user.isSubscribedToSociety(societyNameText.getText().toString()))
			subscribeButton.setText("Unsubscribe");

		subscribeButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				String societyName = societyNameText.getText().toString();

				if(!user.isSubscribedToSociety(societyName))
				{
					Thread t = new Thread(new Runnable()
					{		
						@Override
						public void run()
						{
							try
							{
								InputStream is = NetworkFunctions.getHTTPResponseStream(
										"http://www.dur.ac.uk/cs.seg01/duchess/api/v1/societies.php?userID=" +
												user.getUserID() + "&societyID=" + s.getLong("society_id"), "GET", null);
							}
							catch (IOException e)
							{
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					});
					t.start();

					user.getSocieties().add(societyNameText.getText().toString());
					subscribeButton.setText("Unsubscribe");
				}
				else
				{
					// TODO Should also update in the external database not just in the shared preferences 
					user.getSocieties().remove(societyNameText.getText().toString());
					subscribeButton.setText("Subscribe");
				}

				SessionFunctions.saveUserPreferences((Activity) v.getContext(), user);
			}
		});
	}

	@Override
	public void onResume()
	{
		super.onResume();
		user = SessionFunctions.getCurrentUser(this);
		listView.setAdapter(listView.getAdapter());
	}
}
