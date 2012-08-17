package uk.ac.dur.duchess.ui.activity;

import java.util.List;

import uk.ac.dur.duchess.R;
import uk.ac.dur.duchess.io.SessionFunctions;
import uk.ac.dur.duchess.model.DurhamAffiliation;
import uk.ac.dur.duchess.model.User;
import uk.ac.dur.duchess.ui.view.EventListView;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

public class CollegeEventListActivity extends SortableListActivity
{
	private static final int COLLEGE_DIALOG_ID = 1;
	
	private ProgressDialog progressDialog;
	private AlertDialog alertDialog;
	private User user;
	private TextView collegeNameText;
	private MenuItem filterMenuItem;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.college_events_list_layout);
		
		user = SessionFunctions.getCurrentUser(this);
		
		collegeNameText = (TextView) findViewById(R.id.collegeNameOnEventList);
		
		if(user != null)
		{
			if(user.getAffiliation() == DurhamAffiliation.STAFF)
			{
				collegeNameText.setVisibility(View.GONE);
			}
			else if(user.getCollege() != null)
			{
				collegeNameText.setText(user.getCollege());
				collegeNameText.setBackgroundColor(collegeToColor(user.getCollege()));
				collegeNameText.setCompoundDrawablesWithIntrinsicBounds(
					collegeToImage(user.getCollege()), 0, 0, 0);
			}
		}
		
		listView = (EventListView) findViewById(R.id.collegeEventListView);
		listView.loadAllEvents(this, null);
		listView.setEmptyView(findViewById(R.id.collegeEventListEmpty));
	}

	@Override
	public void onDestroy()
	{
		super.onDestroy();
		if (alertDialog != null && alertDialog.isShowing()) alertDialog.dismiss();
		if (progressDialog != null && progressDialog.isShowing()) progressDialog.dismiss();
	}
	
	public boolean onCreateOptionsMenu(Menu menu)
	{
		User user = SessionFunctions.getCurrentUser(this);

		if(user.getAffiliation() == DurhamAffiliation.STAFF)
		{
			menu.add("Filter");
			filterMenuItem = menu.getItem(menu.size() - 1);
			filterMenuItem.setIcon(R.drawable.action_filter);
			filterMenuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS
					| MenuItem.SHOW_AS_ACTION_WITH_TEXT);
		}

		return super.onCreateOptionsMenu(menu);
	}

	public boolean onOptionsItemSelected(MenuItem item)
	{
		if (item.equals(filterMenuItem))
		{
			showDialog(COLLEGE_DIALOG_ID);
			return true;
		}
		
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected Dialog onCreateDialog(int id)
	{
		switch (id)
		{
			case COLLEGE_DIALOG_ID:
			{		
				List<String> colleges = SessionFunctions.getCurrentUser(this).getColleges();
	
				final CharSequence[] items = new CharSequence[colleges.size()];	
				for(int i = 0; i < items.length; i++) items[i] = colleges.get(i);
				
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setTitle("Pick a college");
				builder.setItems(items, new DialogInterface.OnClickListener()
				{
				    public void onClick(DialogInterface dialog, int item)
				    {
				        listView.filterByCollege(items[item].toString());
				    }
				});
				
				return builder.create();
			}
		}
		return null;
	}

	private int collegeToColor(String collegeName)
	{
		Resources res = getResources();
		
		if (collegeName.equals("St. Aidan's")) return res.getColor(R.color.Aidans);
		else if (collegeName.equals("Collingwood")) return res.getColor(R.color.Collingwood);
		else if (collegeName.equals("Grey")) return res.getColor(R.color.Grey);
		else if (collegeName.equals("Hatfield")) return res.getColor(R.color.Hatfield);
		else if (collegeName.equals("Josephine Butler")) return res.getColor(R.color.JB);
		else if (collegeName.equals("St. Chad's")) return res.getColor(R.color.Chads);
		else if (collegeName.equals("St. Cuthbert's")) return res.getColor(R.color.Cuthberts);
		else if (collegeName.equals("Hild Bede")) return res.getColor(R.color.HildBede);
		else if (collegeName.equals("St. John's")) return res.getColor(R.color.Johns);
		else if (collegeName.equals("St. Mary's")) return res.getColor(R.color.Marys);
		else if (collegeName.equals("Trevelyan")) return res.getColor(R.color.Trevs);
		else if (collegeName.equals("University")) return res.getColor(R.color.Castle);
		else if (collegeName.equals("Van Mildert")) return res.getColor(R.color.VanMildert);
		else if (collegeName.equals("Ustinov")) return res.getColor(R.color.Ustinov);
		else if (collegeName.equals("John Snow")) return res.getColor(R.color.JohnSnow);
		else if (collegeName.equals("Stephenson")) return res.getColor(R.color.Stephenson);
		else return 0xffffff;
	}

	private int collegeToImage(String collegeName)
	{
		if (collegeName.equals("St. Aidan's")) return R.drawable.st_aidans;
		else if (collegeName.equals("Collingwood")) return R.drawable.collingwood;
		else if (collegeName.equals("Grey")) return R.drawable.grey;
		else if (collegeName.equals("Hatfield")) return R.drawable.hatfield;
		else if (collegeName.equals("Josephine Butler")) return R.drawable.butler;
		else if (collegeName.equals("St. Chad's")) return R.drawable.chads;
		else if (collegeName.equals("St. Cuthbert's")) return R.drawable.cuthberts;
		else if (collegeName.equals("Hild Bede")) return R.drawable.hild_bede;
		else if (collegeName.equals("St. John's")) return R.drawable.st_johns;
		else if (collegeName.equals("St. Mary's")) return R.drawable.st_marys;
		else if (collegeName.equals("Trevelyan")) return R.drawable.trevelyan;
		else if (collegeName.equals("University")) return R.drawable.castle;
		else if (collegeName.equals("Van Mildert")) return R.drawable.van_mildert;
		else if (collegeName.equals("Ustinov")) return R.drawable.ustinov;
		else if (collegeName.equals("John Snow")) return R.drawable.john_snow;
		else if (collegeName.equals("Stephenson")) return R.drawable.stephenson;
		else return R.drawable.college;
	}
}
