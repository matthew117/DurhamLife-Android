package uk.ac.dur.duchess;

import uk.ac.dur.hello.R;
import android.app.Activity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Spinner;

public class RegisterActivity extends Activity
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
		setContentView(R.layout.register_layout);
		
		email = (EditText) findViewById(R.id.registerEmailEditText);
		password = (EditText) findViewById(R.id.registerPasswordEditText);
		confirm = (EditText) findViewById(R.id.registerConfirmPasswordEditText);
		affiliation = (Spinner) findViewById(R.id.affiliationSpinner);
		college = (Spinner) findViewById(R.id.collegeSpinner);
	}

}
