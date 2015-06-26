package com.apotheosis.acceleration.monitor.recorder;

import android.content.Context;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

import com.apotheosis.acceleration.monitor.R;

public class DataRecorderSettingsActivity extends PreferenceActivity
{
	private static final String OPTION_DATA_COLLECTION_RATE = "DATA_COLLECTION_RATE";
	private static final String OPTION_DATA_COLLECTION_RATE_DEFAULT = "1";
	
	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.data_recorder_phone_options);
		PreferenceManager.setDefaultValues(this, R.xml.data_recorder_phone_options, false);
	}
	
	public static int getDataCollectionRate(Context context)
	{
		return Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(context).
				getString(OPTION_DATA_COLLECTION_RATE, OPTION_DATA_COLLECTION_RATE_DEFAULT));
	}
}
