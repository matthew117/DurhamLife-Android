package uk.ac.dur.duchess.ui.activity;

import java.util.ArrayList;
import java.util.List;

import uk.ac.dur.duchess.R;
import uk.ac.dur.duchess.io.SessionHandler;
import uk.ac.dur.duchess.model.User;
import uk.ac.dur.duchess.ui.view.ImageCheckbox;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager.LayoutParams;

public class CollegeGridActivity extends Activity
{
	private int[] iconIDs;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.college_chooser_grid);
		getWindow().setLayout(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);

		iconIDs = new int[]
			{R.id.collingwoodIconCheckbox, R.id.greyIconCheckbox,     R.id.hatfieldIconCheckbox, R.id.hildbedeIconCheckbox,
			 R.id.johnSnowIconCheckbox,    R.id.butlerIconCheckbox,   R.id.stAidansIconCheckbox, R.id.stChadsIconCheckbox,
			 R.id.cuthbertsIconCheckbox,   R.id.stJohnsIconCheckbox,  R.id.stMarysIconCheckbox,  R.id.stephensonIconCheckbox,
			 R.id.trevelyanIconCheckbox,   R.id.castleIconCheckbox,   R.id.ustinovIconCheckbox,  R.id.vanMildertIconCheckbox};
		
		String[] colleges = getResources().getStringArray(R.array.college_array);
		User user = SessionHandler.getCurrentUser(this);
		List<String> userColleges = user.getColleges();
		
		for (int i = 1; i < colleges.length; i++)
		{
			if(userColleges.contains(colleges[i]))
				((ImageCheckbox) findViewById(iconIDs[i - 1])).setChecked(true);
		}
	}
	
	@Override
	public void onBackPressed()
	{
		List<String> newColleges = new ArrayList<String>();
		String[] colleges = getResources().getStringArray(R.array.college_array);
		
		for (int i = 0; i < iconIDs.length; i++)
		{
			if(((ImageCheckbox) findViewById(iconIDs[i])).isChecked())
				newColleges.add(colleges[i + 1]);
		}
		
		Intent data = new Intent();
		data.putExtra("colleges", newColleges.toString());
		
		setResult(RESULT_OK, data);
		
		finish();
	}
}
