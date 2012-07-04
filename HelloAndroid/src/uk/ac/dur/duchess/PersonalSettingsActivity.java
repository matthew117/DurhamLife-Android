package uk.ac.dur.duchess;

import java.io.IOException;

import uk.ac.dur.duchess.data.NetworkFunctions;
import uk.ac.dur.duchess.data.SessionFunctions;
import uk.ac.dur.duchess.data.UserFunctions;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

public class PersonalSettingsActivity extends Activity
{
	private EditText forename;
	private EditText surname;
	private Spinner college;
	private Spinner department;
	private Button updateButton;

	private Activity activity;
	private User currentUser;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.personal_setting_layout);

		activity = this;
		currentUser = SessionFunctions.getCurrentUser(activity);

		forename = (EditText) findViewById(R.id.forenameEdit);
		surname = (EditText) findViewById(R.id.surnameEdit);
		college = (Spinner) findViewById(R.id.collegeSpinner);
		department = (Spinner) findViewById(R.id.departmentSpinner);
		updateButton = (Button) findViewById(R.id.updatePersonalButton);
		
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

		updateButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				currentUser.setForename(forename.getText().toString());
				currentUser.setSurname(surname.getText().toString());
				currentUser.setCollege(college.getSelectedItem().toString());
				currentUser.setDepartment(department.getSelectedItem().toString());

				try
				{
					NetworkFunctions.getHTTPResponseStream(
							"http://www.dur.ac.uk/cs.seg01/duchess/api/v1/users.php", "PUT",
							UserFunctions.getUserXML(currentUser).getBytes());

					SessionFunctions.saveUserPreferences(activity, currentUser);

					Intent i = new Intent(v.getContext(), MainActivity.class);
					startActivity(i);
				}
				catch (IOException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}
}
