package uk.ac.dur.duchess;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import android.app.Activity;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;

public class MainActivity extends ListActivity
{

	private ProgressDialog progressDialog;
	private boolean featureMode = true;
	private Button alphabeticalSortButton;
	private Button chronologicalSortButton;
	private Button featuredFilterButton;
	private Button categoryGridButton;
	private Button loginButton;
	private Button settingsButton;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		alphabeticalSortButton = (Button) findViewById(R.id.alphabeticalSortButton);
		chronologicalSortButton = (Button) findViewById(R.id.chronologicalSortButton);
		featuredFilterButton = (Button) findViewById(R.id.featureFilterButton);
		categoryGridButton = (Button) findViewById(R.id.categoryGridButton);
		loginButton = (Button) findViewById(R.id.loginButton);
		settingsButton = (Button) findViewById(R.id.settingsButton);

		try
		{

			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser parser = factory.newSAXParser();
			final XMLReader reader = parser.getXMLReader();

			final URL url = new URL("http://www.dur.ac.uk/cs.seg01/duchess/api/v1/events.php?n=8");

			final ArrayList<Event> eventList = new ArrayList<Event>();

			EventXMLParser myXMLHandler = new EventXMLParser(eventList);

			reader.setContentHandler(myXMLHandler);

			final EventListAdapter adapter = new EventListAdapter(this, R.layout.custom_event_list_row,
					eventList);
			setListAdapter(adapter);

			getListView().setOnItemClickListener(new OnItemClickListener()
			{
				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id)
				{

					Intent i = new Intent(view.getContext(), EventDetailsTabRootActivity.class);
					Event e = (Event) getListAdapter().getItem(position);
					i.putExtra("event_id", e.getEventID());
					i.putExtra("event_name", e.getName());
					i.putExtra("event_start_date", e.getStartDate());
					i.putExtra("event_end_date", e.getEndDate());
					i.putExtra("event_description", e.getDescriptionHeader());
					i.putExtra("event_address1", e.getAddress1());
					i.putExtra("event_address2", e.getAddress2());
					i.putExtra("event_city", e.getCity());
					i.putExtra("event_postcode", e.getPostcode());
					i.putExtra("event_latitude", e.getLatitude());
					i.putExtra("event_longitude", e.getLongitude());
					i.putExtra("image_url", e.getImageURL());
					startActivity(i);
				}
			});

			final Runnable callbackFunction = new Runnable()
			{

				@Override
				public void run()
				{
					progressDialog.dismiss();
					adapter.notifyDataSetChanged();
				}
			};

			Runnable parseData = new Runnable()
			{
				@Override
				public void run()
				{
					try
					{
						reader.parse(new InputSource(url.openStream()));
						runOnUiThread(callbackFunction);
					}
					catch (Exception ex)
					{
						ex.printStackTrace();
					}
				}
			};

			Thread thread = new Thread(null, parseData, "SAXParser");
			thread.start();
			progressDialog = ProgressDialog.show(MainActivity.this, "Please wait...",
					"Downloading Events ...", true);

			alphabeticalSortButton.setOnClickListener(new View.OnClickListener()
			{

				@Override
				public void onClick(View v)
				{
					Collections.sort(eventList, new Comparator<Event>()
					{

						@Override
						public int compare(Event obj1, Event obj2)
						{
							return obj1.getName().compareTo(obj2.getName());
						}
					});
					adapter.notifyDataSetChanged();
				}
			});

			chronologicalSortButton.setOnClickListener(new View.OnClickListener()
			{

				@Override
				public void onClick(View v)
				{
					Collections.sort(eventList, new Comparator<Event>()
					{

						@Override
						public int compare(Event e1, Event e2)
						{
							String sDateStr = e1.getStartDate();
							int sYear = Integer.parseInt(sDateStr.substring(0, 4));
							int sMonth = Integer.parseInt(sDateStr.substring(5, 7));
							int sDate = Integer.parseInt(sDateStr.substring(8, 10));

							String tDateStr = e2.getStartDate();
							int tYear = Integer.parseInt(tDateStr.substring(0, 4));
							int tMonth = Integer.parseInt(tDateStr.substring(5, 7));
							int tDate = Integer.parseInt(tDateStr.substring(8, 10));

							return ((sYear * 10000 + sMonth * 100 + sDate) - (tYear * 10000
									+ tMonth * 100 + tDate));
						}

					});
					adapter.notifyDataSetChanged();
				}
			});

			final Activity listActivity = this;
			
			featuredFilterButton.setOnClickListener(new View.OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					if (featureMode)
					{
						ArrayList<Event> featuredEvents = new ArrayList<Event>();
						for (Event event : eventList)
						{
							if (event.isFeatured()) featuredEvents.add(event);
						}
						setListAdapter(new EventListAdapter(listActivity, R.layout.custom_event_list_row, featuredEvents));
						featureMode = false;
					}
					else
					{
						setListAdapter(adapter);
						featureMode = true;
					}
				}
			});
			
			categoryGridButton.setOnClickListener(new View.OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					Intent i = new Intent(v.getContext(), CategoryGridActivity.class);
					startActivity(i);					
				}
			});
			
			loginButton.setOnClickListener(new View.OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					Intent i = new Intent(v.getContext(), LoginActivity.class);
					startActivity(i);			
				}
			});
			
			settingsButton.setOnClickListener(new View.OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					Intent i = new Intent(v.getContext(), PreferenceSettingsActivity.class);
					startActivity(i);
				}
			});

		}
		catch (Exception e)
		{
			// TODO handle error
			e.printStackTrace();
		}

	}

}