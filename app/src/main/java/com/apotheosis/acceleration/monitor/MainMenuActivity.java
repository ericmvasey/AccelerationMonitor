package com.apotheosis.acceleration.monitor;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.apotheosis.acceleration.monitor.adapter.FileListAdapter;
import com.apotheosis.acceleration.util.FileUtilities;
import com.apotheosis.acceleration.util.LoadData;
import com.apotheosis.acceleration.util.TimeXYZDataPackage;
import com.apotheosis.acceleration.monitor.recorder.DataCollector;
import com.apotheosis.acceleration.monitor.recorder.DataRecorderFragment;

import java.io.File;
import java.util.List;

public class MainMenuActivity extends AppCompatActivity
{
	private DataCollector collector;
	private Button pauseAccel;
	private ActionBarDrawerToggle toggle;
	@Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
		setTitle("Acceleration Monitor");

		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		File dir = new File(FileUtilities.path);

		if(!dir.isDirectory())
			dir.mkdir();

		SharedPreferences defaultPrefs = PreferenceManager.getDefaultSharedPreferences(this);

		if(!defaultPrefs.contains("LICENSES_ACCEPTED"))
		{
			defaultPrefs.edit().putBoolean("LICENSES_ACCEPTED", false).apply();
		}

		if(defaultPrefs.getBoolean("LICENSES_ACCEPTED", false))
		{
			PreferenceManager.setDefaultValues(this, R.xml.data_viewer_phone_options, false);

			setUpListView();

			getSupportFragmentManager().beginTransaction()
					.replace(R.id.recordViewContainer, new DataRecorderFragment())
					.commit();
		}
		else
		{
			licenseDialog();
		}
		DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.mainDrawerLayout);
		toggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.saved_sets_title, R.string.app_name)
		{
			private String oldTitle;
			@Override
			public void onDrawerClosed(View v)
			{
				getSupportActionBar().setTitle(oldTitle);
			}

			@Override
			public void onDrawerOpened(View v)
			{
				oldTitle = getSupportActionBar().getTitle().toString();
				getSupportActionBar().setTitle(getResources().getString(R.string.saved_sets_title));
			}
		};

		drawerLayout.setDrawerListener(toggle);
		getSupportActionBar().setElevation(0);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeButtonEnabled(true);
    }

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		if(toggle.onOptionsItemSelected(item))
			return true;

		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState)
	{
		super.onPostCreate(savedInstanceState);

		// Sync the toggle state after onRestoreInstanceState has occurred.
		toggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig)
	{
		super.onConfigurationChanged(newConfig);

		// Pass any configuration change to the drawer toggle state
		toggle.onConfigurationChanged(newConfig);
	}

	@Override
	public void onBackPressed()
	{
		DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.mainDrawerLayout);
		boolean isDrawerOpen = drawerLayout.isDrawerOpen(findViewById(R.id.side_drawer));

		if(isDrawerOpen)
			drawerLayout.closeDrawers();
		else
			super.onBackPressed();
	}

	@Override
	public void onDestroy()
	{
		stopCollection();
		super.onDestroy();
	}

	private void startCollection(String name)
	{
		collector.setFileName(name);
		collector.start();
		pauseAccel.setText("Finish");
		getSupportActionBar().setTitle("Data Collection In Progress");

		NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		NotificationCompat.Builder notification = new NotificationCompat.Builder(MainMenuActivity.this);
		notification.setSmallIcon(R.drawable.ic_launcher);
		notification.setContentTitle("Acceleration Monitor");
		notification.setContentText("Data Collection in Progress");

		Intent pendingIntent = new Intent(MainMenuActivity.this, MainMenuActivity.class);
		pendingIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);

		PendingIntent contentIntent = PendingIntent.getActivity(MainMenuActivity.this, 0,
				pendingIntent, 0);

		notification.setContentIntent(contentIntent);
		notification.setOngoing(true);

		nm.notify(R.string.app_name, notification.build());
	}

	private void stopCollection()
	{
		collector.stop();
		collector = null;
		pauseAccel.setText("Start");
		getSupportActionBar().setTitle("Data Collection Not Running");

		NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		nm.cancel(R.string.app_name);

		ListView lv = (ListView) findViewById(R.id.fileList);
		((FileListAdapter) lv.getAdapter()).refreshFiles();
	}

	@Override
	public void onResume()
	{
		super.onResume();

		if(collector == null || !collector.isRunning())
			getSupportActionBar().setTitle("Data Collection Not Running");
		else
			getSupportActionBar().setTitle("Data Collection In Progress");


		pauseAccel = (Button) findViewById(R.id.pauseSensor);
		pauseAccel.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				if (collector == null)
					collector = new DataCollector(MainMenuActivity.this);

				if (!collector.isRunning())
				{
					AlertDialog.Builder filename = new AlertDialog.Builder(MainMenuActivity.this);
					filename.setTitle("Enter Filename: ");
					final EditText name = new EditText(MainMenuActivity.this);
					filename.setView(name);

					filename.setPositiveButton("Ok", new DialogInterface.OnClickListener()
					{
						public void onClick(DialogInterface dialog, int which)
						{
							if (!name.getText().toString().equalsIgnoreCase(""))
							{
								if (!FileUtilities.fileExists(name.getText().toString()))
								{
									dialog.dismiss();
									startCollection(name.getText().toString());
								}
							}
						}
					});
					filename.show();
				}
				else
				{
					stopCollection();
				}
			}
		});
	}

	private void licenseDialog()
	{
		final SharedPreferences defaultPrefs = PreferenceManager.getDefaultSharedPreferences(this);
		final AlertDialog.Builder acceptLicenses = new AlertDialog.Builder(this);
		View v = getLayoutInflater().inflate(R.layout.alertdialog_license_prompt, null);
		acceptLicenses.setView(v);

		WebView cpol = (WebView) v.findViewById(R.id.CPOL_view),
				apache = (WebView) v.findViewById(R.id.APACHE_2_0_view);
		cpol.loadUrl("file:///android_res/raw/cpol.html");
		cpol.getSettings().setLoadWithOverviewMode(true);
		cpol.getSettings().setBuiltInZoomControls(true);
		cpol.getSettings().setUseWideViewPort(true);
		apache.loadUrl("file:///android_res/raw/apache.html");
		apache.getSettings().setLoadWithOverviewMode(true);
		apache.getSettings().setBuiltInZoomControls(true);
		apache.getSettings().setUseWideViewPort(true);

		acceptLicenses.setPositiveButton("Accept", new DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				defaultPrefs.edit().putBoolean("LICENSES_ACCEPTED", true).apply();
				dialog.dismiss();

				PreferenceManager.setDefaultValues(MainMenuActivity.this, R.xml.data_viewer_phone_options, false);

				setUpListView();

				getSupportFragmentManager().beginTransaction()
						.replace(R.id.recordViewContainer, new DataRecorderFragment())
						.commit();
			}
		});

		acceptLicenses.setNegativeButton("Decline", new DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				finish();
			}
		});

		acceptLicenses.setCancelable(false);
		acceptLicenses.show();
	}

	private void setUpListView()
	{
		ListView lv = (ListView) findViewById(R.id.fileList);

		List<String> fileNames = FileUtilities.getFileList();

		if(fileNames != null)
		{
			FileListAdapter listAdapter = new FileListAdapter(this);

			lv.setAdapter(listAdapter);
			lv.setTextFilterEnabled(true);

			lv.setOnItemClickListener(new AdapterView.OnItemClickListener()
			{
				public void onItemClick(AdapterView<?> parent, View v,
										int position, long id)
				{
					DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.mainDrawerLayout);
					boolean isDrawerOpen = drawerLayout.isDrawerOpen(findViewById(R.id.side_drawer));

					if(isDrawerOpen)
						drawerLayout.closeDrawers();

					new LoadData(TimeXYZDataPackage.DataType.ACCELERATION,
							MainMenuActivity.this,
							parent.getAdapter().getItem(position).toString()).execute((Void[]) null);
				}
			});

			lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener()
			{
				public boolean onItemLongClick(AdapterView<?> parent, View v,
											   int position, long id)
				{
					final String fileName = parent.getAdapter().getItem(position).toString();
					final AlertDialog.Builder optionsMenu = new AlertDialog.Builder(MainMenuActivity.this);
					optionsMenu.setItems(new String[]{"Open Acceleration Graph",
									"Open Raw Data",
									"Share Data",
									"Delete Data",
									"Cancel"},
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int which)
								{

									DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.mainDrawerLayout);
									boolean isDrawerOpen = drawerLayout.isDrawerOpen(findViewById(R.id.side_drawer));

									Intent i;

									switch (which)
									{
										case 0:
											dialog.dismiss();

											if(isDrawerOpen)
												drawerLayout.closeDrawers();

											new LoadData(TimeXYZDataPackage.DataType.ACCELERATION,
													MainMenuActivity.this,
													fileName).execute((Void[]) null);
											break;

										case 1:

											if(isDrawerOpen)
												drawerLayout.closeDrawers();

											new LoadData(TimeXYZDataPackage.DataType.RAW_DATA,
													MainMenuActivity.this,
													fileName).execute((Void[]) null);
											break;

										case 2:

											if(isDrawerOpen)
												drawerLayout.closeDrawers();

											i = new Intent(Intent.ACTION_SEND);
											i.setType("text/xml");
											i.putExtra(Intent.EXTRA_SUBJECT, "Sending " +
													fileName + "as attachment");
											i.putExtra(Intent.EXTRA_TEXT, fileName + "is attached.");
											File f = new File(FileUtilities.path + fileName + ".csv");
											i.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(f));
											startActivity(Intent.createChooser(i, "Choose an application..."));
											break;

										case 3:
											final AlertDialog.Builder confirmDelete = new AlertDialog.Builder(MainMenuActivity.this);
											confirmDelete.setMessage("Are you sure you want to delete " + fileName + "?");
											confirmDelete.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
												public void onClick(DialogInterface dialog, int which) {
													File f = new File(FileUtilities.path + fileName + ".csv");
													Log.d("DELETION_SUCESS", String.valueOf(f.delete()));
													setUpListView();
												}
											});
											confirmDelete.setNegativeButton("No", new DialogInterface.OnClickListener() {
												public void onClick(DialogInterface dialog, int which) {
													dialog.dismiss();
												}
											});
											confirmDelete.setOnCancelListener(new DialogInterface.OnCancelListener() {
												public void onCancel(DialogInterface dialog) {
													dialog.dismiss();
												}
											});
											confirmDelete.show();
											break;
									}
								}
							});

					optionsMenu.show();
					return true;
				}
			});
		}
	}
}