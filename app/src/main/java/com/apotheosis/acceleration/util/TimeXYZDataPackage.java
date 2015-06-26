package com.apotheosis.acceleration.util;

import java.util.List;
import java.util.Vector;

import android.os.Parcel;
import android.os.Parcelable;

public class TimeXYZDataPackage implements Parcelable
{
	private Vector<Double> time, x, y, z;
	private DataType type;
	private String title;
	public enum DataType {ACCELERATION, RAW_DATA};
	
	public TimeXYZDataPackage()
	{
		time = new Vector<>();
		x = new Vector<>();
		y = new Vector<>();
		z = new Vector<>();
	}
	
	public TimeXYZDataPackage(List<Double> x, List<Double> y, List<Double> z)
	{
		this.x = new Vector<>(x.size()+1);
		this.y = new Vector<>(y.size()+1);
		this.z = new Vector<>(z.size()+1);
		
		this.x.addAll(x); this.y.addAll(y); this.z.addAll(z);
	}
	
	public TimeXYZDataPackage(List<Double> time, List<Double> x, List<Double> y, List<Double> z)
	{
		this.time = new Vector<>(time.size()+1);
		this.x = new Vector<>(x.size()+1);
		this.y = new Vector<>(y.size()+1);
		this.z = new Vector<>(z.size()+1);
		
		this.time.addAll(time); this.x.addAll(x); this.y.addAll(y); this.z.addAll(z);
	}
	
	private TimeXYZDataPackage(Parcel in)
	{
		this.time = new Vector<>();
		this.x = new Vector<>();
		this.y = new Vector<>();
		this.z = new Vector<>();
		
		in.readList(time, null);
		in.readList(x, null);
		in.readList(y, null);
		in.readList(z, null);
		this.type = (DataType) in.readSerializable();
		this.title = in.readString();
	}
	
    public static final Parcelable.Creator<TimeXYZDataPackage> CREATOR = 
    		new Parcelable.Creator<TimeXYZDataPackage>() 
    {
        public TimeXYZDataPackage createFromParcel(Parcel in) 
        {
            return new TimeXYZDataPackage(in);
        }

        public TimeXYZDataPackage[] newArray(int size) 
        {
            return new TimeXYZDataPackage[size];
        }
    };
	
	public void add(double time, double x, double y, double z)
	{
		this.time.add(time);
		this.x.add(x);
		this.y.add(y);
		this.z.add(z);
	}
	public Vector<Double> getX() 
	{
		return x;
	}

	public Vector<Double> getY() 
	{
		return y;
	}

	public Vector<Double> getZ() 
	{
		return z;
	}

	public Vector<Double> getTime()
	{
		return time;
	}
	
	public Vector<Double> getTimeXPaired()
	{
		Vector<Double> x = new Vector<>(this.x.size()*2+1);
		
		for(int i = 0; i < this.x.size(); i++)
		{
			x.add(this.time.get(i));
			x.add(this.x.get(i));
		}
		
		return x;
	}
	
	public Vector<Double> getTimeYPaired()
	{
		Vector<Double> y = new Vector<>(this.y.size()*2+1);
		
		for(int i = 0; i < this.y.size(); i++)
		{
			y.add(this.time.get(i));
			y.add(this.y.get(i));
		}
		
		return y;
	}
	
	public Vector<Double> getTimeZPaired()
	{
		Vector<Double> z = new Vector<>(this.z.size()*2+1);
		
		for(int i = 0; i < this.z.size(); i++)
		{
			z.add(this.time.get(i));
			z.add(this.z.get(i));
		}
		
		return z;
	}
	
	public void setType(DataType type)
	{
		this.type = type;
	}
	
	public DataType getType()
	{
		return this.type;
	}

	public String getTitle() 
	{
		return title;
	}

	public void setTitle(String title) 
	{
		this.title = title;
	}

	public int describeContents() 
	{
		return 0;
	}

	public void writeToParcel(Parcel dest, int flags) 
	{
		dest.writeList(time);
		dest.writeList(x);
		dest.writeList(y);
		dest.writeList(z);
		dest.writeSerializable(type);
		dest.writeString(title);
	}
}