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
import android.widget.Toast;

public class AccountSettingsActivity extends Activity
{
	private EditText email;
	private EditText oldPassword;
	private EditText newPassword;
	private EditText confirmPassword;
	private Button updateButton;
	private Button cancelButton;
	
	private Activity activity;
	
	private User currentUser;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.account_settings_layout);

		activity = this;
		currentUser = SessionFunctions.getCurrentUser(activity);

		email = (EditText) findViewById(R.id.editEmailEditText);
		oldPassword = (EditText) findViewById(R.id.editOldPasswordEditText);
		newPassword = (EditText) findViewById(R.id.editPasswordEditText);
		confirmPassword = (EditText) findViewById(R.id.editConfirmPasswordEditText);

		updateButton = (Button) findViewById(R.id.confirmUpdateAccountButton);
		cancelButton = (Button) findViewById(R.id.cancelUpdateAccountButton);
		
		email.setText(currentUser.getEmailAddress());
		
		cancelButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v) { finish(); }
		});

		updateButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				if(newPassword.getText().toString() == null && confirmPassword.getText().toString() == null)
				{
					String newEmail = email.getText().toString();
					
					if(newEmail.equals(currentUser.getEmailAddress())) /*TODO*/;
					else
					{
						currentUser.setEmailAddress(email.getText().toString());
						saveChanges(v);
					}
					
				}		
				else if(UserFunctions.md5(oldPassword.getText().toString()).equals(currentUser.getPassword()))
				{				
					if(newPassword.getText().toString().equals(confirmPassword.getText().toString()))
					{
						currentUser.setEmailAddress(email.getText().toString());
						currentUser.setPassword(UserFunctions.md5(newPassword.getText().toString()));
		
						saveChanges(v);
					}
					else
					{
						Toast.makeText(v.getContext(), "Passwords do not match", Toast.LENGTH_LONG).show();
					}
				}
				else
				{
					Toast.makeText(v.getContext(), "Old password was incorrect", Toast.LENGTH_LONG).show();
				}
			}

			private void saveChanges(View v)
			{
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

