package com.apotheosis.acceleration.monitor.math;
import java.text.DecimalFormat;
import java.util.Vector;

public class QuadEqn
{
	/* instance variables */
	Vector<Double[]> pointArray;
	private int numOfEntries; 
	private Double[] pointPair;

	/*constructor */
	public QuadEqn()
	{
		numOfEntries = 0;
		pointPair = new Double[2];
	}
	
	public QuadEqn(Vector<Double> time, Vector<Double> dataSet)
	{
		pointArray = new Vector<>(time.size()+1);
		numOfEntries = 0;
		for(int i = 0; i < dataSet.size(); i++)
		{
			numOfEntries++;
			pointPair = new Double[2];
			pointPair[0] = time.get(i);
			pointPair[1] = dataSet.get(i);
			pointArray.add(pointPair);
		}
	}

	public double valueAt(double in)
	{
		return this.aTerm() * Math.pow(in, 2) + this.bTerm() * in + this.cTerm();
	}
	
	public void addPoints(double x, double y) 
	{
		pointPair = new Double[2]; 
		numOfEntries +=1; 
		pointPair[0] = x; 
		pointPair[1] = y;
		pointArray.add(pointPair);
	}
	
	public void clear()
	{
		numOfEntries = 0;
		pointArray = new Vector<>();
		pointPair = null;
	}
	
	public void addSets(Vector<Double> time, Vector<Double> dataSet)
	{
		for(int i = 0; i < time.size(); i++)
		{
			numOfEntries++;
			pointPair = new Double[2];
			pointPair[0] = time.get(i);
			pointPair[1] = dataSet.get(i);
			pointArray.add(pointPair);
		}
	}

	public double aTerm()
	{
		//notation sjk to mean the sum of x_i^j*y_i^k. 
		double s40 = getSx4(); //sum of x^4
		double s30 = getSx3(); //sum of x^3
		double s20 = getSx2(); //sum of x^2
		double s10 = getSx();  //sum of x
		double s00 = numOfEntries;
		//sum of x^0 * y^0  ie 1 * number of entries

		double s21 = getSx2y(); //sum of x^2*y
		double s11 = getSxy();  //sum of x*y
		double s01 = getSy();   //sum of y

		//a = Da/D
		return (s21*(s20 * s00 - s10 * s10) - 
				s11*(s30 * s00 - s10 * s20) + 
				s01*(s30 * s10 - s20 * s20))
				/
				(s40*(s20 * s00 - s10 * s10) -
						s30*(s30 * s00 - s10 * s20) + 
						s20*(s30 * s10 - s20 * s20));
	}

	/// <summary>
	/// returns the b term of the equation ax^2 + bx + c
	/// </summary>
	/// <returns>b term</returns>
	public double bTerm()
	{
		//notation sjk to mean the sum of x_i^j*y_i^k.
		double s40 = getSx4(); //sum of x^4
		double s30 = getSx3(); //sum of x^3
		double s20 = getSx2(); //sum of x^2
		double s10 = getSx();  //sum of x
		double s00 = numOfEntries;
		//sum of x^0 * y^0  ie 1 * number of entries

		double s21 = getSx2y(); //sum of x^2*y
		double s11 = getSxy();  //sum of x*y
		double s01 = getSy();   //sum of y

		//b = Db/D
		return (s40*(s11 * s00 - s01 * s10) - 
				s30*(s21 * s00 - s01 * s20) + 
				s20*(s21 * s10 - s11 * s20))
				/
				(s40 * (s20 * s00 - s10 * s10) - 
						s30 * (s30 * s00 - s10 * s20) + 
						s20 * (s30 * s10 - s20 * s20));
	}

	/// <summary>
	/// returns the c term of the equation ax^2 + bx + c
	/// </summary>
	/// <returns>c term</returns>
	public double cTerm()
	{
		//notation sjk to mean the sum of x_i^j*y_i^k.
		double s40 = getSx4(); //sum of x^4
		double s30 = getSx3(); //sum of x^3
		double s20 = getSx2(); //sum of x^2
		double s10 = getSx();  //sum of x
		double s00 = numOfEntries;
		//sum of x^0 * y^0  ie 1 * number of entries

		double s21 = getSx2y(); //sum of x^2*y
		double s11 = getSxy();  //sum of x*y
		double s01 = getSy();   //sum of y

		//c = Dc/D
		return (s40*(s20 * s01 - s10 * s11) - 
				s30*(s30 * s01 - s10 * s21) + 
				s20*(s30 * s11 - s20 * s21))
				/
				(s40 * (s20 * s00 - s10 * s10) - 
						s30 * (s30 * s00 - s10 * s20) + 
						s20 * (s30 * s10 - s20 * s20));
	}

	public double rSquare() // get r-squared
	{
		// 1 - (residual sum of squares / total sum of squares)
		return 1 - getSSerr() / getSStot();
	}


	/*helper methods*/
	private double getSx() // get sum of x
	{
		double Sx = 0;
		for(Double[] ppair : pointArray)
		{
			Sx += ppair[0];
		}
		return Sx;
	}

	private double getSy() // get sum of y
	{
		double Sy = 0;
		for (Double[] ppair : pointArray)
		{
			Sy += ppair[1];
		}
		return Sy;
	}

	private double getSx2() // get sum of x^2
	{
		double Sx2 = 0;
		for (Double[] ppair : pointArray)
		{
			Sx2 += Math.pow(ppair[0], 2); // sum of x^2
		}
		return Sx2;
	}

	private double getSx3() // get sum of x^3
	{
		double Sx3 = 0;
		for (Double[] ppair : pointArray)
		{
			Sx3 += Math.pow(ppair[0], 3); // sum of x^3
		}
		return Sx3;
	}

	private double getSx4() // get sum of x^4
	{
		double Sx4 = 0;
		for (Double[] ppair : pointArray)
		{
			Sx4 += Math.pow(ppair[0], 4); // sum of x^4
		}
		return Sx4;
	}

	private double getSxy() // get sum of x*y
	{
		double Sxy = 0;
		for (Double[] ppair : pointArray)
		{
			Sxy += ppair[0] * ppair[1]; // sum of x*y
		}
		return Sxy;
	}

	private double getSx2y() // get sum of x^2*y
	{
		double Sx2y = 0;
		for (Double[] ppair : pointArray)
		{
			Sx2y += Math.pow(ppair[0], 2) * ppair[1]; // sum of x^2*y
		}
		return Sx2y;
	}

	private double getYMean() // mean value of y
	{
		double y_tot = 0;
		for (Double[] ppair : pointArray)
		{
			y_tot += ppair[1]; 
		}
		return y_tot/numOfEntries;
	}

	private double getSStot() // total sum of squares
	{
		//the sum of the squares of the differences between 
		//the measured y values and the mean y value
		double ss_tot = 0;
		for (Double[] ppair : pointArray)
		{
			ss_tot += Math.pow(ppair[1] - getYMean(), 2);
		}
		return ss_tot;
	}

	private double getSSerr() // residual sum of squares
	{
		//the sum of the squares of the difference between 
		//the measured y values and the values of y predicted by the equation
		double ss_err = 0;
		for (Double[] ppair : pointArray)
		{
			ss_err += Math.pow(ppair[1] - getPredictedY(ppair[0]), 2);
		}
		return ss_err;
	}

	private double getPredictedY(double x)
	{
		//returns value of y predicted by the equation for a given value of x
		return aTerm() * Math.pow(x, 2) + bTerm() * x + cTerm();
	}
	
	@Override
	public String toString()
	{
		DecimalFormat df = new DecimalFormat("#.####");
		if(!pointArray.isEmpty())
			return "y = (" + df.format(aTerm()) + ")x^2+ (" + df.format(bTerm()) + 
					")x + " + "(" + df.format(cTerm()) + ")";
		else
			return null;
	}
}
