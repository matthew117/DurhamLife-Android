package uk.ac.dur.duchess.activity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import uk.ac.dur.duchess.R;
import uk.ac.dur.duchess.data.NetworkFunctions;
import uk.ac.dur.duchess.data.SessionFunctions;
import uk.ac.dur.duchess.data.UserFunctions;
import uk.ac.dur.duchess.entity.User;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class RegisterActivity extends Activity
{
	private EditText email;
	private EditText password;
	private EditText confirm;
	private Spinner college;
	private Spinner department;
	private Button registerButton;
	private EditText forename;
	private EditText surname;

	private Activity activity;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.register_layout);

		activity = this;

		email = (EditText) findViewById(R.id.registerEmailEditText);
		password = (EditText) findViewById(R.id.registerPasswordEditText);
		confirm = (EditText) findViewById(R.id.registerConfirmPasswordEditText);
		college = (Spinner) findViewById(R.id.collegeSpinner);
		department = (Spinner) findViewById(R.id.departmentSpinner);
		registerButton = (Button) findViewById(R.id.registerUserButton);
		forename = (EditText) findViewById(R.id.forenameRegister);
		surname = (EditText) findViewById(R.id.surnameRegister);


		registerButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				if (password.getText().toString().equals(confirm.getText().toString()))
				{
					User newUser = new User();
					newUser.setForename(forename.getText().toString());
					newUser.setSurname(surname.getText().toString());
					newUser.setCollege(college.getSelectedItem().toString());
					newUser.setDepartment(department.getSelectedItem().toString());
					newUser.setEmailAddress(email.getText().toString());
					newUser.setPassword(UserFunctions.md5(password.getText().toString()));
					List<String> categories = new ArrayList<String>();
					categories.add("University");
					newUser.setCategoryPreferences(categories);
					try
					{
						NetworkFunctions.getHTTPResponseStream(
								"http://www.dur.ac.uk/cs.seg01/duchess/api/v1/users.php", "PUT",
								UserFunctions.getUserXML(newUser).getBytes());

						SessionFunctions.saveUserPreferences(activity, newUser);

						Intent i = new Intent(v.getContext(), UserHubActivity.class);
						startActivity(i);
					}
					catch (IOException e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				else
				{
					Toast.makeText(v.getContext(), "Passwords do not match", Toast.LENGTH_LONG).show();
				}
			}
		});
	}

}
