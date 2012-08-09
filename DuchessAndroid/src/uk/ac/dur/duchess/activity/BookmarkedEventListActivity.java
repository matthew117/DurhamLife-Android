package uk.ac.dur.duchess.activity;

import uk.ac.dur.duchess.EventListView;
import uk.ac.dur.duchess.R;
import uk.ac.dur.duchess.data.SessionFunctions;
import uk.ac.dur.duchess.entity.User;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.os.Bundle;

import com.actionbarsherlock.app.SherlockActivity;

public class BookmarkedEventListActivity extends SherlockActivity
{
	private EventListView listView;
	private ProgressDialog progressDialog;
	private AlertDialog alertDialog;
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.bookmark_list_layout);

		listView = (EventListView) findViewById(R.id.bookmarkListView);
		listView.loadAllEvents(this, null);
		
		User user = SessionFunctions.getCurrentUser(this);
		
		if (!user.hasAnyBookmarkedEvents())
			listView.setEmptyView(findViewById(R.id.bookmarkListEmpty));
	}

	@Override
	public void onResume()
	{
		super.onResume();
		listView.setAdapter(listView.getAdapter());
	}

	@Override
	public void onDestroy()
	{
		super.onDestroy();
		if (alertDialog != null && alertDialog.isShowing()) alertDialog.dismiss();
		if (progressDialog != null && progressDialog.isShowing()) progressDialog.dismiss();
	}
}
