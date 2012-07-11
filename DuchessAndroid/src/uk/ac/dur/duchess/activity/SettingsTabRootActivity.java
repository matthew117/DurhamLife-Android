
package uk.ac.dur.duchess.activity;

import uk.ac.dur.duchess.R;
import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;


public class SettingsTabRootActivity extends TabActivity
{
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);

		setContentView(R.layout.tab_root_layout);

		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.custom_title_bar);


		// TODO better icons on the tabs
		TabHost tabHost = getTabHost();

		Bundle e = getIntent().getExtras();

		TabSpec accountTab = tabHost.newTabSpec("Account");
		accountTab.setIndicator("Account", getResources().getDrawable(R.drawable.login));
		Intent accountIntent = new Intent(this, AccountSettingsActivity.class);

		accountTab.setContent(accountIntent);

		TabSpec profileTab = tabHost.newTabSpec("Profile");
		profileTab.setIndicator("Profile", getResources().getDrawable(R.drawable.community));
		Intent personalIntent = new Intent(this, ProfileSettingsActivity.class);

		profileTab.setContent(personalIntent);

		TabSpec preferencesTab = tabHost.newTabSpec("Preferences");
		preferencesTab.setIndicator("Preferences", getResources().getDrawable(R.drawable.grid));
		Intent preferenceIntent = new Intent(this, PreferenceSettingsActivity.class);

		preferencesTab.setContent(preferenceIntent);

		// Add each tab to the tab host
		tabHost.addTab(accountTab);
		tabHost.addTab(profileTab);
		tabHost.addTab(preferencesTab);
	}

}
