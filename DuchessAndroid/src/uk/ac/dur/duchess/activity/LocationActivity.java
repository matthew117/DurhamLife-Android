package uk.ac.dur.duchess.activity;

import java.util.List;

import uk.ac.dur.duchess.R;
import uk.ac.dur.duchess.entity.DBAccess;
import uk.ac.dur.duchess.entity.EventLocation;
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
	private double eventLat;
	private double eventLon;
	public float bearing = 0;
	private EventLocation location;

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
		
		DBAccess database = new DBAccess(this);
		database.open();
		
		location = database.getLocation(e.getLong("location_id"));
		
		database.close();

		addressBlock.setText(location.getAddress1() + ", " + location.getAddress2() + "\n"
				+ location.getCity() + ", " + location.getPostcode());
		eventName.setText(e.getString("event_name"));

		mapView = (MapView) findViewById(R.id.mapView);
		mapView.setBuiltInZoomControls(true);

		mc = mapView.getController();

		eventLat = Double.parseDouble(location.getLatitude());
		eventLon = Double.parseDouble(location.getLongitude());

		point = new GeoPoint((int) (eventLat * 1E6), (int) (eventLon * 1E6));

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
		lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
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
		if(currentLocation != null) return (float) bearing - rotation;
		else return rotation;
	}

	private class MyLocationListener implements LocationListener
	{
		@Override
		public void onLocationChanged(Location newLocation)
		{
			if (newLocation != null)
			{
				currentLocation = new GeoPoint((int) (newLocation.getLatitude() * 1E6),
						(int) (newLocation.getLongitude() * 1E6));

				float[] distanceResult = new float[3];
				Location.distanceBetween(newLocation.getLatitude(), newLocation.getLongitude(), 
						eventLat, eventLon, distanceResult);
				
				distance = distanceResult[0];
				bearing   = distanceResult[1]; 
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
