package uk.ac.dur.duchess.activity;

import static android.util.FloatMath.sqrt;
import static java.lang.Math.acos;

import java.util.List;

import uk.ac.dur.duchess.R;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Display;
import android.widget.TextView;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;

public class LocationActivity extends MapActivity implements SensorEventListener
{
	private GeoPoint point;
	private GeoPoint currentLocation;
	private MapView mapView;
	private MapController mc;

	private TextView addressBlock;
	private TextView eventName;
	
	private SensorManager mySensorManager;
	private Sensor mySensor;

	private Bitmap compass;
	private double distance = 0;
	private float rotation = 0;
	private LocationManager lm;
	private LocationListener locationListener;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.event_location_layout);

		addressBlock = (TextView) findViewById(R.id.addressBlockTextView);
		eventName = (TextView) findViewById(R.id.locationEventNameLabel);

		compass = BitmapFactory.decodeResource(getResources(), R.drawable.compass_arrow);

		Bundle e = getIntent().getExtras();

		// TODO add error checking

		String address1 = e.getString("event_address1");
		String address2 = e.getString("event_address2");
		String city = e.getString("event_city");
		String postcode = e.getString("event_postcode");

		addressBlock.setText(address1 + ", " + address2 + "\n" + city + ", " + postcode);
		eventName.setText(e.getString("event_name"));

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

		lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		locationListener = new MyLocationListener();

		lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);

		mapView.invalidate();

		mySensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
	
		mySensor = mySensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
	}


	protected void onResume()
	{
		super.onResume();
		mySensorManager.registerListener(this, mySensor, SensorManager.SENSOR_DELAY_NORMAL);
		lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
	}

	protected void onPause()
	{
		super.onPause();
		mySensorManager.unregisterListener(this);
		lm.removeUpdates(locationListener);
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
			canvas.drawBitmap(bmp, screenPoint.x - 36, screenPoint.y - 50, null);
			Bitmap base = BitmapFactory.decodeResource(getResources(), R.drawable.compass_base);

			Display display = getWindowManager().getDefaultDisplay(); 
			int width = canvas.getWidth();
			int height = canvas.getHeight();

			int x = width - 100;
			int y = height - 400;
			int px = x + (compass.getWidth()  / 2);
			int py = y + (compass.getHeight() / 2);
			int bx = x + (base.getWidth()  / 2) - (compass.getWidth()  / 2);
			int by = y + (base.getHeight() / 2) - (compass.getHeight() / 2);
			
			

			Matrix matrix = new Matrix();
			matrix.reset();
			matrix.setTranslate(x, y);
			
			canvas.drawBitmap(base, matrix, null);
			
			
			matrix.setTranslate(bx, by);
			matrix.preRotate(getRotation(), (compass.getWidth() / 2), (compass.getHeight() / 2));
			
			
			Paint paint = new Paint();
			paint.setStyle(Paint.Style.FILL);
			paint.setColor(Color.BLACK);
			paint.setTextSize(30);	
			
			canvas.drawText(String.format("%.3f", distance), px, py - 50, paint);
			canvas.drawBitmap(compass, matrix, null);

			return true;
		}
	}

	private float getRotation()
	{
		if(currentLocation != null)
		{
			float[] d = {(float) ((point.getLongitudeE6() / 1E6) - (currentLocation.getLongitudeE6() / 1E6)),
					(float) ((point.getLatitudeE6()  / 1E6) - (currentLocation.getLatitudeE6()  / 1E6))};

//			Log.d("LOCATION", d[0] + ", " + d[1]);
//			Log.d("ROTATION", String.valueOf(rotation));

			double angle = getAngle(d, new float[] {0, 1}); //smallest angle between translation vector and north

			if(d[1] < 0) angle = 180 - angle; //if the latitude direction is -ve (south)
			if(d[0] < 0) angle = -angle; //if the longitude direction is -ve (west)



			return (float) angle + 180 + rotation;
		}
		else return 180 + rotation;
	}

	public static double getAngle(float[] u, float[] v)
	{
		return acos(dot(u, v) / (sqrt(dot(u, u)) * sqrt(dot(v, v))));
	}

	public static float orient2D(float[] a, float[] b, float[] c)
	{
		return (a[0] - c[0]) * (b[1] - c[1]) - (a[1] - c[1]) * (b[0] - c[0]);
	}

	public static float dot(float[] u, float[] v)
	{
		float dot = 0;
		int n = u.length;

		for(int i = 0; i < n; i++) dot += (u[i] * v[i]);

		return dot;
	}

	private class MyLocationListener implements LocationListener
	{
		@Override
		public void onLocationChanged(Location loc)
		{
			if (loc != null)
			{
				currentLocation = new GeoPoint((int) (loc.getLatitude() * 1E6),
						(int) (loc.getLongitude() * 1E6));

				double R = 6371.0; // km
				double dLat = Math.toRadians((point.getLatitudeE6() / 1E6 - currentLocation.getLatitudeE6() / 1E6));
				double dLon = Math.toRadians((point.getLongitudeE6() / 1E6 - currentLocation.getLongitudeE6() / 1E6));
				double lat1 = Math.toRadians(currentLocation.getLatitudeE6() / 1E6);
				double lat2 = Math.toRadians(point.getLatitudeE6() / 1E6);

				double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.sin(dLon / 2)
						* Math.sin(dLon / 2) * Math.cos(lat1) * Math.cos(lat2);
				double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
				distance = R * c;
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

	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1)
	{

	}

	@Override
	public void onSensorChanged(SensorEvent event)
	{
		rotation = (float) event.values[0];
		
		
	}

}
