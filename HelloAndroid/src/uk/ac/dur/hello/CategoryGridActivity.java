package uk.ac.dur.hello;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.Toast;

public class CategoryGridActivity extends Activity
{

	GridView gridView;

	static final String[] numbers = new String[] { "A", "B", "C", "D", "E", "F", "G", "H" };

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.category_grid_layout);

		gridView = (GridView) findViewById(R.id.categoryGridViewID);

		Integer[] imageIDs = new Integer[] { R.drawable.university,
				R.drawable.college, R.drawable.music, R.drawable.theatre, R.drawable.exhibitions,
				R.drawable.sport, R.drawable.conference, R.drawable.community };
		
		gridView.setAdapter(new ImageGridAdapter(this, imageIDs));

		gridView.setOnItemClickListener(new OnItemClickListener()
		{
			public void onItemClick(AdapterView<?> parent, View v, int position, long id)
			{
				Toast.makeText(getApplicationContext(), categorySelection(position),
						Toast.LENGTH_SHORT).show();
			}
		});
		
		
	}
	
	public String categorySelection(int i)
	{
		switch (i)
		{
		case 0: return "University";
		case 1: return "College";
		case 2: return "Music";
		case 3: return "Theatre";
		case 4: return "Exhibitions";
		case 5: return "Sport";
		case 6: return "Conference";
		case 7: return "Community";
		default: return "NULL Category";
		}
	}
}
