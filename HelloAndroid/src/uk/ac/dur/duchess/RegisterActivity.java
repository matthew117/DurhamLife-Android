package uk.ac.dur.duchess;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import uk.ac.dur.duchess.data.NetworkFunctions;
import uk.ac.dur.duchess.data.UserFunctions;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

public class RegisterActivity extends Activity
{
	private EditText email;
	private EditText password;
	private EditText confirm;
	private Spinner affiliation;
	private Spinner college;
	private Spinner department;
	private Button registerButton;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.register_layout);

		email = (EditText) findViewById(R.id.registerEmailEditText);
		password = (EditText) findViewById(R.id.registerPasswordEditText);
		confirm = (EditText) findViewById(R.id.registerConfirmPasswordEditText);
		affiliation = (Spinner) findViewById(R.id.affiliationSpinner);
		college = (Spinner) findViewById(R.id.collegeSpinner);
		department = (Spinner) findViewById(R.id.departmentSpinner);
		registerButton = (Button) findViewById(R.id.registerUserButton);

		registerButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				User newUser = new User();
				newUser.setForename("Mr.");
				newUser.setSurname("Guest");
				newUser.setCollege(college.getSelectedItem().toString());
				newUser.setDepartment(department.getSelectedItem().toString());
				newUser.setEmailAddress(email.getText().toString());
				newUser.setPassword(password.getText().toString());
				
				List<String> categories = new ArrayList<String>();
				categories.add("University");
				
				newUser.setCategoryPreferences(categories);

				try
				{
					NetworkFunctions.getHTTPResponseStream(
							"http://www.dur.ac.uk/cs.seg01/duchess/api/v1/users.php", "PUT",
							UserFunctions.getUserXML(newUser).getBytes());
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
