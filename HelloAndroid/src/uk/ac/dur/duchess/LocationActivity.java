package uk.ac.dur.duchess;

import java.util.List;

import uk.ac.dur.hello.R;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Point;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;

public class LocationActivity extends MapActivity
{
	private GeoPoint point;
	private GeoPoint currentLocation;
	private MapView mapView;
	private MapController mc;
	
	private TextView addressBlock;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.event_location_details_layout);
		
		addressBlock = (TextView) findViewById(R.id.addressBlockTextView);
		
		Bundle e = getIntent().getExtras();
		
		// TODO add error checking
		
		String address1 = e.getString("event_address1");
		String address2 = e.getString("event_address2");
		String city = e.getString("event_city");
		String postcode = e.getString("event_postcode");
		
		addressBlock.setText(address1 + "\n" + address2 + "\n" + city + "\n" + postcode);
		
		mapView = (MapView) findViewById(R.id.mapView);
		mapView.setBuiltInZoomControls(true);

		mc = mapView.getController();

		double lat = Double.parseDouble(e.getString("event_latitude"));
		double lng = Double.parseDouble(e.getString("event_longitude"));

		point = new GeoPoint((int) (lat * 1E6), (int) (lng * 1E6));

		mc.animateTo(point);
		mc.setZoom(18);

		MapOverlay mapOverlay = new MapOverlay();
		List<Overlay> listOfOverlays = mapView.getOverlays();
		listOfOverlays.clear();
		listOfOverlays.add(mapOverlay);

//		LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//		LocationListener locationListener = new MyLocationListener();
//
//		lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

		mapView.invalidate();
	}

	@Override
	protected boolean isRouteDisplayed()
	{
		return false;
	}

	private class MapOverlay extends com.google.android.maps.Overlay
	{
		@Override
		public boolean draw(Canvas canvas, MapView mapView, boolean shadow, long when)
		{
			super.draw(canvas, mapView, shadow);

			Point screenPoint = new Point();
			mapView.getProjection().toPixels(point, screenPoint);

			Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.arrow);
			canvas.drawBitmap(bmp, screenPoint.x-36, screenPoint.y - 50, null);

			return true;
		}
	}

	private class MyLocationListener implements LocationListener
	{
		@Override
		public void onLocationChanged(Location loc)
		{
			if (loc != null)
			{
				currentLocation = new GeoPoint((int) (loc.getLatitude() * 1E6),
						(int) (loc.getLatitude() * 1E6));

				Toast.makeText(getApplicationContext(),
						currentLocation.getLatitudeE6() * 1E6 + ", " + currentLocation.getLongitudeE6() * 1E6,
						Toast.LENGTH_LONG).show();

				mc.animateTo(currentLocation);
				mc.setZoom(18);
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

}
