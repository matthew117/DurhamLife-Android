package uk.ac.dur.duchess.ui.activity;

import java.net.URL;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import uk.ac.dur.duchess.R;
import uk.ac.dur.duchess.io.SessionFunctions;
import uk.ac.dur.duchess.io.UserFunctions;
import uk.ac.dur.duchess.io.xml.UserXMLParser;
import uk.ac.dur.duchess.model.User;
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
		
		
//		titleBarButton1.setOnClickListener(new View.OnClickListener()
//		{
//			@Override
//			public void onClick(View v)
//			{
//				new AlertDialog.Builder(LoginActivity.this)
//		        .setSingleChoiceItems(new String[]{"#7E317B","#682860","#6D28AA","#745075","#67226D"}, -1, new DialogInterface.OnClickListener()
//				{
//					@Override
//					public void onClick(DialogInterface dialog, int position)
//					{
//						dialog.dismiss();
//						LinearLayout loginScreen = (LinearLayout) findViewById(R.id.loginScreenBackground);
//						switch(position)
//						{
//							case 0: loginScreen.setBackgroundColor(Color.parseColor("#7E317B")); break;
//							case 1: loginScreen.setBackgroundColor(Color.parseColor("#682860")); break;
//							case 2: loginScreen.setBackgroundColor(Color.parseColor("#6D28AA")); break;
//							case 3: loginScreen.setBackgroundColor(Color.parseColor("#745075")); break;
//							case 4: loginScreen.setBackgroundColor(Color.parseColor("#67226D")); break;
//						}
//					}
//				})
//		        .show();
//			}
//		});

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
				
				if (usernameEditText.getText().length() == 0)
				{
					Intent i = new Intent(v.getContext(), EventListActivity.class);
					startActivity(i);
				}
				
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

					if (!user.getPassword().equalsIgnoreCase(UserFunctions.md5(passwordEditText.getText()
							.toString())))
					{
						passwordEditText.setText("");
						Toast.makeText(v.getContext(), "Incorrect Password", Toast.LENGTH_LONG).show();
					}
					else
					{
						SessionFunctions.saveUserPreferences(activity, user);

						Intent i = new Intent(v.getContext(), UserHubActivity.class);
						startActivity(i);
						finish();
					}
				}
				catch (Exception ex)
				{
					ex.printStackTrace();
				}
			}
		});

	}

}
