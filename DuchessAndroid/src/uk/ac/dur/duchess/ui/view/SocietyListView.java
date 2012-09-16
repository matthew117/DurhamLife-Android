package uk.ac.dur.duchess.ui.view;

import java.util.ArrayList;
import java.util.List;

import uk.ac.dur.duchess.GlobalApplicationData;
import uk.ac.dur.duchess.R;
import uk.ac.dur.duchess.io.SessionHandler;
import uk.ac.dur.duchess.io.provider.DataProvider;
import uk.ac.dur.duchess.model.Society;
import uk.ac.dur.duchess.ui.activity.PersonalSocietyListActivity;
import uk.ac.dur.duchess.ui.activity.SocietyEventListActivity;
import uk.ac.dur.duchess.ui.activity.SocietyListActivity;
import uk.ac.dur.duchess.ui.adapter.SocietyListAdapter;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

public class SocietyListView extends ListView
{
	private List<Society> societyList;
	private SocietyListAdapter adapter;

	private Runnable dataProviderThread;

	private ProgressDialog progressDialog;
	private AlertDialog alertDialog;

	public SocietyListView(Context context)
	{
		super(context);

		adapter = new SocietyListAdapter(context, R.layout.society_list_row, new ArrayList<Society>());
		setAdapter(adapter);

		societyList = new ArrayList<Society>();
		
		setDivider(new ColorDrawable(Color.BLACK));
		setDividerHeight(1);

		setOnItemClickListener(new OnItemClickListener()
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
				getContext().startActivity(i);
			}
		});
	}

	public SocietyListView(Context context, AttributeSet attrs)
	{
		super(context, attrs);

		adapter = new SocietyListAdapter(context, R.layout.society_list_row, new ArrayList<Society>());
		setAdapter(adapter);

		societyList = new ArrayList<Society>();

		setOnItemClickListener(new OnItemClickListener()
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
				getContext().startActivity(i);
			}
		});
	}

	public void loadSocieties(final Activity activity)
	{
		final Runnable uiThreadCallback = getCallback();
		final Runnable errorCallback = getErrorCallback(activity);

		dataProviderThread = new Runnable()
		{
			@Override
			public void run()
			{
				GlobalApplicationData delegate = GlobalApplicationData.getInstance();
				DataProvider dataPro = delegate.getDataProvider();

				if(activity instanceof SocietyListActivity)
				{
					societyList = dataPro.getSocieties(getContext());
				}
				else if(activity instanceof PersonalSocietyListActivity)
				{
					List<String> subscribedSocieties =
							SessionHandler.getCurrentUser(activity).getSocieties();

					List<Society> societies = dataPro.getSocieties(getContext());

					if(societies != null)
					{
						for (Society society : societies)
						{
							for(String subscription : subscribedSocieties)
							{
								if(subscription.equals(society.getName()))
								{
									societyList.add(society);
									break;
								}
							}
						}
					}
					else societyList = null;
				}

				if (societyList != null) activity.runOnUiThread(uiThreadCallback);
				else                     activity.runOnUiThread(errorCallback);
			}
		};

		if(!(activity instanceof PersonalSocietyListActivity))
		{
			Thread thread = new Thread(null, dataProviderThread, "DownloadSocietyThread");

			thread.start();
			progressDialog = ProgressDialog.show(getContext(), "Please wait...",
					"Downloading Society Information ...", true);
		}
		else if(SessionHandler.getCurrentUser(activity).getSocieties().size() > 0)
		{
			Thread thread = new Thread(null, dataProviderThread, "DownloadSocietyThread");

			thread.start();
			progressDialog = ProgressDialog.show(getContext(), "Please wait...",
					"Downloading Society Information ...", true);
		}
	}

	private Runnable getCallback()
	{
		Runnable uiThreadCallback = new Runnable()
		{
			@Override
			public void run()
			{
				adapter.clear();
				for (Society s : societyList) adapter.add(s);
				adapter.notifyDataSetChanged();

				progressDialog.dismiss();
			}
		};

		return uiThreadCallback;
	}

	private Runnable getErrorCallback(final Activity activity)
	{
		Runnable errorCallback = new Runnable()
		{
			@Override
			public void run()
			{
				progressDialog.dismiss();
				AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
				builder.setMessage(
						"Could not connect. Are you sure that you have an internet connection?")
						.setCancelable(false)
						.setNegativeButton("Back", new DialogInterface.OnClickListener()
						{
							public void onClick(DialogInterface dialog, int id)
							{
								activity.finish();
							}
						}).setPositiveButton("Retry", new DialogInterface.OnClickListener()
						{
							public void onClick(DialogInterface dialog, int id)
							{
								Thread thread = new Thread(null, dataProviderThread, "DownloadSocietyThread");
								thread.start();
								progressDialog = ProgressDialog.show(getContext(), "Please wait...",
										"Downloading Society Information ...", true);
							}
						});
				alertDialog = builder.create();
				alertDialog.show();
			}
		};

		return errorCallback;
	}
}
