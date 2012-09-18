package uk.ac.dur.duchess.ui.activity;

import uk.ac.dur.duchess.R;
import uk.ac.dur.duchess.util.CalendarUtils;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
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
	private static final String FACEBOOK_KEY = "facebook-credentials";

	private Facebook facebook;
	private String messageToPost = "Hi";
	private Handler handler = new Handler();

	public boolean saveCredentials(Facebook facebook)
	{
		Editor editor = getApplicationContext().getSharedPreferences(FACEBOOK_KEY, Context.MODE_PRIVATE).edit();
		
		editor.putString(TOKEN, facebook.getAccessToken());
		editor.putLong(EXPIRES, facebook.getAccessExpires());
		
		return editor.commit();
	}

	public boolean restoreCredentials(Facebook facebook)
	{
		SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(FACEBOOK_KEY, Context.MODE_PRIVATE);
		
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

		String facebookMessage = getIntent().getStringExtra("facebookMessage");
		if (facebookMessage == null) facebookMessage = "Test wall post";

		messageToPost = facebookMessage;
	}

	public void doNotShare(View button) { finish(); }
	
	public void share(View button)
	{
		if(!facebook.isSessionValid())
			facebook.authorize(this, PERMISSIONS, Facebook.FORCE_DIALOG_AUTH, new LoginDialogListener());
		else postToWall();
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
		String date = CalendarUtils.getEventDate(start_date, end_date);
		String description = e.getString("event_description");
		String image_url = e.getString("image_url");
		String webAddress = e.getString("event_web_address");
		
		Bundle params = new Bundle();
		
		params.putString("method", "POST");
		params.putString("message", name);
		params.putString("name", name);
		params.putString("caption", date);
		params.putString("link", webAddress);
		params.putString("description", description);
		params.putString("picture", image_url);
		params.putString("icon", "http://www.dur.ac.uk/matthew.bates/Duchess/img/DUFavicon.png");

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
