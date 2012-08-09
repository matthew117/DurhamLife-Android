package uk.ac.dur.duchess.activity;

import java.util.ArrayList;
import java.util.List;

import com.actionbarsherlock.app.SherlockActivity;

import uk.ac.dur.duchess.GlobalApplicationData;
import uk.ac.dur.duchess.R;
import uk.ac.dur.duchess.SocietyListAdapter;
import uk.ac.dur.duchess.data.DataProvider;
import uk.ac.dur.duchess.data.SessionFunctions;
import uk.ac.dur.duchess.entity.Society;
import uk.ac.dur.duchess.entity.User;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class PersonalSocietyListActivity extends SherlockActivity
{
	private List<Society> societyList;
	private ArrayAdapter<Society> adapter;
	private ListView listView;
	private ProgressDialog progressDialog;
	private User user;
	private Context context;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.society_list_layout);

		this.context = this;

		user = SessionFunctions.getCurrentUser(this);

		listView = (ListView) findViewById(R.id.societyListView);

		adapter = new SocietyListAdapter(this, R.layout.society_list_row, new ArrayList<Society>());
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
				i.putExtra("society_constitution", s.getConstitution());
				startActivity(i);
			}
		});

		final Runnable callbackFunction = new Runnable()
		{
			@Override
			public void run()
			{
				List<String> personalSocieties = user.getSocieties();
				for (Society society : societyList)
					for(String name : personalSocieties)
					{
						if(name.equals(society.getName()))
						{
							adapter.add(society);
							break;
						}
					}
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
					GlobalApplicationData delegate = GlobalApplicationData.getInstance();
					DataProvider data = delegate.getDataProvider();
					societyList = data.getSocieties(context);
					runOnUiThread(callbackFunction);
				}
				catch (Exception ex)
				{
					ex.printStackTrace();
				}
			}
		};

		Thread thread = new Thread(null, parseData, "DownloadSocietyThread");
		thread.start();
		progressDialog = ProgressDialog.show(PersonalSocietyListActivity.this, "Please Wait...",
				"Downloading Society Information ...", true);
	}
}