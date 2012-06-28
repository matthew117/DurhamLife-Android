package uk.ac.dur.duchess;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends Activity
{
	private EditText usernameEditText;
	private EditText passwordEditText;
	private Button registerButton;
	private Button loginButton;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_layout);

		usernameEditText = (EditText) findViewById(R.id.usernameEditText);
		passwordEditText = (EditText) findViewById(R.id.passwordEditText);
		registerButton = (Button) findViewById(R.id.registerButton);
		loginButton = (Button) findViewById(R.id.loginButton);
		
		registerButton.setClickable(true); // could possibly be moved to the xml layout file
		registerButton.setOnClickListener(new View.OnClickListener()
		{
			
			@Override
			public void onClick(View v)
			{
				Intent i = new Intent(getApplicationContext(), RegisterActivity.class);
				// TODO could maybe pass the chosen e-mail address to the registry page if they've entered one
				startActivity(i);
			}
		});
		
		loginButton.setClickable(true); // could possible be moved to the xml layout file
		loginButton.setOnClickListener(new View.OnClickListener()
		{	
			@Override
			public void onClick(View v)
			{
				// TODO login properly + error checking
				Toast.makeText(getApplicationContext(), "Login", Toast.LENGTH_LONG).show();			
			}
		});
		
	}

}
