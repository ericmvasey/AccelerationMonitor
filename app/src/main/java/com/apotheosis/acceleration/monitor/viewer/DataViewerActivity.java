package com.apotheosis.acceleration.monitor.viewer;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.androidplot.xy.XYPlot;
import com.apotheosis.acceleration.monitor.R;
import com.apotheosis.acceleration.util.FileUtilities;
import com.apotheosis.acceleration.util.LoadGraph;
import com.apotheosis.acceleration.util.TimeXYZDataPackage;

import java.io.File;
import java.lang.reflect.Method;

public class DataViewerActivity extends AppCompatActivity
{
	private TimeXYZDataPackage dp;
	private LoadGraph loadGraph;
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_data_viewer);

		if(getLastCustomNonConfigurationInstance() != null)
		{
			loadGraph = (LoadGraph) getLastCustomNonConfigurationInstance();
		}
	}

	@Override
	public void onResume()
	{
		super.onResume();
		if(getIntent().getExtras() != null && !getIntent().getExtras().isEmpty() && getIntent().getExtras().containsKey("DATA") &&
				getLastCustomNonConfigurationInstance() == null)
		{
			dp = getIntent().getExtras().getParcelable("DATA");
			loadGraph = new LoadGraph(dp, this, (XYPlot) findViewById(R.id.plot));
			loadGraph.execute( (Void[]) null);
			setTitle(dp.getTitle());
		}

		XYPlot plot = (XYPlot) findViewById(R.id.plot);
		plot.setOnLongClickListener(new View.OnLongClickListener()
		{
			@Override
			public boolean onLongClick(View v)
			{
				final AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());

				builder.setItems(new String[]{"Details", "Share", "Settings"}, new DialogInterface.OnClickListener()
				{
					@Override
					public void onClick(DialogInterface dialog, int which)
					{
						Intent i;
						dialog.dismiss();

						if(Build.VERSION.SDK_INT>=24){
							try{
								Method m = StrictMode.class.getMethod("disableDeathOnFileUriExposure");
								m.invoke(null);
							}catch(Exception e){
								e.printStackTrace();
							}
						}

						if (which == 0)
						{
							i = new Intent(DataViewerActivity.this, DataInfoPageActivity.class);
							i.putExtra("FILENAME", ((TimeXYZDataPackage) getIntent().getExtras().getParcelable("DATA")).getTitle());
							startActivity(i);
						}
						else if (which == 1)
						{
							i = new Intent(Intent.ACTION_SEND);
							i.setType("text/xml");
							i.putExtra(Intent.EXTRA_SUBJECT, "Sending " +
									dp.getTitle() + ".csv" + " as attachment");
							i.putExtra(Intent.EXTRA_TEXT, dp.getTitle() + ".csv " + "is attached.");
							File f = new File(FileUtilities.path + dp.getTitle() + ".csv");
							i.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(f));
							startActivity(Intent.createChooser(i, "Share"));
						}
						else if(which == 2)
						{
							startActivity(new Intent(DataViewerActivity.this, DataViewerSettingsActivity.class));
						}
					}
				});

				builder.show();

				return true;
			}
		});
	}

	@Override
	public Object onRetainCustomNonConfigurationInstance()
	{
		return loadGraph;
	}
}