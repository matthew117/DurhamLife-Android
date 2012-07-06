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
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

public class CollegeEventListActivity extends Activity
{
	private ArrayList<Event> eventList;
	private EventListAdapter adapter;
	private ListView listView;
	private ProgressDialog progressDialog;
	private User user;
	private TextView collegeNameText;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.college_events_list_layout);

		user = SessionFunctions.getCurrentUser(this);

		listView = (ListView) findViewById(R.id.collegeEventListView);
		collegeNameText = (TextView) findViewById(R.id.collegeNameOnEventList);

		if (user != null && user.getCollege() != null)
		{
			collegeNameText.setText(user.getCollege());
			collegeNameText.setBackgroundColor(collegeToColor(user.getCollege()));
		}

		try
		{

			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser parser = factory.newSAXParser();
			final XMLReader reader = parser.getXMLReader();

			final URL url = new URL("http://www.dur.ac.uk/cs.seg01/duchess/api/v1/events.php?college="+collegeToCode(user.getCollege()));

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
			progressDialog = ProgressDialog.show(CollegeEventListActivity.this, "Please wait...",
					"Downloading Events ...", true);
		}
		catch (Exception ex)
		{
			progressDialog.dismiss();
			ex.printStackTrace();
			finish();
		}
	}

	private String collegeToCode(String collegeName)
	{
		if (collegeName.equals("St. Aidan's")) return "SA";
		else if (collegeName.equals("Collingwood")) return "CW";
		else if (collegeName.equals("Grey")) return "GR";
		else if (collegeName.equals("Hatfield")) return "HAT";
		else if (collegeName.equals("Josephine Butler")) return "JB";
		else if (collegeName.equals("St. Chad's")) return "CH";
		else if (collegeName.equals("St. Cuthbert's")) return "CB";
		else if (collegeName.equals("Hild Bede")) return "HB";
		else if (collegeName.equals("St. John's")) return "JHN";
		else if (collegeName.equals("St. Mary's")) return "MRY";
		else if (collegeName.equals("Trevelyan")) return "TRV";
		else if (collegeName.equals("University")) return "UNI";
		else if (collegeName.equals("Van Mildert")) return "VM";
		else if (collegeName.equals("Ustinov")) return "UST";
		else if (collegeName.equals("John Snow")) return "JS";
		else if (collegeName.equals("Stephenson")) return "STV";
		else return null;

	}
	
	private int collegeToColor(String collegeName)
	{
		if (collegeName.equals("St. Aidan's")) return Color.parseColor("#146539");
		else if (collegeName.equals("Collingwood")) return Color.parseColor("#A01B1B");
		else if (collegeName.equals("Grey")) return Color.parseColor("#AF0B0B");
		else if (collegeName.equals("Hatfield")) return Color.parseColor("00006B");
		else if (collegeName.equals("Josephine Butler")) return Color.parseColor("990033");
		else if (collegeName.equals("St. Chad's")) return Color.parseColor("#001900");
		else if (collegeName.equals("St. Cuthbert's")) return Color.parseColor("#339999");
		else if (collegeName.equals("Hild Bede")) return Color.parseColor("#003366");
		else if (collegeName.equals("St. John's")) return Color.parseColor("#3A4C80");
		else if (collegeName.equals("St. Mary's")) return Color.parseColor("#5F248E");
		else if (collegeName.equals("Trevelyan")) return Color.parseColor("#2584BB");
		else if (collegeName.equals("University")) return Color.parseColor("#6E0803");
		else if (collegeName.equals("Van Mildert")) return Color.parseColor("#BE1727");
		else if (collegeName.equals("Ustinov")) return Color.parseColor("#3C4443");
		else if (collegeName.equals("John Snow")) return Color.parseColor("#326A89");
		else if (collegeName.equals("Stephenson")) return Color.parseColor("#BA1810");
		else return 0xffffff;
	}

}
