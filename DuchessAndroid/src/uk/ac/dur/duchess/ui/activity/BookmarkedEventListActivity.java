package uk.ac.dur.duchess.ui.activity;

import uk.ac.dur.duchess.R;
import uk.ac.dur.duchess.io.SessionHandler;
import uk.ac.dur.duchess.model.User;
import uk.ac.dur.duchess.ui.view.EventListView;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.os.Bundle;

public class BookmarkedEventListActivity extends SortableListActivity
{
	private ProgressDialog progressDialog;
	private AlertDialog alertDialog;
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.bookmark_list_layout);

		listView = (EventListView) findViewById(R.id.bookmarkListView);
		listView.loadAllEvents(this, null);
		
		User user = SessionHandler.getCurrentUser(this);
		
		if (!user.hasAnyBookmarkedEvents())
			listView.setEmptyView(findViewById(R.id.bookmarkListEmpty));
	}

	@Override
	public void onDestroy()
	{
		super.onDestroy();
		if (alertDialog != null && alertDialog.isShowing()) alertDialog.dismiss();
		if (progressDialog != null && progressDialog.isShowing()) progressDialog.dismiss();
	}
}
