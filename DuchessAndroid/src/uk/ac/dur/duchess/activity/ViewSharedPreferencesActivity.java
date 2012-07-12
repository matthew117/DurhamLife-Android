package uk.ac.dur.duchess.activity;

import java.util.Map;

import uk.ac.dur.duchess.R;
import android.app.Activity;
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
		
		SharedPreferences sp = getSharedPreferences("UserSession", Activity.MODE_PRIVATE);
		
		Map<String, ?> map = sp.getAll();
		
		for (String key : map.keySet())
		{
			output.append(key + " : " + map.get(key)+"\n");
		}
	}
}
