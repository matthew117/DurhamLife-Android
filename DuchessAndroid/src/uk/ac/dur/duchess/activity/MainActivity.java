package uk.ac.dur.duchess.activity;

import com.bugsense.trace.BugSenseHandler;

import uk.ac.dur.duchess.ApplicationGlobal;
import uk.ac.dur.duchess.data.SessionFunctions;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

public class MainActivity extends ListActivity
{
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		if (ApplicationGlobal.isFirstRun(this)) {
			ApplicationGlobal.setFirstRun(this, false);
			final AlertDialog.Builder analyticsDialog = new Builder(this);
			analyticsDialog
					.setTitle("Permissions")
					.setMessage(
							"Allow this application to send anonymous usage data.")
					.setNegativeButton("No",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int id) {
									ApplicationGlobal.setAnalyticsPermission(
											false, MainActivity.this);
									if (SessionFunctions.getCurrentUser(MainActivity.this) != null)
									{
										Intent i = new Intent(MainActivity.this, UserHubActivity.class);
										startActivity(i);
										finish();
									}
									else
									{
										Intent i = new Intent(MainActivity.this, LoginActivity.class);
										startActivity(i);
										finish();
									}
								}
							})
					.setPositiveButton("Yes",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									ApplicationGlobal.setAnalyticsPermission(
											true, MainActivity.this);
									if (SessionFunctions.getCurrentUser(MainActivity.this) != null)
									{
										Intent i = new Intent(MainActivity.this, UserHubActivity.class);
										startActivity(i);
										finish();
									}
									else
									{
										Intent i = new Intent(MainActivity.this, LoginActivity.class);
										startActivity(i);
										finish();
									}
								}
							});
			
			AlertDialog.Builder bugsenseDialog = new Builder(this);
			bugsenseDialog
					.setTitle("Permissions")
					.setMessage(
							"Allow this application to send bug/error reports.")
					.setNegativeButton("No",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int id) {
									ApplicationGlobal.setBugsensePermission(
											false, MainActivity.this);
									analyticsDialog.show();
								}
							})
					.setPositiveButton("Yes",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									ApplicationGlobal.setBugsensePermission(
											true, MainActivity.this);

										BugSenseHandler
												.setup(MainActivity.this,
														ApplicationGlobal.BUGSENSE_API_KEY);
									
									analyticsDialog.show();
								}
							});
			bugsenseDialog.show();
		}
		else
		{
		if (SessionFunctions.getCurrentUser(this) != null)
		{
			Intent i = new Intent(this, UserHubActivity.class);
			startActivity(i);
			finish();
		}
		else
		{
			Intent i = new Intent(this, LoginActivity.class);
			startActivity(i);
			finish();
		}
		}
	}
}