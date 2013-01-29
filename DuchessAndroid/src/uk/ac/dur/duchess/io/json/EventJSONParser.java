package uk.ac.dur.duchess.io.json;

import java.util.ArrayList;
import java.util.List;

import uk.ac.dur.duchess.model.Event;
import uk.ac.dur.duchess.model.EventLocation;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import org.json.JSONArray;
import org.json.JSONObject;

import android.util.Log;


public class EventJSONParser
{
	public static List<Event> parseJSONEvents(String url)
	{
		List<Event> events = new ArrayList<Event>();
		
		String eventJSON = requestJSON(url);
		
		try
		{
			JSONArray eventArray = new JSONArray(eventJSON);
			Log.i("JSON EVENTS: ", "" + eventArray.length());
			
			for(int i = 0; i < eventArray.length(); i++)
			{
				JSONObject eventObject = eventArray.getJSONObject(i);
				
				Event event = new Event();
				
				event.setEventID(eventObject.getLong("id"));
				event.setName(eventObject.getString("title"));
				event.setDescriptionHeader(eventObject.getString("summary"));
				
				if(eventObject.has("full_description")) event.setDescriptionBody(eventObject.getString("full_description"));
				else event.setDescriptionBody(eventObject.getString("description"));
				
				//TODO venue information
				EventLocation location = new EventLocation();
				
				String venue = eventObject.getString("venue");
				String[] address = venue.split(", ");
				
				if(address.length > 0) location.setAddress1(address[0]);
				if(address.length > 1) location.setAddress2(address[1]);
				if(address.length > 2) location.setCity(address[2]);
				if(address.length > 3) location.setPostcode(address[3]);
				
				event.setLocation(location);
				
				event.setContactEmailAddress(eventObject.getString("contact_email"));
				
				String start = eventObject.has("start") ? eventObject.getString("start") : eventObject.getString("start_date_time");
				String end = eventObject.has("end") ? eventObject.getString("end") : eventObject.getString("end_date_time");
				
				event.setStartDate(start.substring(0, 10));
				event.setStartTime(start);
				
				event.setEndDate(end.substring(0, 10));
				event.setEndTime(end);
				
				if(eventObject.has("image_url")) event.setImageURL(eventObject.getString("image_url"));
				else event.setImageURL(eventObject.getString("image_URL"));
				
				JSONArray categoriesArray = eventObject.getJSONArray("categories");
				List<String> categories = new ArrayList<String>();
				
				for(int j = 0; j < categoriesArray.length(); j++)
				{
					JSONObject categoryObject = categoriesArray.getJSONObject(j);
					
					long id = categoryObject.getLong("id");
					String category = categoryObject.has("name") ? categoryObject.getString("name") : categoryObject.getString("category");
					
					if(isSuitableCategory(id, category)) categories.add(category);
				}
				
				event.setCategoryTags(categories);
				
				if(eventObject.has("links"))
				{			
					JSONObject linksObject = eventObject.getJSONObject("links");
					event.setICalURL(linksObject.getString("ical"));
				}
				
				events.add(event);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return events;
	}
	
	public static boolean isSuitableCategory(long id, String category)
	{
		if (category.contains( "University") || id == 679) return true;
		if (category.contains(    "College") || id == 680) return true;
		if (category.contains(    "Theatre") || id == 264) return true;
		if (category.contains(      "Music") || id ==  52) return true;
		if (category.contains("Exhibitions") || id ==  50) return true;
		if (category.contains("Conferences") || id ==  49) return true;
		if (category.contains(  "Community") || id == 558) return true;
		if (category.contains(      "Sport") || id ==  54) return true;
		
		return false;
	}

	public static String requestJSON(String url)
	{
		StringBuilder builder = new StringBuilder();
		HttpClient client = new DefaultHttpClient();
		HttpGet httpGet = new HttpGet(url);
		
		try
		{
			HttpResponse response = client.execute(httpGet);
			StatusLine statusLine = response.getStatusLine();
			int statusCode = statusLine.getStatusCode();
			
			if(statusCode == 200)
			{
				HttpEntity entity = response.getEntity();
				InputStream content = entity.getContent();
				BufferedReader reader = new BufferedReader(new InputStreamReader(content));
				
				String line = "";
				
				while((line = reader.readLine()) != null) builder.append(line);
			}
			else Log.e("JSON", "Failed to download file");
		}
		catch(ClientProtocolException e) { e.printStackTrace(); }
		catch(IOException e) 			 { e.printStackTrace(); }
		
		return builder.toString();
	}
}
