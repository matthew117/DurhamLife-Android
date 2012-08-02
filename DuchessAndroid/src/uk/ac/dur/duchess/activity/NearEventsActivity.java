package uk.ac.dur.duchess.activity;

import java.util.ArrayList;
import java.util.List;

import uk.ac.dur.duchess.GlobalApplicationData;
import uk.ac.dur.duchess.R;
import uk.ac.dur.duchess.entity.Event;
import uk.ac.dur.duchess.entity.EventLocation;
import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;

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
		
		setContentView(R.layout.view_shared_preferences);
		
		LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		LocationListener locationListener = new MyLocationListener();

		lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
	}
	
	private class MyLocationListener implements LocationListener
	{
		@Override
		public void onLocationChanged(Location newLocation)
		{
			if (newLocation != null)
			{
				GeoPoint g = new GeoPoint((int) (newLocation.getLatitude()* 1E6), (int) (newLocation.getLongitude() * 1E6));
				
				for (Event e : eventList)
				{
					EventLocation loc = e.getLocation();
					
					String eventName = e.getName();
					
					float[] distanceResult = new float[3];
					
					Location.distanceBetween(newLocation.getLatitude(), newLocation.getLongitude(), 
							Double.parseDouble(loc.getLatitude()), Double.parseDouble(loc.getLongitude()), distanceResult);
					
					double distance = distanceResult[0];
					
					if (distance < 800)
						Toast.makeText(NearEventsActivity.this, eventName + " : " + String.format("%.2fm", distance),
								Toast.LENGTH_SHORT).show();
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
	
	private GeoPoint stringToGeoPoint(String latitude, String longitude)
	{
		return new GeoPoint((int) (Double.parseDouble(latitude) * 1E6), (int) (Double.parseDouble(longitude) * 1E6));
	}
}
