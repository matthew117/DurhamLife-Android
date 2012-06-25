package uk.ac.dur.hello;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
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
import android.widget.Toast;

public class CustomEventListView extends ListActivity
{

	private ProgressDialog progressDialog;
	private boolean featureMode = true;

	private InputStream openHTTPConnection(String urlString) throws IOException
	{
		InputStream in = null;
		int response = -1;

		URL url = new URL(urlString);
		URLConnection conn = url.openConnection();

		if (!(conn instanceof HttpURLConnection)) throw new IOException("Not an HTTP Exception");

		try
		{
			HttpURLConnection httpConn = (HttpURLConnection) conn;
			httpConn.setAllowUserInteraction(false);
			httpConn.setInstanceFollowRedirects(true);
			httpConn.setRequestMethod("GET");
			httpConn.connect();
			response = httpConn.getResponseCode();
			if (response == HttpURLConnection.HTTP_OK) in = httpConn.getInputStream();
		}
		catch (Exception ex)
		{
			Toast.makeText(this, ex.getLocalizedMessage(), Toast.LENGTH_LONG).show();
			ex.printStackTrace();
			throw new IOException("Connection Error");
		}
		return in;
	}

	private String downloadText(String URL)
	{
		int BUFFER_SIZE = 2000;
		InputStream in = null;

		try
		{
			in = openHTTPConnection(URL);
		}
		catch (IOException ex)
		{
			Toast.makeText(this, ex.getLocalizedMessage(), Toast.LENGTH_LONG).show();
			ex.printStackTrace();
			return "";
		}

		InputStreamReader isr = new InputStreamReader(in);
		int charRead;
		String str = "";
		char[] inputBuffer = new char[BUFFER_SIZE];

		try
		{
			while ((charRead = isr.read(inputBuffer)) > 0)
			{
				String readString = String.copyValueOf(inputBuffer, 0, charRead);
				str += readString;
				inputBuffer = new char[BUFFER_SIZE];
			}
			in.close();
		}
		catch (Exception ex)
		{
			Toast.makeText(this, ex.getLocalizedMessage(), Toast.LENGTH_LONG).show();
			ex.printStackTrace();
			return "";
		}
		return str;
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		Button alphabeticalSortButton = (Button) findViewById(R.id.alphabeticalSortButton);
		Button chronologicalSortButton = (Button) findViewById(R.id.chronologicalSortButton);
		Button featuredFilterButton = (Button) findViewById(R.id.featureFilterButton);
		Button categoryGridButton = (Button) findViewById(R.id.categoryGridButton);
		Button loginButton = (Button) findViewById(R.id.loginButton);
		Button settingsButton = (Button) findViewById(R.id.settingsButton);

		try
		{

			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser parser = factory.newSAXParser();
			final XMLReader reader = parser.getXMLReader();

			final URL url = new URL("http://www.dur.ac.uk/cs.seg01/duchess/api/v1/events.php?n=8");

			final ArrayList<Event> eventList = new ArrayList<Event>();

			EventXMLParser myXMLHandler = new EventXMLParser(eventList);

			reader.setContentHandler(myXMLHandler);

			final EventAdapter adapter = new EventAdapter(this, R.layout.custom_event_list_row,
					eventList);
			setListAdapter(adapter);

			getListView().setOnItemClickListener(new OnItemClickListener()
			{
				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id)
				{

					Intent i = new Intent(view.getContext(), DetailsTabRoot.class);
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
			progressDialog = ProgressDialog.show(CustomEventListView.this, "Please wait...",
					"Retrieving data ...", true);

			alphabeticalSortButton.setClickable(true);
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

			chronologicalSortButton.setClickable(true);
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
			
			featuredFilterButton.setClickable(true);
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
						setListAdapter(new EventAdapter(listActivity, R.layout.custom_event_list_row, featuredEvents));
						featureMode = false;
					}
					else
					{
						setListAdapter(adapter);
						featureMode = true;
					}
				}
			});
			
			categoryGridButton.setClickable(true);
			categoryGridButton.setOnClickListener(new View.OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					Intent i = new Intent(v.getContext(), CategoryGridViewActivity.class);
					startActivity(i);					
				}
			});
			
			loginButton.setClickable(true);
			loginButton.setOnClickListener(new View.OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					Intent i = new Intent(v.getContext(), LoginActivity.class);
					startActivity(i);			
				}
			});

		}
		catch (Exception e)
		{
			System.out.println(e);
		}

	}

}