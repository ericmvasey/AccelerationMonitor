package com.apotheosis.acceleration.util;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import com.apotheosis.acceleration.monitor.viewer.DataViewerActivity;
import com.apotheosis.acceleration.monitor.viewer.RawDataViewerActivity;

public class LoadData extends AsyncTask<Void, Void, TimeXYZDataPackage>
{
    private TimeXYZDataPackage.DataType loadType;
    private Context c;
    private String fileName;
    private ProgressDialog d;

    public LoadData(TimeXYZDataPackage.DataType loadType, Context c, String fileName)
    {
        this.loadType = loadType;
        this.c = c;
        this.fileName = fileName;
    }

    @Override
    protected void onPreExecute()
    {
        d = new ProgressDialog(c);
        d.setCancelable(false);
        d.setMessage("Loading, please wait...");
        d.show();
        Log.d("DATA LOADER", "DATA LOADER");
    }

    @Override
    protected TimeXYZDataPackage doInBackground(Void...voids)
    {
        TimeXYZDataPackage dp = null;
        try
        {
            dp =  FileUtilities.readAccelerationData(fileName);
            dp.setType(loadType);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return dp;
    }

    @Override
    protected void onPostExecute(TimeXYZDataPackage dp)
    {
        if(d != null)
        {
            try
            {
                d.dismiss();
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
        }

        Intent i;

        if(dp.getType() == TimeXYZDataPackage.DataType.ACCELERATION)
            i = new Intent(c, DataViewerActivity.class);
        else
            i = new Intent(c, RawDataViewerActivity.class);

        i.putExtra("DATA", dp);

        c.startActivity(i);
    }
}