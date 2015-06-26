package com.apotheosis.acceleration.monitor.viewer;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYPlotZoomPan;
import com.apotheosis.acceleration.util.FileUtilities;
import com.apotheosis.acceleration.util.LoadGraph;
import com.apotheosis.acceleration.util.TimeXYZDataPackage;
import com.apotheosis.acceleration.util.TimeXYZDataPackage.DataType;
import com.apotheosis.acceleration.monitor.R;

import java.io.File;

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
			loadGraph = new LoadGraph(dp, this, (XYPlotZoomPan) findViewById(R.id.plot));
			loadGraph.execute( (Void[]) null);
			setTitle(dp.getTitle());
		}

		XYPlotZoomPan plot = (XYPlotZoomPan) findViewById(R.id.plot);
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
									dp.getTitle() + ".csv" + "as attachment");
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

		plot.setZoomEnabled(false);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.grapher_options_menu, menu);

		return true;
	}

	@Override
	public Object onRetainCustomNonConfigurationInstance()
	{
		return loadGraph;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		Intent i;
		switch(item.getItemId())
		{

		case R.id.share_data:
			i = new Intent(Intent.ACTION_SEND);
			i.setType("text/xml");
			i.putExtra(Intent.EXTRA_SUBJECT, "Sending " + 
					dp.getTitle() + ".csv" + "as attachment");
			i.putExtra(Intent.EXTRA_TEXT, dp.getTitle() +".csv " + "is attached.");
			File f = new File(FileUtilities.path + dp.getTitle() + ".csv");
			i.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(f));
			startActivity(Intent.createChooser(i, "Share"));
			break;

		case R.id.open_options_menu:
			startActivity(new Intent(DataViewerActivity.this, DataViewerSettingsActivity.class));
			break;

		case R.id.open_info_page:

			i = new Intent(DataViewerActivity.this, DataInfoPageActivity.class);
			i.putExtra("FILENAME", ((TimeXYZDataPackage) getIntent().getExtras().getParcelable("DATA")).getTitle());
			startActivity(i);

			break;

		case R.id.open_help_page:
			startActivity(new Intent(DataViewerActivity.this, DataViewerHelpPageActivity.class));
			break;

		case R.id.changeDomainBounds:
			View v = getLayoutInflater().inflate(R.layout.alertdialog_newdomain, null);
			final AlertDialog.Builder boundMod = new AlertDialog.Builder(this);
			boundMod.setView(v);
			boundMod.setTitle("Enter new domain bounds");
			
			final EditText lowerBound = (EditText) v.findViewById(R.id.lowerDomainBound),
					upperBound = (EditText) v.findViewById(R.id.upperDomainBound);
			final Button resetBounds = (Button) v.findViewById(R.id.resetBoundValuesToMax);
			
			resetBounds.setOnClickListener(new View.OnClickListener() 
			{
				public void onClick(View v) 
				{
					lowerBound.setText("0");
					upperBound.setText(String.valueOf(dp.getTime().get(dp.getTime().size()-1)));
				}
			});

			boundMod.setPositiveButton("Set", new DialogInterface.OnClickListener() 
			{
				public void onClick(DialogInterface dialog, int which) 
				{
					if( !lowerBound.getText().toString().equals("") && 
							!(Double.parseDouble(lowerBound.getText().toString()) < 0) && 
							!(Double.parseDouble(lowerBound.getText().toString()) > dp.getTime().get(dp.getTime().size()-1))  &&
							!upperBound.getText().toString().equals("") && 
							!(Double.parseDouble(upperBound.getText().toString()) < 0) && 
							!(Double.parseDouble(upperBound.getText().toString()) > dp.getTime().get(dp.getTime().size()-1)) &&
							!(Double.parseDouble(lowerBound.getText().toString()) >= Double.parseDouble(upperBound.getText().toString()))
							)
					{
						dialog.dismiss();
						int lowerIndex = 0, upperIndex = dp.getTime().size()-1;
						double bestDist = Integer.MAX_VALUE;
						
						for(int i = 0; i < dp.getTime().size(); i++)
						{
							double d = Math.abs(Double.valueOf(lowerBound.getText().toString()) - dp.getTime().get(i));
							if (d < bestDist) 
							{
								bestDist = d;
								lowerIndex = i;
							}
						}

						bestDist = Integer.MAX_VALUE;
						
						for(int i = 0; i < dp.getTime().size(); i++)
						{
							double d = Math.abs(Double.valueOf(upperBound.getText().toString()) - dp.getTime().get(i));
							if (d < bestDist) 
							{
								bestDist = d;
								upperIndex = i;
							}
						}

						TimeXYZDataPackage newPack = new TimeXYZDataPackage(
								dp.getTime().subList(lowerIndex, upperIndex),
								dp.getX().subList(lowerIndex, upperIndex), 
								dp.getY().subList(lowerIndex, upperIndex),
								dp.getZ().subList(lowerIndex, upperIndex));
						newPack.setType(DataType.ACCELERATION);
						newPack.setTitle(dp.getTitle());
						
						loadGraph = new LoadGraph(newPack, DataViewerActivity.this, (XYPlot) findViewById(R.id.plot));
						loadGraph.execute( (Void[]) null);

					}
					else
					{
						Toast.makeText(DataViewerActivity.this, "Please enter valid domain bounds.", Toast.LENGTH_LONG).show();
					}

				}
			});
			
			boundMod.setNegativeButton("Cancel", new DialogInterface.OnClickListener() 
			{
				public void onClick(DialogInterface dialog, int which) 
				{
					dialog.dismiss();
				}
			});
			
			boundMod.setOnCancelListener(new DialogInterface.OnCancelListener()
			{
				public void onCancel(DialogInterface dialog) 
				{
					dialog.dismiss();
				}
			});
			
			boundMod.show();

			break;
		}
		return true;
	}
}