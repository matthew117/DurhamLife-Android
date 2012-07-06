package uk.ac.dur.duchess.activity;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import uk.ac.dur.duchess.EventListAdapter;
import uk.ac.dur.duchess.R;
import uk.ac.dur.duchess.data.SessionFunctions;
import uk.ac.dur.duchess.entity.Event;
import uk.ac.dur.duchess.entity.EventXMLParser;
import uk.ac.dur.duchess.entity.User;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class SocietyEventListActivity extends Activity
{
	private ArrayList<Event> eventList;
	private EventListAdapter adapter;
	private ListView listView;
	private ProgressDialog progressDialog;
	private User user;
	private TextView societyNameText;
	
	private Button aboutButton;
	private Button subscribeButton;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.society_event_list_layout);

		user = SessionFunctions.getCurrentUser(this);

		listView = (ListView) findViewById(R.id.societyEventListView);
		societyNameText = (TextView) findViewById(R.id.nameOnSocietyEventList);
		
		aboutButton = (Button) findViewById(R.id.societyAboutButton);
		subscribeButton = (Button) findViewById(R.id.societyButtonSubscribe);
		
		Bundle s = getIntent().getExtras();
		
		aboutButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				Intent i = new Intent(v.getContext(), SocietyAboutActivity.class);
				Bundle societyInfo = getIntent().getExtras();
				
				i.putExtra("society_name", societyInfo.getString("society_name"));
				i.putExtra("society_constitution", societyInfo.getString("society_constitution"));
				i.putExtra("society_website", societyInfo.getString("society_website"));
				i.putExtra("society_email", societyInfo.getString("society_email"));
				
				startActivity(i);
				
			}
		});
		
		if (s.getString("society_name") != null)
		{
			societyNameText.setText(s.getString("society_name"));
		}

		try
		{

			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser parser = factory.newSAXParser();
			final XMLReader reader = parser.getXMLReader();

			final URL url = new URL("http://www.dur.ac.uk/cs.seg01/duchess/api/v1/events.php?society="+s.getLong("society_id"));

			Log.d("URL", url.toString());
			
			eventList = new ArrayList<Event>();

			EventXMLParser myXMLHandler = new EventXMLParser(eventList);

			reader.setContentHandler(myXMLHandler);

			adapter = new EventListAdapter(this, R.layout.custom_event_list_row, eventList);
			listView.setAdapter(adapter);

			listView.setOnItemClickListener(new OnItemClickListener()
			{
				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id)
				{

					Intent i = new Intent(view.getContext(), EventDetailsTabRootActivity.class);
					Event e = (Event) adapter.getItem(position);
					i.putExtra("event_id", e.getEventID());
					i.putExtra("event_name", e.getName());
					i.putExtra("event_start_date", e.getStartDate());
					i.putExtra("event_end_date", e.getEndDate());
					i.putExtra("event_description", e.getDescriptionHeader());
					i.putExtra("event_contact_telephone_number", e.getContactTelephoneNumber());
					i.putExtra("event_contact_email_address", e.getContactEmailAddress());
					i.putExtra("event_web_address", e.getWebAddress());
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
						InputStream is = url.openStream();					
						InputSource source = new InputSource(is);
						source.setEncoding("UTF-8");
						reader.parse(source);
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
			progressDialog = ProgressDialog.show(SocietyEventListActivity.this, "Please wait...",
					"Downloading Events ...", true);
		}
		catch (Exception ex)
		{
			progressDialog.dismiss();
			ex.printStackTrace();
			finish();
		}
	}

}
