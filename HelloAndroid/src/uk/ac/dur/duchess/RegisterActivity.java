package uk.ac.dur.duchess;

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
		registerButton = (Button) findViewById(R.id.registerButton);
		
		registerButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				User newUser = new User();
				
			}
		});
	}

}
