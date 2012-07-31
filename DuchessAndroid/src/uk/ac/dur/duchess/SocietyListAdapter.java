package uk.ac.dur.duchess;

import java.util.List;

import uk.ac.dur.duchess.entity.Society;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class SocietyListAdapter extends ArrayAdapter<Society> {

	private Context context;
	private int rowLayoutResourceID;
	
	public SocietyListAdapter(Context context, int rowLayoutResourceID, List<Society> societyList)
	{
		super(context, rowLayoutResourceID, societyList);
		this.context = context;
		this.rowLayoutResourceID = rowLayoutResourceID;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		View v = convertView;
		
		if (v == null)
		{
			LayoutInflater inflater = ((Activity) context).getLayoutInflater();
			v = inflater.inflate(rowLayoutResourceID, parent, false);
		}
		
		Society s = getItem(position);
		
		if (s != null)
		{
			TextView societyName = (TextView) v.findViewById(R.id.societyListRowSocietyName);
			TextView societyDescription = (TextView) v.findViewById(R.id.societyListRowSocietyDescription);
			
			societyName.setText(s.getName());
			societyDescription.setText(s.getConstitution());
		}
		
		return v;
	}
}
