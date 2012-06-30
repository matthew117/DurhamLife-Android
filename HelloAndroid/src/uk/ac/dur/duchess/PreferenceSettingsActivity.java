package uk.ac.dur.duchess;

import java.util.ArrayList;
import java.util.List;

import uk.ac.dur.duchess.data.SessionFunctions;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;

public class PreferenceSettingsActivity extends Activity
{
	// TODO you can only access this activity if you're signed in
	
	private User currentUser;
	private CheckBox universityCheckBox;
	private CheckBox collegeCheckBox;
	private CheckBox musicCheckBox;
	private CheckBox sportCheckBox;
	private CheckBox theatreCheckBox;
	private CheckBox exhibitionsCheckBox;
	private CheckBox conferencesCheckBox;
	private CheckBox communityCheckBox;
	private Button changeUserPreferencesButton;
	
	private Activity activity;
	
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setting_layout);

		activity = this;
		
		universityCheckBox = (CheckBox) findViewById(R.id.checkBoxUniversity);
		collegeCheckBox = (CheckBox) findViewById(R.id.checkBoxCollege);
		musicCheckBox = (CheckBox) findViewById(R.id.checkBoxMusic);
		sportCheckBox = (CheckBox) findViewById(R.id.checkBoxSport);
		theatreCheckBox = (CheckBox) findViewById(R.id.checkBoxTheatre);
		exhibitionsCheckBox = (CheckBox) findViewById(R.id.checkBoxExhibitions);
		conferencesCheckBox = (CheckBox) findViewById(R.id.checkBoxConferences);
		communityCheckBox = (CheckBox) findViewById(R.id.checkBoxCommunity);

		changeUserPreferencesButton = (Button) findViewById(R.id.changeUserPreferencesButton);
		
		currentUser = SessionFunctions.getCurrentUser(this);
		
		List<String> categoryPreferences = currentUser.getCategoryPreferences();
		
		if (categoryPreferences.contains("University")) universityCheckBox.setChecked(true);
		if (categoryPreferences.contains("College")) collegeCheckBox.setChecked(true);
		if (categoryPreferences.contains("Music")) musicCheckBox.setChecked(true);
		if (categoryPreferences.contains("Theatre")) theatreCheckBox.setChecked(true);
		if (categoryPreferences.contains("Exhibitions")) exhibitionsCheckBox.setChecked(true);
		if (categoryPreferences.contains("Sport")) sportCheckBox.setChecked(true);
		if (categoryPreferences.contains("Conferences")) conferencesCheckBox.setChecked(true);
		if (categoryPreferences.contains("Community")) communityCheckBox.setChecked(true);

		changeUserPreferencesButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				List<String> newPreferences = new ArrayList<String>();
				currentUser.getCategoryPreferences().clear();
				
				if (universityCheckBox.isChecked()) newPreferences.add("University");
				if (collegeCheckBox.isChecked()) newPreferences.add("College");
				if (musicCheckBox.isChecked()) newPreferences.add("Music");
				if (theatreCheckBox.isChecked()) newPreferences.add("Theatre");
				if (exhibitionsCheckBox.isChecked()) newPreferences.add("Exhibitions");
				if (sportCheckBox.isChecked()) newPreferences.add("Sport");
				if (conferencesCheckBox.isChecked()) newPreferences.add("Conferences");
				if (communityCheckBox.isChecked()) newPreferences.add("Community");
				
				currentUser.setCategoryPreferences(newPreferences);
				SessionFunctions.saveUserPreferences(activity, currentUser);
			}
		});
	}
}
