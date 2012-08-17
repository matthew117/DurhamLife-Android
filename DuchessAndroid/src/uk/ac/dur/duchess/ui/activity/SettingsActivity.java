package uk.ac.dur.duchess.ui.activity;

import java.util.ArrayList;
import java.util.List;

import uk.ac.dur.duchess.R;
import uk.ac.dur.duchess.io.SessionFunctions;
import uk.ac.dur.duchess.model.DurhamAffiliation;
import uk.ac.dur.duchess.model.User;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;

public class SettingsActivity extends BaseActivity
{
	private EditText email;
	private Button password;
	
	private EditText forename;
	private EditText surname;
	private Spinner affiliation;
	private Spinner college;
	private Spinner department;
	
	private CheckBox universityCheckBox;
	private CheckBox collegeCheckBox;
	private CheckBox musicCheckBox;
	private CheckBox sportCheckBox;
	private CheckBox theatreCheckBox;
	private CheckBox exhibitionsCheckBox;
	private CheckBox conferencesCheckBox;
	private CheckBox communityCheckBox;
	private Button selectAll;
	private Button clearAll;
	
	private Activity activity;
	private User currentUser;
	
	private CheckBox[] preferences;
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings_layout);
		
		setTitle("Settings");
		
		activity = this;
		currentUser = SessionFunctions.getCurrentUser(activity);
		
		email    = (EditText) findViewById(R.id.editEmailEditText);
		password = (Button)   findViewById(R.id.passwordButton);
		
		forename   = (EditText) findViewById(R.id.forenameEdit);
		surname    = (EditText) findViewById(R.id.surnameEdit);
		affiliation = (Spinner) findViewById(R.id.affiliationSpinner);
		college     = (Spinner) findViewById(R.id.collegeSpinner);
		department  = (Spinner) findViewById(R.id.departmentSpinner);
		
		universityCheckBox  = (CheckBox) findViewById(R.id.checkBoxUniversity);
		collegeCheckBox     = (CheckBox) findViewById(R.id.checkBoxCollege);
		musicCheckBox       = (CheckBox) findViewById(R.id.checkBoxMusic);
		sportCheckBox       = (CheckBox) findViewById(R.id.checkBoxSport);
		theatreCheckBox     = (CheckBox) findViewById(R.id.checkBoxTheatre);
		exhibitionsCheckBox = (CheckBox) findViewById(R.id.checkBoxExhibitions);
		conferencesCheckBox = (CheckBox) findViewById(R.id.checkBoxConferences);
		communityCheckBox   = (CheckBox) findViewById(R.id.checkBoxCommunity);
		
		preferences = new CheckBox[]
		{
			universityCheckBox, collegeCheckBox, musicCheckBox,
			theatreCheckBox, exhibitionsCheckBox, sportCheckBox, 
			conferencesCheckBox, communityCheckBox
		};
		
		selectAll = (Button) findViewById(R.id.selectPreferencesButton);
		clearAll  = (Button) findViewById(R.id.clearPreferencesButton);
		
		email.setText(currentUser.getEmailAddress());
		
		password.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				Intent i = new Intent(v.getContext(), NewPasswordActivity.class);
				startActivity(i);
			}
		});
		
		String[] affiliations = getResources().getStringArray(R.array.durham_affiliation);
		String[] colleges = getResources().getStringArray(R.array.college_array);
		String[] departments = getResources().getStringArray(R.array.durham_departments);
		
		forename.setText(currentUser.getForename());
		surname.setText(currentUser.getSurname());
		
		for(int i = 0; i < affiliations.length; i++)
			if(affiliations[i].equalsIgnoreCase(currentUser.getAffiliation().name()))
				{affiliation.setSelection(i); break;}
		
		for(int i = 0; i < colleges.length; i++)
			if(colleges[i].equals(currentUser.getCollege()))
				{college.setSelection(i); break;}
		
		for(int i = 0; i < departments.length; i++)
			if(departments[i].equals(currentUser.getDepartment()))
				{department.setSelection(i); break;}
		
		List<String> categoryPreferences = currentUser.getCategoryPreferences();
		
		String[] categories = getResources().getStringArray(R.array.event_categories);
		
		for(int i = 0; i < categories.length; i++)
			if(categoryPreferences.contains(categories[i])) preferences[i].setChecked(true);
		
		selectAll.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				for(CheckBox box : preferences) box.setChecked(true);
			}
		});
		
		clearAll.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				for(CheckBox box : preferences) box.setChecked(false);
			}
		});
	}
	
	@Override
	public void onBackPressed()
	{
		currentUser.setEmailAddress(email.getText().toString());
		
		currentUser.setForename(forename.getText().toString());
		currentUser.setSurname(surname.getText().toString());
		currentUser.setCollege(college.getSelectedItem().toString());
		
		DurhamAffiliation _affiliation = currentUser.getAffiliation();
		
		currentUser.setAffiliation(affiliation.getSelectedItem().toString());
		currentUser.setDepartment(department.getSelectedItem().toString());
		
		List<String> newPreferences = new ArrayList<String>();
		currentUser.getCategoryPreferences().clear();
		
		String[] categories = getResources().getStringArray(R.array.event_categories);
		
		for(int i = 0; i < categories.length; i++)
			if(preferences[i].isChecked()) newPreferences.add(categories[i]);
		
		Log.d("CATEGORIES", newPreferences.toString());
		
		currentUser.setCategoryPreferences(newPreferences);
		
		SessionFunctions.saveUserPreferences(activity, currentUser);
		
		Intent i = new Intent();
		
		if(currentUser.getAffiliation() != _affiliation) setResult(RESULT_OK);
		else setResult(RESULT_CANCELED);
		
		finish();
	}	
}
