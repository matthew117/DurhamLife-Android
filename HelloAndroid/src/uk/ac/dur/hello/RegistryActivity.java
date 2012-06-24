package uk.ac.dur.hello;

import android.app.Activity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Spinner;

public class RegistryActivity extends Activity
{
	private EditText email;
	private EditText password;
	private EditText confirm;
	private Spinner affiliation;
	private Spinner college;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.account_wizard_layout);
		
		email = (EditText) findViewById(R.id.registerEmailEditText);
		password = (EditText) findViewById(R.id.registerPasswordEditText);
		confirm = (EditText) findViewById(R.id.registerConfirmPasswordEditText);
		affiliation = (Spinner) findViewById(R.id.affiliationSpinner);
		college = (Spinner) findViewById(R.id.collegeSpinner);
	}

}
