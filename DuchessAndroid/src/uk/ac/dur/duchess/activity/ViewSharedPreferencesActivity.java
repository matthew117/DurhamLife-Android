package uk.ac.dur.duchess.activity;

import java.util.Map;

import uk.ac.dur.duchess.GlobalApplicationData;
import uk.ac.dur.duchess.R;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;

public class ViewSharedPreferencesActivity extends Activity
{
	private TextView output;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.view_sharded_preferences);

		output = (TextView) findViewById(R.id.sharedPreferenceOutput);

		SharedPreferences sp = getSharedPreferences("UserSession", Context.MODE_PRIVATE);

		Map<String, ?> map = sp.getAll();

		for (String key : map.keySet())
		{
			output.append(key + " : " + map.get(key) + "\n");
		}
		
		output.append("\n");

		SharedPreferences fb = getSharedPreferences("facebook-credentials", Context.MODE_PRIVATE);

		Map<String, ?> newMap = fb.getAll();

		for (String key : newMap.keySet())
		{
			output.append(key + " : " + newMap.get(key) + "\n");
		}
		
		output.append("\n");

		SharedPreferences reporting = getSharedPreferences(GlobalApplicationData.REPORTING_PREFERENCES_KEY, Context.MODE_PRIVATE);

		Map<String, ?> newMap2 = reporting.getAll();

		for (String key : newMap2.keySet())
		{
			output.append(key + " : " + newMap2.get(key) + "\n");
		}
	}
}
