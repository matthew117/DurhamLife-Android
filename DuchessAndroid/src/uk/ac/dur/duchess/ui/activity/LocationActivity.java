package uk.ac.dur.duchess.ui.activity;

import java.util.List;

import uk.ac.dur.duchess.R;
import uk.ac.dur.duchess.io.provider.DatabaseHandler;
import uk.ac.dur.duchess.model.EventLocation;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.Typeface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;

public class LocationActivity extends MapActivity implements SensorEventListener
{
	private GeoPoint point;
	private GeoPoint currentLocation;

	private ViewSwitcher mapSwitcher;
	private ImageButton mapToggle;
	private MapView mapView;
	private LinearLayout compassLayout;
	private CompassView compassView;
	private TextView compassText;
	private boolean mapMode = true;

	private MapController mc;

	private TextView addressBlock;
	private TextView eventName;

	private SensorManager mySensorManager;
	private Sensor mySensor;

	private Bitmap compass;
	private Bitmap base;
	
	private double distance = 0;
	private float bearing = 0;
	private float rotation = 0;
	
	private LocationManager locationManager;
	private LocationListener locationListener;
	
	private double eventLat;
	private double eventLon;
	
	private EventLocation location;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.event_location_layout);

		addressBlock = (TextView) findViewById(R.id.addressBlockTextView);
		eventName = (TextView) findViewById(R.id.locationEventNameLabel);

		compass = BitmapFactory.decodeResource(getResources(), R.drawable.compass_arrow_2);
		base = BitmapFactory.decodeResource(getResources(), R.drawable.compass_base_2);

		Bundle e = getIntent().getExtras();

		DatabaseHandler database = new DatabaseHandler(this);
		database.open();

		location = database.getLocation(e.getLong("location_id"));

		database.close();

		addressBlock.setText(location.getAddress1() + ", " + location.getAddress2() + "\n"
				+ location.getCity() + ", " + location.getPostcode());
		eventName.setText(e.getString("event_name"));

		mapView = (MapView) getLayoutInflater().inflate(R.layout.map_view, null, false);
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

		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		locationListener = new MyLocationListener();

		//locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

		mapView.invalidate();

		mapSwitcher = (ViewSwitcher) findViewById(R.id.mapSwitcher);
		
		mapToggle = (ImageButton) findViewById(R.id.mapToggleButton);
		mapToggle.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				mapMode = !mapMode;
				mapSwitcher.showNext();
				
				if(mapMode) mapToggle.setImageResource(R.drawable.compass_icon);
				else mapToggle.setImageResource(R.drawable.location_tab_icon);
			}
		});
		
		/*
		 * ---------------------------------------------------------------------
		 * The line below disables the compass feature, which is implemented but
		 * does not function correctly on all devices and screen resolutions
		 * --------------------------------------------------------------------- 
		 */
		mapToggle.setVisibility(View.GONE);
		
		compassLayout = new LinearLayout(this);
		compassLayout.setBackgroundColor(Color.parseColor("#FFFFFF"));
		compassLayout.setOrientation(LinearLayout.VERTICAL);
		
		compassView = new CompassView(this);
		compassView.setScaleType(ScaleType.CENTER_INSIDE);
		
		compassText = new TextView(this);
		compassText.setText(String.format("Distance to Event: Calculating..."));
		compassText.setTextColor(Color.BLACK);
		compassText.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
		compassText.setPadding(10, 30, 10, 10);
		compassText.setGravity(Gravity.CENTER_HORIZONTAL);
		
		compassLayout.addView(compassText);
		compassLayout.addView(compassView, new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		

		mapSwitcher.addView(mapView);
		mapSwitcher.addView(compassLayout, new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

		mySensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

		mySensor = mySensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
	}

	public class CompassView extends ImageView
	{	 
		public CompassView(Context context)
		{
			super(context);
		}

		@Override
		protected void onDraw(Canvas canvas)
		{
			super.onDraw(canvas);

			int width = getWidth();
			int height = getHeight();

			int x = (width  / 2) - (base.getWidth()  / 2); 
			int y = (height / 2) - (base.getHeight() / 2);

			int bx = x + (base.getWidth()  / 2) - (compass.getWidth()  / 2);
			int by = y + (base.getHeight() / 2) - (compass.getHeight() / 2);

			Matrix matrix = new Matrix();
			matrix.reset();
			matrix.setTranslate(x, y);

			canvas.drawBitmap(base, matrix, null);

			matrix.setTranslate(bx, by);
			matrix.preRotate(getCompassRotation(), (compass.getWidth() / 2), (compass.getHeight() / 2));

			canvas.drawBitmap(compass, matrix, null);
		} 
	}

	protected void onResume()
	{
		super.onResume();
		//mySensorManager.registerListener(this, mySensor, SensorManager.SENSOR_DELAY_NORMAL);
		//locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
	}

	protected void onPause()
	{
		super.onPause();
		mySensorManager.unregisterListener(this);
		locationManager.removeUpdates(locationListener);
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

			Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.google_maps_pin_purple);
			canvas.drawBitmap(bmp, screenPoint.x - (bmp.getWidth()/2), screenPoint.y - bmp.getHeight(), null);

			return true;
		}
	}

	private float getCompassRotation()
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
				bearing  = distanceResult[1];
				
				compassText.setText("Distance to Event: " + String.format("%.3f", distance) + "m");
				
				compassView.postInvalidate();
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
//		float[] R = new float[9];
//		float[] gravity = new float[3];
//		float[] geomagnetic = new float[3];
//		float[] values = new float[3];
//		boolean rotated = false;
//		
//		rotated = SensorManager.getRotationMatrix(R, null, gravity, geomagnetic);
//		values = SensorManager.getOrientation(R, values);
//		
//		Log.d("COMPASS", values[0] + ", " + values[1] + ", " + values[2]);
		
		rotation = (float) event.values[0];
		compassView.postInvalidate();
	}

}
