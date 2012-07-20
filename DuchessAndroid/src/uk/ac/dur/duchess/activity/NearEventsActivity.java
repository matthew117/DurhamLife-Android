package uk.ac.dur.duchess.activity;

import java.util.ArrayList;
import java.util.List;

import com.google.android.maps.GeoPoint;

import uk.ac.dur.duchess.GlobalApplicationData;
import uk.ac.dur.duchess.entity.Event;
import android.app.Activity;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

public class NearEventsActivity extends Activity
{
	private List<Event> eventList;
	private GeoPoint currentLocation;
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		eventList = new ArrayList<Event>(GlobalApplicationData.globalEventList);
		currentLocation = new GeoPoint((int) (54.768018 * 1E6), (int) (-1.571968 * 1E6));
	}
	
	private class MyLocationListener implements LocationListener
	{
		@Override
		public void onLocationChanged(Location newLocation)
		{
			if (newLocation != null)
			{
				GeoPoint g = null;
				
				for (Event e : eventList)
				{
					String eventName = e.getName();
					GeoPoint eventLocation = stringToGeoPoint(e.getLatitude(), e.getLongitude());
					double distance = distanceBetweenTwoPoints(g, eventLocation);
					
				}
			}
		}

		@Override
		public void onProviderDisabled(String provider)
		{
		}

		@Override
		public void onProviderEnabled(String provider)
		{
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras)
		{
		}
	}
	
	private double distanceBetweenTwoPoints(GeoPoint a, GeoPoint b)
	{
		double R = 6371.0; // km
		double dLat = Math.toRadians((a.getLatitudeE6() / 1E6 - b.getLatitudeE6() / 1E6));
		double dLon = Math.toRadians((a.getLongitudeE6() / 1E6 - b.getLongitudeE6() / 1E6));
		double lat1 = Math.toRadians(a.getLatitudeE6() / 1E6);
		double lat2 = Math.toRadians(b.getLatitudeE6() / 1E6);

		double t = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.sin(dLon / 2)
				* Math.sin(dLon / 2) * Math.cos(lat1) * Math.cos(lat2);
		double c = 2 * Math.atan2(Math.sqrt(t), Math.sqrt(1 - t));
		return R * c;
	}
	
	private GeoPoint stringToGeoPoint(String latitude, String longitude)
	{
		return new GeoPoint((int) (Double.parseDouble(latitude) * 1E6), (int) (Double.parseDouble(longitude) * 1E6));
	}
}
