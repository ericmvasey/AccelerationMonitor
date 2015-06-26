package com.apotheosis.acceleration.monitor.math;
import org.apache.commons.math3.stat.regression.SimpleRegression;

import java.text.DecimalFormat;
import java.util.Vector;

public class LinearEqn 
{
	private double[][] data;
	private SimpleRegression sr;
	
	public LinearEqn()
	{
		
	}
	
	public LinearEqn(Vector<Double> time, Vector<Double> dataSet)
	{
		data = new double[dataSet.size()][2];
		
		for(int i = 0; i < dataSet.size(); i++)
		{
			data[i][0] = time.get(i);
			data[i][1] = dataSet.get(i);
		}
		
		sr = new SimpleRegression();
		sr.addData(data);
	}
	
	public LinearEqn(double[][] data)
	{
		sr = new SimpleRegression();
		sr.addData(data);
	}
	
	public double valueAt(double in)
	{
		return this.getSlope() * in + this.getIntercept();
	}
	
	public void clear()
	{
		data = null;
		sr = null;
	}
	
	@Override
	public String toString()
	{
		DecimalFormat df = new DecimalFormat("#.####");
		return "y = (" + df.format(sr.getSlope()) + ")x + (" + df.format(sr.getIntercept()) +")";
	}
	
	public double getSlope()
	{
		return sr.getSlope();
	}
	
	public double getIntercept()
	{
		return sr.getIntercept();
	}
	
	public void setData(Vector<Double> time, Vector<Double> dataSet)
	{
		data = new double[dataSet.size()][2];
		
		for(int i = 0; i < dataSet.size(); i++)
		{
			data[i][0] = time.get(i);
			data[i][1] = dataSet.get(i);
		}
		
		sr = new SimpleRegression();
		sr.addData(data);
	}
}
