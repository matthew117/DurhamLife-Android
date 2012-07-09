package uk.ac.dur.duchess.activity;

import uk.ac.dur.duchess.data.SessionFunctions;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;

public class MainActivity extends ListActivity
{
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		if (SessionFunctions.getCurrentUser(this) != null)
		{
			Intent i = new Intent(this, UserHubActivity.class);
			startActivity(i);
			finish();
		}
		else
		{
			Intent i = new Intent(this, LoginActivity.class);
			startActivity(i);
			finish();
		}
	}
}