package com.apotheosis.acceleration;

import android.app.Application;

import com.apotheosis.acceleration.monitor.AnalyticsTrackers;

public class AccelerationMonitorApp extends Application
{
    @Override
    public void onCreate()
    {
        super.onCreate();
        AnalyticsTrackers.initialize(this);
    }
}
