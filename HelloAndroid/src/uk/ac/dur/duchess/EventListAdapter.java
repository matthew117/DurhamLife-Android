package uk.ac.dur.duchess;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class EventListAdapter extends ArrayAdapter<Event>
{
	private ArrayList<Event> events;
	private Context context;
	private int rowLayoutResourceID;

	public EventListAdapter(Context context, int rowLayoutResourceID, ArrayList<Event> events)
	{
		super(context, rowLayoutResourceID, events);
		this.events = events;
		this.context = context;
		this.rowLayoutResourceID = rowLayoutResourceID;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		View v = convertView;
		if (v == null)
		{
			LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            v = inflater.inflate(rowLayoutResourceID, parent, false);
		}
		
		Event e = events.get(position);
		
		if (e != null)
		{
			TextView txtEventName = (TextView) v.findViewById(R.id.txtEventName);
			TextView txtEventDescription = (TextView) v.findViewById(R.id.txtEventDescription);
			TextView txtEventDate = (TextView) v.findViewById(R.id.txtEventDate);
			
			if (txtEventName != null)
			{
				txtEventName.setText(e.getName());
			}
			if (txtEventDescription != null)
			{
				txtEventDescription.setText(e.getDescriptionHeader());
			}
			if (txtEventDate != null)
			{
				try
				{
					SimpleDateFormat sourceFormat = new SimpleDateFormat("yyyy-MM-dd");
					SimpleDateFormat destinationFormat = new SimpleDateFormat("d MMMMM yyyy");
					
					Date startDate = sourceFormat.parse(e.getStartDate());
					Date endDate = sourceFormat.parse(e.getEndDate());
						
					txtEventDate.setText(
						e.getAddress1() + "\n" +
						destinationFormat.format(startDate) + " until " +
						destinationFormat.format(endDate));
				}
				catch (ParseException e1)
				{
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}	
			}
		}
		return v;
	}
}
