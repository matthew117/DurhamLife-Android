package uk.ac.dur.duchess.ui.adapter;

import uk.ac.dur.duchess.R;
import uk.ac.dur.duchess.R.id;
import uk.ac.dur.duchess.R.layout;
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
	private String[] textLabels;

	public ImageGridAdapter(Context context, Integer[] imageIDs, String[] textLabels)
	{
		this.context = context;
		this.imageIDs = imageIDs;
		this.textLabels = textLabels;
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
		icon.setText(textLabels[position]);
		
		return v;
	}

}
