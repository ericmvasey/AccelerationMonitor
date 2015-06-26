package com.apotheosis.acceleration.monitor.recorder;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.view.View;
import android.widget.Toast;

import com.androidplot.Plot;
import com.androidplot.ui.SizeMetric;
import com.androidplot.ui.SizeMetrics;
import com.androidplot.xy.BoundaryMode;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYPlot;
import com.apotheosis.acceleration.util.FileUtilities;
import com.apotheosis.acceleration.monitor.R;

import java.io.File;
import java.io.IOException;

public class DataCollector implements SensorEventListener
{
    private float[] Rarray = new float[16], I = new float[16];
    private float[] gravityVals = new float[3];
    private float[] magVals;
    //private float[] gyroVals;
    private float[] linear_acceleration = new float[3];
    private Context c;
    private SensorManager manager;
    private Sensor magFieldSensor, accSensor;
    private double nanoTimeStart = 0, currentTimeNano = 0, currentTimeSec = 0, currentTimeOffset = 0;
    private int dataCollectionRate;
    private boolean run;
    private FileUtilities fu;
    private final int HISTORY_SIZE = 20;
    private boolean pause = false;

    private SimpleXYSeries xAccel, yAccel, zAccel;
    private XYPlot plot;

    public DataCollector(Activity a)
    {
        this.c = a;
        run = false;

        manager = (SensorManager) a.getSystemService(Context.SENSOR_SERVICE);
        accSensor = manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magFieldSensor = manager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        int collectionRate = DataRecorderSettingsActivity.getDataCollectionRate(a);

        switch(collectionRate)
        {
            case 0: dataCollectionRate = SensorManager.SENSOR_DELAY_UI; break;
            case 1: dataCollectionRate = SensorManager.SENSOR_DELAY_NORMAL; break;
            case 2: dataCollectionRate = SensorManager.SENSOR_DELAY_GAME; break;
        }

        plot = (XYPlot) a.findViewById(R.id.accelerationGraphView);
    }

    private void initPlot()
    {
        plot.setBorderStyle(Plot.BorderStyle.SQUARE, null, null);
        plot.setVisibility(View.INVISIBLE);

        xAccel = new SimpleXYSeries("X Acceleration");
        yAccel = new SimpleXYSeries("Y Acceleration");
        zAccel = new SimpleXYSeries("Z Acceleration");

        plot.addSeries(xAccel, new LineAndPointFormatter(Color.GREEN, Color.TRANSPARENT, Color.TRANSPARENT, null) );
        plot.addSeries(yAccel, new LineAndPointFormatter(Color.RED, Color.TRANSPARENT, Color.TRANSPARENT, null));
        plot.addSeries(zAccel, new LineAndPointFormatter(Color.BLUE, Color.TRANSPARENT, Color.TRANSPARENT, null));
        plot.setDomainLabel("Time (sec)");
        plot.setRangeLabel("Acceleration (m/s^2)");
        plot.getTitleWidget().setVisible(false);
        plot.getGraphWidget().getGridBackgroundPaint().setColor(Color.BLACK);
        plot.getGraphWidget().getBackgroundPaint().setColor(Color.BLACK);

        plot.setPlotPadding(0, 0, 0, 0);
        plot.setPlotMargins(0, 0, 0, 0);
    }

    public synchronized DataCollector setFileName(String fileName)
    {
        try
        {
            fu = new FileUtilities(new File(FileUtilities.path,  fileName + ".csv"));
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

        return this;
    }

    public synchronized boolean isRunning()
    {
        return run;
    }

    public synchronized void start()
    {
        run = true;
        initPlot();
        manager.registerListener(this, accSensor, dataCollectionRate);
        manager.registerListener(this, magFieldSensor, dataCollectionRate);

        plot.setTitle(FileUtilities.fileName);
        plot.setVisibility(View.VISIBLE);
    }

    public synchronized void pause()
    {
        pause = true;
    }

    public synchronized void resume()
    {
        pause = false;
    }

    public synchronized void stop()
    {
        run = false;

        if(!plot.isEmpty())
            plot.clear();
        manager.unregisterListener(this, accSensor);
        manager.unregisterListener(this, magFieldSensor);

        try
        {
            fu.purge();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        plot.setVisibility(View.INVISIBLE);
    }

    @Override
    public synchronized void onSensorChanged(SensorEvent event)
    {
        if(!pause)
        {
            if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
            {
                if(magVals != null)
                {
                    double updateFreq = 30; // match this to your update speed
                    double cutOffFreq = 0.9f;
                    double RC = 1.0f / cutOffFreq;
                    double dt = 1.0f / updateFreq;
                    double alpha = RC / (dt + RC);

                    // Isolate the force of gravity with the low-pass filter.
                    gravityVals[0] = (float) (alpha * gravityVals[0] + (1 - alpha) * event.values[0]);
                    gravityVals[1] = (float) (alpha * gravityVals[1] + (1 - alpha) * event.values[1]);
                    gravityVals[2] = (float) (alpha * gravityVals[2] + (1 - alpha) * event.values[2]);

                    // Remove the gravity contribution with the high-pass filter.
                    linear_acceleration[0] = event.values[0] - gravityVals[0];
                    linear_acceleration[1] = event.values[1] - gravityVals[1];
                    linear_acceleration[2] = event.values[2] - gravityVals[2];

                    onAccelerometerChanged(linear_acceleration[0], linear_acceleration[1], linear_acceleration[2], event.timestamp);
                }
            }
            else if(event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
            {
                magVals = event.values.clone();
            }
        }
        else
        {
            currentTimeOffset += event.timestamp - nanoTimeStart - currentTimeNano;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy)
    {
        if(accuracy == SensorManager.SENSOR_STATUS_UNRELIABLE || accuracy == SensorManager.SENSOR_STATUS_ACCURACY_LOW)
        {

        }
    }

    private static final boolean ADAPTIVE_ACCEL_FILTER = true;
    double lastAccel[] = new double[3];
    float accelFilter[] = new float[3];

    public synchronized void onAccelerometerChanged(float accelX, float accelY, float accelZ, double timestamp)
    {
        // high pass filter
        double updateFreq = 30; // match this to your update speed
        double cutOffFreq = 0.9f;
        double RC = 1.0f / cutOffFreq;
        double dt = 1.0f / updateFreq;
        double filterConstant = RC / (dt + RC);
        double alpha = filterConstant;
        double kAccelerometerMinStep = 0.033f;
        double kAccelerometerNoiseAttenuation = 3.0f;

        lastAccel[0] = accelX;
        lastAccel[1] = accelY;
        lastAccel[2] = accelZ;


        SensorManager.getRotationMatrix(Rarray, I, gravityVals, magVals);
        float[] inverted = new float[16];
        android.opengl.Matrix.invertM(inverted, 0, Rarray, 0);
        float[] accelVals = new float[4];
        float[] eventVals = {accelX, accelY, accelZ, 0};
        android.opengl.Matrix.multiplyMV(accelVals, 0, inverted, 0, eventVals, 0);

        if(ADAPTIVE_ACCEL_FILTER)
        {
            double d = clamp(Math.abs(norm(accelFilter[0], accelFilter[1], accelFilter[2]) - norm(accelVals[0], accelVals[1], accelVals[2])) / kAccelerometerMinStep - 1.0f, 0.0f, 1.0f);
            alpha = d * filterConstant / kAccelerometerNoiseAttenuation + (1.0f - d) * filterConstant;
        }

        accelFilter[0] = (float) (alpha * (accelFilter[0] + accelVals[0] - lastAccel[0]));
        accelFilter[1] = (float) (alpha * (accelFilter[1] + accelVals[1] - lastAccel[1]));
        accelFilter[2] = (float) (alpha * (accelFilter[2] + accelVals[2] - lastAccel[2]));

        onFilteredAccelerometerChanged(accelVals[0], accelVals[1], accelVals[2], timestamp);
        //onFilteredAccelerometerChanged(accelX, accelY, accelZ, timestamp);
    }
    private double clamp(double v, double min, double max)
    {
        if(v > max)
            return max;

        else if(v < min)
            return min;

        else
            return v;
    }

    private double norm(double accelX, double accelY, double accelZ)
    {
        return Math.sqrt(accelX * accelX + accelY * accelY + accelZ + accelZ);
    }

    private synchronized void onFilteredAccelerometerChanged(double x, double y, double z, double timestamp)
    {
        if(nanoTimeStart == 0)
        {
            nanoTimeStart = timestamp;
            try
            {
                fu.appendToFileBuffered("TIME,X,Y,Z");
            }
            catch (IOException e)
            {
                Toast.makeText(c, "Error writing to file.", Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        }

        currentTimeNano = timestamp - nanoTimeStart - currentTimeOffset;
        currentTimeSec = truncate((currentTimeNano * Math.pow(10, -9)), 4);

        try
        {
            fu.appendToFileBuffered(
                    String.valueOf(currentTimeSec) + "," +
                            String.valueOf(x) + "," +
                            String.valueOf(y)+ "," +
                            String.valueOf(z));
        }
        catch (IOException e)
        {
            Toast.makeText(c, "Error writing to file.", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }

        if(plot.getVisibility() == View.VISIBLE)
        {
            if (xAccel.size() > HISTORY_SIZE)
            {
                xAccel.removeFirst();
                yAccel.removeFirst();
                zAccel.removeFirst();
            }

            xAccel.addLast(currentTimeSec, x);
            yAccel.addLast(currentTimeSec, y);
            zAccel.addLast(currentTimeSec, z);

            plot.redraw();
        }
    }

    private static double truncate(double value, int places)
    {
        double multiplier = Math.pow(10, places);
        return Math.floor(multiplier * value) / multiplier;
    }
}
