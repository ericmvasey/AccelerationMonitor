package com.apotheosis.acceleration.util;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;

import com.androidplot.Plot.BorderStyle;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.SimpleXYSeries.ArrayFormat;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYStepMode;
import com.apotheosis.acceleration.monitor.viewer.DataViewerSettingsActivity;

public class LoadGraph extends AsyncTask<Void, Void, Void>
{
	private TimeXYZDataPackage dp;
	private ProgressDialog dialog;
	private XYPlot plot;
	private Activity c;

	public LoadGraph(TimeXYZDataPackage dp, Activity c, XYPlot plot)
	{
		this.dp = dp;
		this.c = c;
		this.plot = plot;
		Log.d("GRAPH LOADER", "GRAPH LOADER");
	}

	@Override
	protected void onPreExecute()
	{
		dialog = new ProgressDialog(c);
		dialog.setCancelable(false);
		dialog.setMessage("Loading, please wait...");
		dialog.show();
	}

	@SuppressWarnings("deprecation")
	@Override
	protected Void doInBackground(Void... arg0) 
	{
		DataViewerSettingsActivity.setOPTION_X_AXIS_TITLE_DEFAULT("Time (sec)");
		DataViewerSettingsActivity.setOPTION_Y_AXIS_TITLE_DEFAULT("Acceleration (m/s^2)");
		DataViewerSettingsActivity.setOPTION_X_LINE_KEY_TITLE_DEFAULT("X Acceleration");
		DataViewerSettingsActivity.setOPTION_Y_LINE_KEY_TITLE_DEFAULT("Y Acceleration");
		DataViewerSettingsActivity.setOPTION_Z_LINE_KEY_TITLE_DEFAULT("Z Acceleration");

		plot.clear();
		plot.getGraphWidget().getGridBackgroundPaint().setColor(Color.BLACK);
		plot.getGraphWidget().getBackgroundPaint().setColor(Color.BLACK);

		plot.setRangeLabel(DataViewerSettingsActivity.getYAxisTitle(c));
		plot.setDomainLabel(DataViewerSettingsActivity.getXAxisTitle(c));
		plot.setTitle(dp.getTitle());

        SimpleXYSeries xSeries = new SimpleXYSeries(dp.getTimeXPaired(), ArrayFormat.XY_VALS_INTERLEAVED,
				DataViewerSettingsActivity.getXLineKeyTitle(c));

        SimpleXYSeries ySeries = new SimpleXYSeries(dp.getTimeYPaired(), ArrayFormat.XY_VALS_INTERLEAVED,
				DataViewerSettingsActivity.getYLineKeyTitle(c));

        SimpleXYSeries zSeries = new SimpleXYSeries(dp.getTimeZPaired(), ArrayFormat.XY_VALS_INTERLEAVED,
				DataViewerSettingsActivity.getZLineKeyTitle(c));


		boolean showXLine = DataViewerSettingsActivity.getXLineEnabledOption(c);
		boolean showYLine = DataViewerSettingsActivity.getYLineEnabledOption(c);
		boolean showZLine = DataViewerSettingsActivity.getZLineEnabledOption(c);
		boolean showXDataPts = DataViewerSettingsActivity.getXLineDataPointsEnabled(c);
		boolean showYDataPts = DataViewerSettingsActivity.getYLineDataPointsEnabled(c);
		boolean showZDataPts = DataViewerSettingsActivity.getZLineDataPointsEnabled(c);

		int xLineColor = Color.GREEN, yLineColor = Color.RED, zLineColor = Color.BLUE,
				xDataPtColor = Color.GREEN, yDataPtColor = Color.RED, zDataPtColor = Color.BLUE;

		if(showXLine)
		{
			if(DataViewerSettingsActivity.getXLineColor(c) != 51200)
			{
				xLineColor = DataViewerSettingsActivity.getXLineColor(c);
			}
		}
		else
			xLineColor = Color.TRANSPARENT;

		if(showXDataPts)
		{
			if(DataViewerSettingsActivity.getXDataPointColor(c) != 51200)
			{
				xDataPtColor = DataViewerSettingsActivity.getXDataPointColor(c);
			}
		}
		else
			xDataPtColor = Color.TRANSPARENT;

		if(showYLine)
		{
			if(DataViewerSettingsActivity.getYLineColor(c) != 13107200)
			{
				yLineColor = DataViewerSettingsActivity.getYLineColor(c);
			}
		}
		else
			yLineColor = Color.TRANSPARENT;

		if(showYDataPts)
		{
			if(DataViewerSettingsActivity.getYDataPointColor(c) != 13107200)
			{
				yDataPtColor = DataViewerSettingsActivity.getYDataPointColor(c);
			}
		}
		else
			yDataPtColor = Color.TRANSPARENT;

		if(showZLine)
		{
			if(DataViewerSettingsActivity.getZLineColor(c) != 200)
			{
				zLineColor = DataViewerSettingsActivity.getZLineColor(c);
			}
		}
		else
			zLineColor = Color.TRANSPARENT;

		if(showZDataPts)
		{
			if(DataViewerSettingsActivity.getZDataPointColor(c) != 200)
			{
				zDataPtColor = DataViewerSettingsActivity.getZDataPointColor(c);
			}
		}
		else
			zDataPtColor = Color.TRANSPARENT;

        LineAndPointFormatter xFormat = new LineAndPointFormatter(xLineColor, xDataPtColor, Color.TRANSPARENT, null);
        LineAndPointFormatter yFormat = new LineAndPointFormatter(yLineColor, yDataPtColor, Color.TRANSPARENT, null);
        LineAndPointFormatter zFormat = new LineAndPointFormatter(zLineColor, zDataPtColor, Color.TRANSPARENT, null);

		if(showXLine || showXDataPts)
		{
			plot.addSeries(xSeries, xFormat);
		}

		if(showYLine || showYDataPts)
		{
			plot.addSeries(ySeries, yFormat);
		}

		if(showZLine || showZDataPts)
		{
			plot.addSeries(zSeries, zFormat);
		}

		//Plot layout configurations
		plot.getGraphWidget().setTicksPerRangeLabel(1);
		plot.getGraphWidget().setTicksPerDomainLabel(1);

		double stepVal = DataViewerSettingsActivity.getXAxisStepValue(c);
		Log.d("STEPVAL", String.valueOf(stepVal));

		if(stepVal == -1.0)
		{
			double lastTimeVal = dp.getTime().get(dp.getTime().size()-1);
			Log.d("Last_TIME_VAL", String.valueOf(lastTimeVal));
			stepVal = (lastTimeVal/10.0);
		}

		plot.setDomainStep(XYStepMode.INCREMENT_BY_VAL, stepVal);
		plot.setRangeStep(XYStepMode.INCREMENT_BY_VAL, 1);
		plot.setBorderStyle(BorderStyle.SQUARE, null, null);
		plot.setPlotPadding(0,0,0,0);
		plot.setPlotMargins(0,0,0,0);

		return null;
	}

	@Override
	protected void onPostExecute(Void v)
	{
		plot.setVisibility(View.VISIBLE);
		plot.redraw();

		if(dialog != null)
		{
			try
			{
				dialog.dismiss();
			}
			catch(Exception e)
			{
                e.printStackTrace();
			}
		}
	}
}
