package uk.ac.dur.duchess;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

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
		ImageView imageView;
		if (convertView == null){
			imageView = new ImageView(context);
			imageView.setLayoutParams(new GridView.LayoutParams(96,96));
			imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
			imageView.setPadding(8, 8, 8, 8);
		}
		else
		{
			imageView = (ImageView) convertView;
		}
		imageView.setImageResource(imageIDs[position]);
		return imageView;
	}

}
