package uk.ac.dur.hello;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends Activity
{
	EditText usernameEditText;
	EditText passwordEditText;
	Button registerButton;
	Button loginButton;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_layout);

		usernameEditText = (EditText) findViewById(R.id.usernameEditText);
		passwordEditText = (EditText) findViewById(R.id.passwordEditText);
		registerButton = (Button) findViewById(R.id.registerButton);
		loginButton = (Button) findViewById(R.id.loginButton);
		
		if (registerButton == null) {Log.d("WHOA", "SHIT");}
		
		registerButton.setClickable(true);
		registerButton.setOnClickListener(new View.OnClickListener()
		{
			
			@Override
			public void onClick(View v)
			{
				Intent i = new Intent(getApplicationContext(), RegistryActivity.class);
				startActivity(i);
			}
		});
		
		loginButton.setClickable(true);
		loginButton.setOnClickListener(new View.OnClickListener()
		{
			
			@Override
			public void onClick(View v)
			{
				Toast.makeText(getApplicationContext(), "Login", Toast.LENGTH_LONG).show();			
			}
		});
		
	}

}
