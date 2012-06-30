package uk.ac.dur.duchess;

import java.util.ArrayList;

import uk.ac.dur.duchess.data.CalendarFunctions;
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
				txtEventDate.setText( e.getAddress1() + "\n" + CalendarFunctions.getEventDate(e));
			}
		}
		return v;
	}
}
