package uk.ac.dur.duchess.ui.activity;

import uk.ac.dur.duchess.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.TextView;

public class CategoryGridActivity extends Activity
{
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.category_grid);
		getWindow().setLayout(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);

		Integer[] iconIDs = new Integer[] { R.id.categoryGridUniversity, R.id.categoryGridCollege,
				R.id.categoryGridMusic, R.id.categoryGridTheatre, R.id.categoryGridExhibitions, R.id.categoryGridSport,
				R.id.categoryGridConferences, R.id.categoryGridCommunity };
		
		for (int id : iconIDs)
		{
			final TextView icon = (TextView) findViewById(id);
			icon.setOnClickListener(new View.OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					Intent i = new Intent();
					i.putExtra("category_filter", icon.getText());
					setResult(RESULT_OK, i);
					finish();					
				}
			});
		}

	}

}
