package uk.ac.dur.duchess;

import java.net.URL;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import uk.ac.dur.duchess.data.SessionFunctions;

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
	
	private Activity activity;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_layout);
		
		activity = this;

		usernameEditText = (EditText) findViewById(R.id.usernameEditText);
		passwordEditText = (EditText) findViewById(R.id.passwordEditText);
		registerButton = (Button) findViewById(R.id.registerButton);
		loginButton = (Button) findViewById(R.id.loginButton);

		registerButton.setOnClickListener(new View.OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				Intent i = new Intent(v.getContext(), RegisterActivity.class);
				// TODO could maybe pass the chosen e-mail address to the
				// registry page if they've entered one
				startActivity(i);
			}
		});

		loginButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				// TODO error checking
				try
				{
					SAXParserFactory factory = SAXParserFactory.newInstance();
					SAXParser parser = factory.newSAXParser();
					XMLReader xmlReader = parser.getXMLReader();

					User user = new User();

					UserXMLParser userXMLParser = new UserXMLParser(user);
					xmlReader.setContentHandler(userXMLParser);
					xmlReader.parse(new InputSource((new URL(
							"http://www.dur.ac.uk/cs.seg01/duchess/api/v1/users.php/"
									+ usernameEditText.getText())).openStream()));

					SessionFunctions.saveUserPreferences(activity, user);
					
					Toast.makeText(v.getContext(), "Hello, " + user.getForename() + " " + user.getSurname(), Toast.LENGTH_LONG).show();
				}
				catch (Exception ex)
				{
					ex.printStackTrace();
				}
			}
		});

	}

}
