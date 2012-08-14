package uk.ac.dur.duchess.ui.activity;

import uk.ac.dur.duchess.R;
import uk.ac.dur.duchess.ui.view.EventListView;
import uk.ac.dur.duchess.util.CalendarUtils;
import android.os.Bundle;

public class CalendarEventListActivity extends SortableListActivity
{
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.event_list_layout);
		
		Bundle bundle = getIntent().getExtras();
		String fromDate = bundle.getString("from_date");
		String toDate   = bundle.getString("to_date");
		setTitle(CalendarUtils.getTitleDate(fromDate));
		
		listView = (EventListView) findViewById(R.id.eventListView);
		listView.loadAllEvents(this, bundle);
		listView.setEmptyView(findViewById(R.id.eventListEmpty));
	}
}
