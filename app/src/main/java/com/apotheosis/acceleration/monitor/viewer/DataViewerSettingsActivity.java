package com.apotheosis.acceleration.monitor.viewer;

import android.content.Context;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

import com.apotheosis.acceleration.monitor.R;

public class DataViewerSettingsActivity extends PreferenceActivity
{
	private static final String OPTION_X_LINE_ENABLED = "X_LINE_ENABLED";
	private static final boolean OPTION_X_LINE_ENABLED_DEFAULT = true;
	private static final String OPTION_X_LINE_KEY_TITLE = "X_LINE_KEY_TITLE";
	private static String OPTION_X_LINE_KEY_TITLE_DEFAULT;
	private static final String OPTION_X_LINE_COLOR = "X_LINE_COLOR";
	private static final int OPTION_X_LINE_COLOR_DEFAULT = 0xc800;
	private static final String OPTION_X_DATA_POINTS_ENABLED = "X_DATA_POINTS_ENABLED";
	private static final boolean OPTION_X_DATA_POINTS_ENABLED_DEFAULT = false;
	
	private static final String OPTION_Y_LINE_ENABLED = "Y_LINE_ENABLED";
	private static final boolean OPTION_Y_LINE_ENABLED_DEFAULT = true;
	private static final String OPTION_Y_LINE_KEY_TITLE = "Y_LINE_KEY_TITLE";
	private static String OPTION_Y_LINE_KEY_TITLE_DEFAULT;
	private static final String OPTION_Y_LINE_COLOR = "Y_LINE_COLOR";
	private static final int OPTION_Y_LINE_COLOR_DEFAULT = 0xc80000;
	private static final String OPTION_Y_DATA_POINTS_ENABLED = "Y_DATA_POINTS_ENABLED";
	private static final boolean OPTION_Y_DATA_POINTS_ENABLED_DEFAULT = false;
	
	private static final String OPTION_Z_LINE_ENABLED = "Z_LINE_ENABLED";
	private static final boolean OPTION_Z_LINE_ENABLED_DEFAULT = true;
	private static final String OPTION_Z_LINE_KEY_TITLE = "Z_LINE_KEY_TITLE";
	private static String OPTION_Z_LINE_KEY_TITLE_DEFAULT;
	private static final String OPTION_Z_LINE_COLOR = "Z_LINE_COLOR";
	private static final int OPTION_Z_LINE_COLOR_DEFAULT = 0xc8;
	private static final String OPTION_Z_DATA_POINTS_ENABLED = "Z_DATA_POINTS_ENABLED";
	private static final boolean OPTION_Z_DATA_POINTS_ENABLED_DEFAULT = false;
	
	private static final String OPTION_X_AXIS_TITLE = "X_AXIS_TITLE";
	private static String OPTION_X_AXIS_TITLE_DEFAULT;
	private static final String OPTION_X_AXIS_STEP_VALUE = "X_AXIS_STEP_VALUE";
	private static final String OPTION_X_AXIS_STEP_VALUE_DEFAULT = "-1";
	
	private static final String OPTION_Y_AXIS_TITLE = "Y_AXIS_TITLE";
	private static String OPTION_Y_AXIS_TITLE_DEFAULT;
	
	private static final String OPTION_GRAPH_BACKGROUND_COLOR = "GRAPH_BACKGROUND_COLOR";
	private static final int OPTION_GRAPH_BACKGROUND_COLOR_DEFAULT = 0x000000;
	
	private static final String OPTION_X_DATA_POINT_COLOR = "X_DATA_POINT_COLOR";
	private static final int OPTION_X_DATA_POINT_COLOR_DEFAULT = 0xc800;
	private static final String OPTION_Y_DATA_POINT_COLOR = "Y_DATA_POINT_COLOR";
	private static final int OPTION_Y_DATA_POINT_COLOR_DEFAULT = 0xc80000;
	private static final String OPTION_Z_DATA_POINT_COLOR = "Z_DATA_POINT_COLOR";
	private static final int OPTION_Z_DATA_POINT_COLOR_DEFAULT = 0xc8;
	
	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.data_viewer_phone_options);
		PreferenceManager.setDefaultValues(this, R.xml.data_viewer_phone_options, false);
		
		EditTextPreference xLegendTitle = (EditTextPreference) findPreference(OPTION_X_LINE_KEY_TITLE);
		xLegendTitle.setText(DataViewerSettingsActivity.getXLineKeyTitle(this));
		
		EditTextPreference yLegendTitle = (EditTextPreference) findPreference(OPTION_Y_LINE_KEY_TITLE);
		yLegendTitle.setText(DataViewerSettingsActivity.getYLineKeyTitle(this));
		
		EditTextPreference zLegendTitle = (EditTextPreference) findPreference(OPTION_Z_LINE_KEY_TITLE);
		zLegendTitle.setText(DataViewerSettingsActivity.getZLineKeyTitle(this));
		
		EditTextPreference xAxisTitle = (EditTextPreference) findPreference(OPTION_X_AXIS_TITLE);
		xAxisTitle.setText(DataViewerSettingsActivity.getXAxisTitle(this));
		
		EditTextPreference yAxisTitle = (EditTextPreference) findPreference(OPTION_Y_AXIS_TITLE);
		yAxisTitle.setText(DataViewerSettingsActivity.getYAxisTitle(this));
	}
	
	public static boolean getXLineEnabledOption(Context context)
	{
		return PreferenceManager.getDefaultSharedPreferences(context).
				getBoolean(OPTION_X_LINE_ENABLED , OPTION_X_LINE_ENABLED_DEFAULT);
	}
	
	public static String getXLineKeyTitle(Context context)
	{
		return PreferenceManager.getDefaultSharedPreferences(context).
				getString(OPTION_X_LINE_KEY_TITLE, OPTION_X_LINE_KEY_TITLE_DEFAULT);
	}
	
	public static int getXLineColor(Context context)
	{
		return PreferenceManager.getDefaultSharedPreferences(context).
				getInt(OPTION_X_LINE_COLOR, OPTION_X_LINE_COLOR_DEFAULT);
	}
	
	public static boolean getXLineDataPointsEnabled(Context context)
	{
		return PreferenceManager.getDefaultSharedPreferences(context).
				getBoolean(OPTION_X_DATA_POINTS_ENABLED, OPTION_X_DATA_POINTS_ENABLED_DEFAULT);
	}
	
	public static boolean getYLineEnabledOption(Context context)
	{
		return PreferenceManager.getDefaultSharedPreferences(context).
				getBoolean(OPTION_Y_LINE_ENABLED , OPTION_Y_LINE_ENABLED_DEFAULT);
	}
	
	public static String getYLineKeyTitle(Context context)
	{
		return PreferenceManager.getDefaultSharedPreferences(context).
				getString(OPTION_Y_LINE_KEY_TITLE, OPTION_Y_LINE_KEY_TITLE_DEFAULT);
	}
	
	public static int getYLineColor(Context context)
	{
		return PreferenceManager.getDefaultSharedPreferences(context).
				getInt(OPTION_Y_LINE_COLOR, OPTION_Y_LINE_COLOR_DEFAULT);
	}
	
	public static boolean getYLineDataPointsEnabled(Context context)
	{
		return PreferenceManager.getDefaultSharedPreferences(context).
				getBoolean(OPTION_Y_DATA_POINTS_ENABLED, OPTION_Y_DATA_POINTS_ENABLED_DEFAULT);
	}	
	
	
	
	public static boolean getZLineEnabledOption(Context context)
	{
		return PreferenceManager.getDefaultSharedPreferences(context).
				getBoolean(OPTION_Z_LINE_ENABLED , OPTION_Z_LINE_ENABLED_DEFAULT);
	}
	
	public static String getZLineKeyTitle(Context context)
	{
		return PreferenceManager.getDefaultSharedPreferences(context).
				getString(OPTION_Z_LINE_KEY_TITLE, OPTION_Z_LINE_KEY_TITLE_DEFAULT);
	}
	
	public static int getZLineColor(Context context)
	{
		return PreferenceManager.getDefaultSharedPreferences(context).
				getInt(OPTION_Z_LINE_COLOR, OPTION_Z_LINE_COLOR_DEFAULT);
	}
	
	public static boolean getZLineDataPointsEnabled(Context context)
	{
		return PreferenceManager.getDefaultSharedPreferences(context).
				getBoolean(OPTION_Z_DATA_POINTS_ENABLED, OPTION_Z_DATA_POINTS_ENABLED_DEFAULT);
	}
	
	public static String getXAxisTitle(Context context)
	{
		return PreferenceManager.getDefaultSharedPreferences(context).
				getString(OPTION_X_AXIS_TITLE, OPTION_X_AXIS_TITLE_DEFAULT);
	}
	
	public static double getXAxisStepValue(Context context)
	{
		return Double.parseDouble(PreferenceManager.getDefaultSharedPreferences(context).
				getString(OPTION_X_AXIS_STEP_VALUE, OPTION_X_AXIS_STEP_VALUE_DEFAULT));
	}
	
	public static String getYAxisTitle(Context context)
	{
		return PreferenceManager.getDefaultSharedPreferences(context).
				getString(OPTION_Y_AXIS_TITLE, OPTION_Y_AXIS_TITLE_DEFAULT);
	}
	
	public static int getXDataPointColor(Context context)
	{
		return PreferenceManager.getDefaultSharedPreferences(context).
				getInt(OPTION_X_DATA_POINT_COLOR, OPTION_X_DATA_POINT_COLOR_DEFAULT);
	}
	
	public static int getYDataPointColor(Context context)
	{
		return PreferenceManager.getDefaultSharedPreferences(context).
				getInt(OPTION_Y_DATA_POINT_COLOR, OPTION_Y_DATA_POINT_COLOR_DEFAULT);
	}
	
	public static int getZDataPointColor(Context context)
	{
		return PreferenceManager.getDefaultSharedPreferences(context).
				getInt(OPTION_Z_DATA_POINT_COLOR, OPTION_Z_DATA_POINT_COLOR_DEFAULT);
	}

	public static void setOPTION_X_LINE_KEY_TITLE_DEFAULT(
			String oPTION_X_LINE_KEY_TITLE_DEFAULT) {
		OPTION_X_LINE_KEY_TITLE_DEFAULT = oPTION_X_LINE_KEY_TITLE_DEFAULT;
	}

	public static void setOPTION_Y_LINE_KEY_TITLE_DEFAULT(
			String oPTION_Y_LINE_KEY_TITLE_DEFAULT) {
		OPTION_Y_LINE_KEY_TITLE_DEFAULT = oPTION_Y_LINE_KEY_TITLE_DEFAULT;
	}

	public static void setOPTION_Z_LINE_KEY_TITLE_DEFAULT(
			String oPTION_Z_LINE_KEY_TITLE_DEFAULT) {
		OPTION_Z_LINE_KEY_TITLE_DEFAULT = oPTION_Z_LINE_KEY_TITLE_DEFAULT;
	}

	public static void setOPTION_X_AXIS_TITLE_DEFAULT(
			String oPTION_X_AXIS_TITLE_DEFAULT) {
		OPTION_X_AXIS_TITLE_DEFAULT = oPTION_X_AXIS_TITLE_DEFAULT;
	}

	public static void setOPTION_Y_AXIS_TITLE_DEFAULT(
			String oPTION_Y_AXIS_TITLE_DEFAULT) {
		OPTION_Y_AXIS_TITLE_DEFAULT = oPTION_Y_AXIS_TITLE_DEFAULT;
	}
}