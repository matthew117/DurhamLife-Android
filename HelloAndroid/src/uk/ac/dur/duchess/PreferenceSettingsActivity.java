package uk.ac.dur.duchess;

import android.app.Activity;
import android.os.Bundle;
import android.widget.CheckBox;

public class PreferenceSettingsActivity extends Activity
{
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setting_layout);
		
		CheckBox universityCheckBox = (CheckBox) findViewById(R.id.checkBoxUniversity);
		CheckBox collegeCheckBox = (CheckBox) findViewById(R.id.checkBoxCollege);
		CheckBox musicCheckBox = (CheckBox) findViewById(R.id.checkBoxMusic);
		CheckBox sportCheckBox = (CheckBox) findViewById(R.id.checkBoxTheatre);
		CheckBox theatreCheckBox = (CheckBox) findViewById(R.id.checkBoxExhibitions);
		CheckBox exhibitionsCheckBox = (CheckBox) findViewById(R.id.checkBoxSport);
		CheckBox conferencesCheckBox = (CheckBox) findViewById(R.id.checkBoxConferences);
		CheckBox communityCheckBox = (CheckBox) findViewById(R.id.checkBoxCommunity);
		
	}
}
