package uk.ac.dur.duchess;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.BaseRequestListener;
import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.Facebook.DialogListener;
import com.facebook.android.FacebookError;

public class FacebookActivity extends Activity
{
	private static final String APP_ID = "305840312844748";
	private static final String[] PERMISSIONS = new String[] {"publish_stream"};

	private static final String TOKEN = "access_token";
	private static final String EXPIRES = "expires_in";
	private static final String KEY = "facebook-credentials";

	private Facebook facebook;
	private String messageToPost = "Hi";
	private ProgressDialog progress;
	private Handler handler = new Handler();

	public boolean saveCredentials(Facebook facebook)
	{
		Editor editor = getApplicationContext().getSharedPreferences(KEY, Context.MODE_PRIVATE).edit();
		
		editor.putString(TOKEN, facebook.getAccessToken());
		editor.putLong(EXPIRES, facebook.getAccessExpires());
		
		return editor.commit();
	}

	public boolean restoreCredentials(Facebook facebook)
	{
		SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(KEY, Context.MODE_PRIVATE);
		
		facebook.setAccessToken(sharedPreferences.getString(TOKEN, null));
		facebook.setAccessExpires(sharedPreferences.getLong(EXPIRES, 0));
		
		return facebook.isSessionValid();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		facebook = new Facebook(APP_ID);
		restoreCredentials(facebook);

		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.facebook_layout);
		
		progress = new ProgressDialog(this);

		String facebookMessage = getIntent().getStringExtra("facebookMessage");
		if (facebookMessage == null) facebookMessage = "Test wall post";

		messageToPost = facebookMessage;
	}

	public void doNotShare(View button) { finish(); }
	
	public void share(View button)
	{
		if(!facebook.isSessionValid()) loginAndPostToWall();
		else postToWall();
	}

	public void loginAndPostToWall()
	{
		facebook.authorize(this, PERMISSIONS, Facebook.FORCE_DIALOG_AUTH, new LoginDialogListener());
	}

	public void postToWall(String message)
	{
		Bundle parameters = new Bundle();
		parameters.putString("message", message);
		parameters.putString("description", "topic share");
		
		try
		{
			facebook.request("me");
			String response = facebook.request("me/feed", parameters, "POST");
			Log.d("Tests", "got response: " + response);
			
			if(response == null || response.equals("") || response.equals("false"))
				 showToast("Blank response.");
			else showToast("Message posted to your facebook wall!");
		}
		catch (Exception e)
		{
			showToast("Failed to post to wall!");
			e.printStackTrace();
		}
		finally { finish(); } 
	}
	
	public void postToWall()
	{
//		progress.setMessage("Posting ...");
//		progress.show();

		AsyncFacebookRunner runner = new AsyncFacebookRunner(facebook);

		Bundle e = getIntent().getExtras();
		
		String name = e.getString("event_name");
		String start_date = e.getString("event_start_date");
		String end_date = e.getString("event_end_date");
		String description = e.getString("event_description");
		String image_url = e.getString("image_url");
		
		Bundle params = new Bundle();
		
		params.putString("method", "POST");
		params.putString("message", "I'm attending " + name);
		params.putString("name", name);
		params.putString("caption", start_date);
		params.putString("link", "http://www.linkedAddress.net");
		params.putString("description", description);
		params.putString("picture", image_url);

		try
		{
			facebook.request("me");
			runner.request("me/feed", params, new WallPostListener());
		}
		catch (Exception ex)
		{
			showToast("Failed to post to wall!");
			ex.printStackTrace();
		}
		finally { finish(); } 
	}

	private final class WallPostListener extends BaseRequestListener
	{
		public void onComplete(final String response, Object state)
		{
			handler.post(new Runnable()
			{
				@Override
				public void run()
				{
//					progress.dismiss();
					Toast.makeText(FacebookActivity.this, "Posted to Facebook", Toast.LENGTH_SHORT).show();
				}
			});
		}
	}

	class LoginDialogListener implements DialogListener
	{
		public void onComplete(Bundle values)
		{
			saveCredentials(facebook);
			
			if (messageToPost != null) postToWall();
		}
		
		public void onFacebookError(FacebookError error)
		{
			showToast("Authentication with Facebook failed!");
			finish();
		}
		
		public void onError(DialogError error)
		{
			showToast("Authentication with Facebook failed!");
			finish();
		}
		
		public void onCancel()
		{
			showToast("Authentication with Facebook cancelled!");
			finish();
		}
	}

	private void showToast(String message)
	{
		Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
	}
}
