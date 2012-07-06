package uk.ac.dur.duchess.activity;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import uk.ac.dur.duchess.R;
import uk.ac.dur.duchess.data.SessionFunctions;
import uk.ac.dur.duchess.entity.Society;
import uk.ac.dur.duchess.entity.SocietyXMLParser;
import uk.ac.dur.duchess.entity.User;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class SocietyListActivity extends Activity
{
	private ArrayList<Society> societyList;
	private ArrayAdapter<Society> adapter;
	private ListView listView;
	private ProgressDialog progressDialog;
	private User user;
	private TextView societyNameText;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.society_list_layout);

		user = SessionFunctions.getCurrentUser(this);

		listView = (ListView) findViewById(R.id.societyListView);
		societyNameText = (TextView) findViewById(R.id.nameOnSocietyList);

		try
		{

			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser parser = factory.newSAXParser();
			final XMLReader reader = parser.getXMLReader();

			final URL url = new URL("http://www.dur.ac.uk/cs.seg01/duchess/api/v1/societies.php");

			societyList = new ArrayList<Society>();

			SocietyXMLParser myXMLHandler = new SocietyXMLParser(societyList);

			reader.setContentHandler(myXMLHandler);

			adapter = new ArrayAdapter<Society>(this, android.R.layout.simple_list_item_1, societyList);
			listView.setAdapter(adapter);

			listView.setOnItemClickListener(new OnItemClickListener()
			{
				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id)
				{

					Intent i = new Intent(view.getContext(), SocietyEventListActivity.class);
					Society s = (Society) adapter.getItem(position);
					i.putExtra("society_id", s.getSocietyID());
					i.putExtra("society_name", s.getName());
					i.putExtra("society_website", s.getWebsite());
					i.putExtra("society_email", s.getEmail());
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
			progressDialog = ProgressDialog.show(SocietyListActivity.this, "Please Wait...",
					"Downloading Society Information ...", true);
		}
		catch (Exception ex)
		{
			progressDialog.dismiss();
			ex.printStackTrace();
			finish();
		}
	}
}
