package uk.ac.dur.duchess.ui.activity;

import java.util.ArrayList;
import java.util.List;

import uk.ac.dur.duchess.R;
import uk.ac.dur.duchess.io.SessionHandler;
import uk.ac.dur.duchess.model.DurhamAffiliation;
import uk.ac.dur.duchess.model.User;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.actionbarsherlock.view.MenuItem;

public class SettingsActivity extends BaseActivity
{
	protected static final int REQUEST_COLLEGES = 1;

	private Spinner affiliation;
	private ViewSwitcher college;
	private boolean multipleColleges;
	private Spinner collegeSpinner;
	private Button collegeButton;
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
		currentUser = SessionHandler.getCurrentUser(activity);

		affiliation = (Spinner) findViewById(R.id.affiliationSpinner);
		college     = (ViewSwitcher) findViewById(R.id.collegeSelector);
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

		final String[] affiliations = getResources().getStringArray(R.array.durham_affiliation);
		final String[] colleges = getResources().getStringArray(R.array.college_array);
		final String[] departments = getResources().getStringArray(R.array.durham_departments);

		if(currentUser.getAffiliation() != null)
		{
			for(int i = 0; i < affiliations.length; i++)
				if(affiliations[i].equalsIgnoreCase(currentUser.getAffiliation().name()))
				{affiliation.setSelection(i); break;}
		}

		affiliation.setOnItemSelectedListener(new OnItemSelectedListener()
		{
			@Override
			public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) 
			{
				if(position != 0)
				{
					findViewById(R.id.collegeEdit).setVisibility(View.VISIBLE);
					findViewById(R.id.departmentEdit).setVisibility(View.VISIBLE);

					college.setVisibility(View.VISIBLE);
					department.setVisibility(View.VISIBLE);

					if(position == 1 && multipleColleges)
					{
						multipleColleges = false;

						if(currentUser.getColleges().size() > 0)
						{
							for(int i = 0; i < colleges.length; i++)
								if(colleges[i].equals(currentUser.getCollege()))
								{collegeSpinner.setSelection(i); break;}
						}
						else collegeSpinner.setSelection(0);
						
						college.showNext();
					}
					else if(position == 2 && !multipleColleges)
					{	
						multipleColleges = true;
						college.showNext();
					}
				}
				else
				{
					findViewById(R.id.collegeEdit).setVisibility(View.GONE);
					findViewById(R.id.departmentEdit).setVisibility(View.GONE);

					college.setVisibility(View.GONE);
					department.setVisibility(View.GONE);
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parentView) 
			{

			}
		});

		ArrayAdapter<String> collegeAdapter =
				new ArrayAdapter<String>
		(this, android.R.layout.simple_spinner_item, colleges);

		collegeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		collegeSpinner = new Spinner(this);
		collegeSpinner.setPrompt("College");
		collegeSpinner.setAdapter(collegeAdapter);
		
		collegeSpinner.setOnItemSelectedListener(new OnItemSelectedListener()
		{
			@Override
			public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) 
			{
				if(position != 0)
					currentUser.setCollege(collegeSpinner.getSelectedItem().toString());
			}

			@Override
			public void onNothingSelected(AdapterView<?> parentView) 
			{

			}
		});

		if(currentUser.getColleges().size() > 0)
		{
			for(int i = 0; i < colleges.length; i++)
				if(colleges[i].equals(currentUser.getCollege()))
				{collegeSpinner.setSelection(i); break;}
		}

		collegeButton = new Button(this);
		collegeButton.setText("Set Colleges");
		collegeButton.setTextSize(16);

		int left   = collegeButton.getPaddingLeft();
		int top    = collegeButton.getPaddingTop();
		int right  = collegeButton.getPaddingRight();
		int bottom = collegeButton.getPaddingBottom();

		collegeButton.setPadding(left, top + 10, right, bottom + 10);
		collegeButton.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
		collegeButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				SessionHandler.saveUserPreferences(activity, currentUser);
				Intent i = new Intent(v.getContext(), CollegeGridActivity.class);
				startActivityForResult(i, REQUEST_COLLEGES);
			}
		});

		college.addView(collegeButton);
		college.addView(collegeSpinner);

		if(currentUser.getDepartment() != null)
		{
			for(int i = 0; i < departments.length; i++)
				if(departments[i].equals(currentUser.getDepartment()))
				{department.setSelection(i); break;}
		}


		switch(currentUser.getAffiliation())
		{
		case STAFF:
		{
			multipleColleges = true;
			break;
		}
		case STUDENT:
		{
			college.showNext();
			multipleColleges = false;
			break;
		}
		case VISITOR:
		{
			findViewById(R.id.collegeEdit).setVisibility(View.GONE);
			findViewById(R.id.departmentEdit).setVisibility(View.GONE);

			college.setVisibility(View.GONE);
			department.setVisibility(View.GONE);

			multipleColleges = true;
			break;
		}
		}

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
	public void finish()
	{
		if(affiliation.getSelectedItem().toString().equalsIgnoreCase("Student"))
		{
			if(collegeSpinner.getSelectedItem().toString().equals("Select a college"))
			{
				Toast.makeText(activity, "Please select a college", Toast.LENGTH_SHORT).show();
				
				currentUser.setAffiliation(affiliation.getSelectedItem().toString());
				SessionHandler.saveUserPreferences(activity, currentUser);
				
				return;
			}
			else currentUser.setCollege(collegeSpinner.getSelectedItem().toString());
		}

		DurhamAffiliation _affiliation = currentUser.getAffiliation();

		currentUser.setAffiliation(affiliation.getSelectedItem().toString());
		currentUser.setDepartment(department.getSelectedItem().toString());

		List<String> newPreferences = new ArrayList<String>();
		currentUser.getCategoryPreferences().clear();

		String[] categories = getResources().getStringArray(R.array.event_categories);

		for(int i = 0; i < categories.length; i++)
			if(preferences[i].isChecked()) newPreferences.add(categories[i]);

		currentUser.setCategoryPreferences(newPreferences);

		SessionHandler.saveUserPreferences(activity, currentUser);

		Intent i = new Intent();

		if(currentUser.getAffiliation() != _affiliation) setResult(RESULT_OK);
		else setResult(RESULT_CANCELED);
		
		super.finish();
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
			case android.R.id.home:
			{
				finish();
				return true;
			}

			default: return true;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int responseCode, Intent data)
	{
		switch (requestCode)
		{
		case REQUEST_COLLEGES:
		{
			if (responseCode == RESULT_OK)
			{
				currentUser.setColleges(
						SessionHandler.getCollegesFromString(
								data.getStringExtra("colleges")));
	
				SessionHandler.saveUserPreferences(this, currentUser);
			}
			break;
		}
		default: break;
		}
	}
}
