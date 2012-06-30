package uk.ac.dur.duchess;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ImageGridAdapter extends BaseAdapter
{
	private Context context;
	private Integer[] imageIDs;

	// TODO add category label text below the image

	public ImageGridAdapter(Context context, Integer[] imageIDs)
	{
		this.context = context;
		this.imageIDs = imageIDs;
	}

	public int getCount()
	{
		return imageIDs.length;
	}

	public Object getItem(int position)
	{
		return position;
	}

	public long getItemId(int position)
	{
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent)
	{
		TextView icon;

		View v = convertView;
		if (v == null)
		{
			LayoutInflater inflater = ((Activity) context).getLayoutInflater();
			v = inflater.inflate(R.layout.category_grid_icon, parent, false);
		}

		icon = (TextView) v.findViewById(R.id.categoryGridLabel);

		icon.setCompoundDrawablesWithIntrinsicBounds(0, imageIDs[position], 0, 0);
		icon.setText(categorySelection(position));

		return v;
	}

	private String categorySelection(int i)
	{
		switch (i)
		{
		case 0:
			return "University";
		case 1:
			return "College";
		case 2:
			return "Music";
		case 3:
			return "Theatre";
		case 4:
			return "Exhibitions";
		case 5:
			return "Sport";
		case 6:
			return "Conference";
		case 7:
			return "Community";
		default:
			return "NULL Category";
		}
	}

}
