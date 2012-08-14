package uk.ac.dur.duchess.ui.activity;

import uk.ac.dur.duchess.R;
import uk.ac.dur.duchess.ui.view.EventListView;
import android.app.ProgressDialog;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

public abstract class SortableListActivity extends BaseActivity
{
	protected EventListView   listView;
	protected ProgressDialog  locationFinderProgress;
	protected LocationManager locationManager;
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		getSupportMenuInflater().inflate(R.menu.sortable_list_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
		case R.id.submenuSortableListAZ:
			listView.sortAlphabetically();
			return true;
		case R.id.submenuSortableListChronological:
			listView.sortChronologically();
			return true;
		case R.id.submenuSortableListRatings:
			listView.sortByHighestReview();
			return true;
		case R.id.submenuSortableListDistance:
			locationFinderProgress = ProgressDialog.show(this, "Sorting By Distance", "Retrieving Location...");
			locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
			LocationListener locationListener = new SortEventsLocationListener();
			locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	protected class SortEventsLocationListener implements LocationListener
	{
		@Override
		public void onLocationChanged(final Location newLocation)
		{
			if (newLocation != null)
			{
				locationFinderProgress.dismiss();
				listView.sortByDistance(newLocation);
				locationManager.removeUpdates(this);
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
