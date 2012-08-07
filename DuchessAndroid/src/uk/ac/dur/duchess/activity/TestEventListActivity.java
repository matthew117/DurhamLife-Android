package uk.ac.dur.duchess.activity;

import uk.ac.dur.duchess.EventListView;
import uk.ac.dur.duchess.R;
import uk.ac.dur.duchess.data.SessionFunctions;
import uk.ac.dur.duchess.entity.User;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.res.Resources;
import android.os.Bundle;
import android.widget.TextView;

public class TestEventListActivity extends Activity
{
	private EventListView listView;
	private ProgressDialog progressDialog;
	private AlertDialog alertDialog;
	private User user;
	private TextView collegeNameText;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.college_events_list_layout);
		
		user = SessionFunctions.getCurrentUser(this);

		collegeNameText = (TextView) findViewById(R.id.collegeNameOnEventList);

		if (user != null && user.getCollege() != null)
		{
			collegeNameText.setText(user.getCollege());
			collegeNameText.setBackgroundColor(collegeToColor(user.getCollege()));
			collegeNameText.setCompoundDrawablesWithIntrinsicBounds(
					collegeToImage(user.getCollege()), 0, 0, 0);
		}
		
		listView = (EventListView) findViewById(R.id.collegeEventListView);
		listView.loadEvents(this);
		listView.setEmptyView(findViewById(R.id.collegeEventListEmpty));
	}

	@Override
	public void onDestroy()
	{
		super.onDestroy();
		if (alertDialog != null && alertDialog.isShowing()) alertDialog.dismiss();
		if (progressDialog != null && progressDialog.isShowing()) progressDialog.dismiss();
	}
	
	@Override
	public void onResume()
	{
		super.onResume();
		listView.setAdapter(listView.getAdapter());
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
