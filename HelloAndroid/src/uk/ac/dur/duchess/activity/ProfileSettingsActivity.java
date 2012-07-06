package uk.ac.dur.duchess.activity;

import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

import uk.ac.dur.duchess.R;
import uk.ac.dur.duchess.data.NetworkFunctions;
import uk.ac.dur.duchess.data.SessionFunctions;
import uk.ac.dur.duchess.data.UserFunctions;
import uk.ac.dur.duchess.entity.User;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class ProfileSettingsActivity extends Activity
{
	private EditText forename;
	private EditText surname;
	private Spinner college;
	private Spinner department;
	private Button saveButton;
	private Button cancelButton;

	private Activity activity;
	private User currentUser;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.profile_settings_layout);

		activity = this;
		currentUser = SessionFunctions.getCurrentUser(activity);

		forename = (EditText) findViewById(R.id.forenameEdit);
		surname = (EditText) findViewById(R.id.surnameEdit);
		college = (Spinner) findViewById(R.id.collegeSpinner);
		department = (Spinner) findViewById(R.id.departmentSpinner);
		saveButton = (Button) findViewById(R.id.confirmUpdateProfileButton);
		cancelButton = (Button) findViewById(R.id.cancelUpdateProfileButton);
		
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
		
		cancelButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v) { finish(); }
		});

		saveButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				currentUser = SessionFunctions.getCurrentUser(activity);
				
				currentUser.setForename(forename.getText().toString());
				currentUser.setSurname(surname.getText().toString());
				currentUser.setCollege(college.getSelectedItem().toString());
				currentUser.setDepartment(department.getSelectedItem().toString());

				try
				{
					InputStream is = NetworkFunctions.getHTTPResponseStream(
							"http://www.dur.ac.uk/cs.seg01/duchess/api/v1/users.php", "PUT",
							UserFunctions.getUserXML(currentUser).getBytes());
					
					Log.d("NEW USER XML", UserFunctions.getUserXML(currentUser));
					
					Scanner sc = new Scanner(is);
					
					while (sc.hasNextLine())
					{
						Log.d("NEW USER API RESPONSE", sc.nextLine());
					}

					SessionFunctions.saveUserPreferences(activity, currentUser);
					
					Toast.makeText(v.getContext(), "Your details have been saved", Toast.LENGTH_LONG).show();
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
