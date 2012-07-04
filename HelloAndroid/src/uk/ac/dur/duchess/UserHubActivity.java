package uk.ac.dur.duchess;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

public class UserHubActivity extends Activity
{
	private static final int BROWSE_EVENTS = 0;
	private static final int COLLEGE_EVENTS = 1;
	private static final int USER_EVENTS = 2;
	private static final int NEWS = 3;

	private GridView grid;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.user_hub);

		grid = (GridView) findViewById(R.id.userHubGridView);

		Integer[] iconIDs = new Integer[] { R.drawable.grid, R.drawable.college, R.drawable.star,
				R.drawable.news };
		String[] textLabels = new String[] { "Browse", "College Events", "My Events", "News" };

		grid.setAdapter(new ImageGridAdapter(this, iconIDs, textLabels));

		grid.setOnItemClickListener(new OnItemClickListener()
		{
			public void onItemClick(AdapterView<?> parent, View v, int position, long id)
			{
				switch (position)
				{
				case BROWSE_EVENTS:
					Intent i = new Intent(v.getContext(), MainActivity.class);
					startActivity(i);
					break;
				case COLLEGE_EVENTS:
					Toast.makeText(v.getContext(), "College Events", Toast.LENGTH_LONG).show();
					break;
				case USER_EVENTS:
					Toast.makeText(v.getContext(), "Your Events", Toast.LENGTH_LONG).show();
					break;
				case NEWS:
					Toast.makeText(v.getContext(), "News", Toast.LENGTH_LONG).show();
					break;
				default:
					Log.e("UserHubActivity", "This shouldn't happen");
				}
			}
		});

	}
}
