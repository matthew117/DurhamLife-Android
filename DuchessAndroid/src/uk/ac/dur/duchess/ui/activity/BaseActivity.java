package uk.ac.dur.duchess.ui.activity;

import uk.ac.dur.duchess.R;
import android.os.Bundle;
import android.support.v4.app.NavUtils;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;

/**
 * 
 * Any activities that wishes to include a menu (action) bar should extend this
 * class.
 * 
 * Activites that extend this class will gain access to the features of Action Bar
 * Sherlock.
 * 
 * @author Jamie Bates
 *
 */
public abstract class BaseActivity extends SherlockActivity
{
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		getSupportActionBar().setHomeButtonEnabled(true);
		getSupportActionBar().setIcon(getResources().getDrawable(R.drawable.action_home));
		
		if (!(this instanceof DashboardActivity))
		{
			getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
		case android.R.id.home:
			if (this instanceof DashboardActivity) { return false; }

			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
