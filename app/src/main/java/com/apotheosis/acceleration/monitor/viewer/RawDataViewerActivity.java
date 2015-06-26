package com.apotheosis.acceleration.monitor.viewer;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.ViewGroup.LayoutParams;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.apotheosis.acceleration.util.TimeXYZDataPackage;
import com.apotheosis.acceleration.monitor.R;

import java.text.DecimalFormat;
import java.util.Vector;

public class RawDataViewerActivity extends AppCompatActivity
{
	private TimeXYZDataPackage dp;
	private LoadData loadTask;

	@SuppressWarnings({ "deprecation"})
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_raw_data_page);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		if(this.getLastNonConfigurationInstance() != null)
		{
			loadTask = (LoadData) this.getLastNonConfigurationInstance();
		}
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onResume()
	{
		super.onResume();

		TableLayout table = (TableLayout) findViewById(R.id.dataSet);

		if(getIntent().getExtras() != null && getLastNonConfigurationInstance() == null)
		{
			dp = getIntent().getExtras().getParcelable("DATA");

			if(getIntent().getExtras().containsKey("LABEL"))
				setTitle(getIntent().getExtras().getString("LABEL"));
			else
				setTitle(dp.getTitle() + " - Acceleration Data");

			loadTask = new LoadData(dp, RawDataViewerActivity.this, table);
			loadTask.execute( (Void[]) null);
		}
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig)
	{
		super.onConfigurationChanged(newConfig);		
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
	}

	private class LoadData extends AsyncTask<Void, Void, Vector<TableRow>>
	{
		private TableLayout table;
		private TimeXYZDataPackage dp;
		private Activity c;
		private ProgressDialog dialog;

		public LoadData(TimeXYZDataPackage dp, Activity c, TableLayout t)
		{
			this.c = c;
			this.table = t;
			this.dp = dp;
		}

		@Override
		protected void onPreExecute()
		{

			if(!(this.table.getChildCount() > 0))
			{
				dialog = new ProgressDialog(c);
				dialog.setCancelable(false);
				dialog.setMessage("Loading, please wait...");
				dialog.show();
			}
			else
			{
				this.cancel(true);
			}
		}

		@Override
		protected Vector<TableRow> doInBackground(Void... arg0) 
		{
			Vector<TableRow> rows = new Vector<>();
			TableRow row = new TableRow(c);
			row.setLayoutParams(new LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));

			TextView timeTitle = new TextView(c),
					xTitle = new TextView(c),
					yTitle = new TextView(c),
					zTitle = new TextView(c);

			timeTitle.setText("Time (sec)");
			xTitle.setText("X (m/s^2)");
			yTitle.setText("Y (m/s^2)");
			zTitle.setText("Z (m/s^2)");

			timeTitle.setPadding(5,5,5,5);
			xTitle.setPadding(5,5,5,5);
			yTitle.setPadding(5,5,5,5);
			zTitle.setPadding(5,5,5,5);

			timeTitle.setBackgroundResource(R.drawable.cell_shape);
			xTitle.setBackgroundResource(R.drawable.cell_shape);
			yTitle.setBackgroundResource(R.drawable.cell_shape);
			zTitle.setBackgroundResource(R.drawable.cell_shape);

			row.addView(timeTitle);
			row.addView(xTitle);
			row.addView(yTitle);
			row.addView(zTitle);

			rows.add(row);

			DecimalFormat df = new DecimalFormat("0.0000");
			for(int i = 0; i < dp.getTime().size(); i++)
			{
				row = new TableRow(c);
				row.setLayoutParams(new LayoutParams(
						LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
				TextView time = new TextView(c),
						x = new TextView(c),
						y = new TextView(c),
						z = new TextView(c);

				time.setText(df.format(dp.getTime().get(i)));
				x.setText(df.format(dp.getX().get(i)));
				y.setText(df.format(dp.getY().get(i)));
				z.setText(df.format(dp.getZ().get(i)));

				time.setPadding(5, 5, 5, 5);
				x.setPadding(5, 5, 5, 5);
				y.setPadding(5, 5, 5, 5);
				z.setPadding(5, 5, 5, 5);

				time.setBackgroundResource(R.drawable.cell_shape);
				x.setBackgroundResource(R.drawable.cell_shape);
				y.setBackgroundResource(R.drawable.cell_shape);
				z.setBackgroundResource(R.drawable.cell_shape);

				row.addView(time);
				row.addView(x);
				row.addView(y);
				row.addView(z);

				rows.add(row);
			}

			return rows;
		}

		@Override
		protected void onPostExecute(Vector<TableRow> rows)
		{
			for(TableRow row: rows)
			{
				row.setGravity(Gravity.CENTER);
				table.addView(row);
			}	

			if(dialog != null)
			{
				try
				{
					dialog.dismiss();
				}
				catch(Exception e)
				{

				}
			}
		}

	}
}