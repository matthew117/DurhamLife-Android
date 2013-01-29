package uk.ac.dur.duchess.io.json;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.util.Log;

import uk.ac.dur.duchess.model.EventLocation;

public class LocationJSONParser
{
	public static List<EventLocation> parseJSONLocation(String jsonString)
	{
		List<EventLocation> locations = new ArrayList<EventLocation>();

		try
		{
			JSONArray locationJSONArray = new JSONArray(jsonString);
			Log.i("JSON_LOCATION", "Constructed " + locationJSONArray.length());
			for (int i = 0; i < locationJSONArray.length(); i++)
			{
				JSONObject locationJSONObject = locationJSONArray.getJSONObject(i);
				EventLocation location = new EventLocation();
				// TODO locations have to be linked to events before this can be finished
				
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}

		return locations;
	}
}
