package uk.ac.dur.duchess.activity;

import java.util.ArrayList;
import java.util.List;

import uk.ac.dur.duchess.R;
import uk.ac.dur.duchess.data.SessionFunctions;
import uk.ac.dur.duchess.entity.User;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;

public class SettingsActivity extends Activity
{
	private EditText email;
	private Button password;
	
	private EditText forename;
	private EditText surname;
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
	
	private Activity activity;
	private User currentUser;
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings_layout);
		
		activity = this;
		currentUser = SessionFunctions.getCurrentUser(activity);
		
		email    = (EditText) findViewById(R.id.editEmailEditText);
		password = (Button)   findViewById(R.id.passwordButton);
		
		forename   = (EditText) findViewById(R.id.forenameEdit);
		surname    = (EditText) findViewById(R.id.surnameEdit);
		college    = (Spinner)  findViewById(R.id.collegeSpinner);
		department = (Spinner)  findViewById(R.id.departmentSpinner);
		
		universityCheckBox  = (CheckBox) findViewById(R.id.checkBoxUniversity);
		collegeCheckBox     = (CheckBox) findViewById(R.id.checkBoxCollege);
		musicCheckBox       = (CheckBox) findViewById(R.id.checkBoxMusic);
		sportCheckBox       = (CheckBox) findViewById(R.id.checkBoxSport);
		theatreCheckBox     = (CheckBox) findViewById(R.id.checkBoxTheatre);
		exhibitionsCheckBox = (CheckBox) findViewById(R.id.checkBoxExhibitions);
		conferencesCheckBox = (CheckBox) findViewById(R.id.checkBoxConferences);
		communityCheckBox   = (CheckBox) findViewById(R.id.checkBoxCommunity);
		
		email.setText(currentUser.getEmailAddress());
		
		password.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				Intent i = new Intent(v.getContext(), AccountSettingsActivity.class);
				startActivity(i);
			}
		});
		
		String[] colleges = getResources().getStringArray(R.array.college_array);
		String[] departments = getResources().getStringArray(R.array.durham_departments);
		
		forename.setText(currentUser.getForename());
		surname.setText(currentUser.getSurname());
		
		for(int i = 0; i < colleges.length; i++)
			if(colleges[i].equals(currentUser.getCollege()))
				{college.setSelection(i); break;}
		
		for(int i = 0; i < departments.length; i++)
			if(departments[i].equals(currentUser.getDepartment()))
				{department.setSelection(i); break;}
		
		List<String> categoryPreferences = currentUser.getCategoryPreferences();
		
		if (categoryPreferences.contains("University"))  universityCheckBox.setChecked(true);
		if (categoryPreferences.contains("College"))     collegeCheckBox.setChecked(true);
		if (categoryPreferences.contains("Music"))       musicCheckBox.setChecked(true);
		if (categoryPreferences.contains("Theatre"))     theatreCheckBox.setChecked(true);
		if (categoryPreferences.contains("Exhibitions")) exhibitionsCheckBox.setChecked(true);
		if (categoryPreferences.contains("Sport"))       sportCheckBox.setChecked(true);
		if (categoryPreferences.contains("Conferences")) conferencesCheckBox.setChecked(true);
		if (categoryPreferences.contains("Community"))   communityCheckBox.setChecked(true);
	}
	
	@Override
	public void onBackPressed()
	{
		currentUser.setEmailAddress(email.getText().toString());
		
		currentUser.setForename(forename.getText().toString());
		currentUser.setSurname(surname.getText().toString());
		currentUser.setCollege(college.getSelectedItem().toString());
		currentUser.setDepartment(department.getSelectedItem().toString());
		
		List<String> newPreferences = new ArrayList<String>();
		currentUser.getCategoryPreferences().clear();
		
		if (universityCheckBox.isChecked())  newPreferences.add("University");
		if (collegeCheckBox.isChecked())     newPreferences.add("College");
		if (musicCheckBox.isChecked())       newPreferences.add("Music");
		if (theatreCheckBox.isChecked())     newPreferences.add("Theatre");
		if (exhibitionsCheckBox.isChecked()) newPreferences.add("Exhibitions");
		if (sportCheckBox.isChecked())       newPreferences.add("Sport");
		if (conferencesCheckBox.isChecked()) newPreferences.add("Conferences");
		if (communityCheckBox.isChecked())   newPreferences.add("Community");
		
		currentUser.setCategoryPreferences(newPreferences);
		
		SessionFunctions.saveUserPreferences(activity, currentUser);
		
		finish();
	}	
}
