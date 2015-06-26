package com.apotheosis.acceleration.monitor.viewer;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.apotheosis.acceleration.util.BadDataException;
import com.apotheosis.acceleration.util.FileUtilities;
import com.apotheosis.acceleration.util.TimeXYZDataPackage;
import com.apotheosis.acceleration.monitor.R;
import com.apotheosis.acceleration.monitor.math.LinearEqn;
import com.apotheosis.acceleration.monitor.math.QuadEqn;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Vector;

public class DataInfoPageActivity extends AppCompatActivity implements OnItemSelectedListener
{
	private TimeXYZDataPackage dp;
	private QuadEqn qEQX, qEQY, qEQZ;
	private LinearEqn lEQX, lEQY, lEQZ;
	private static LoadData loadData;

	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_data_info_page);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

		if(this.getLastNonConfigurationInstance() != null)
		{
			loadData = (LoadData) getLastNonConfigurationInstance();
		}
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onResume()
	{
		super.onResume();
		if(getIntent().getExtras() != null && getLastNonConfigurationInstance() == null)
		{
			try 
			{
				dp = FileUtilities.readAccelerationData(getIntent().getExtras().getString("FILENAME"));
			} 
			catch (IOException e) 
			{
				Toast.makeText(this, "Error reading file.", Toast.LENGTH_LONG).show();
				e.printStackTrace();
			} 
			catch (BadDataException e) 
			{
				Toast.makeText(this, "Invalid Data Format.", Toast.LENGTH_LONG).show();
				e.printStackTrace();
			}

			setTitle(dp.getTitle() + " - Info Page");
		}

		final Spinner regressionTypesX = (Spinner) findViewById(R.id.typeOfRegressionX),
				regressionTypesY = (Spinner) findViewById(R.id.typeOfRegressionY),
				regressionTypesZ = (Spinner) findViewById(R.id.typeOfRegressionZ);

		final String[] entriesRegressionTypes = {"Linear", "Quadratic"};
		ArrayAdapter<String> regressionTypesArrayAdapter = new ArrayAdapter<>(this,
				android.R.layout.simple_spinner_dropdown_item, entriesRegressionTypes);

		regressionTypesX.setAdapter(regressionTypesArrayAdapter);
		regressionTypesY.setAdapter(regressionTypesArrayAdapter);
		regressionTypesZ.setAdapter(regressionTypesArrayAdapter);

		Button xCalc = (Button) findViewById(R.id.calculateXButton),
				yCalc = (Button) findViewById(R.id.calculateYButton),
				zCalc = (Button) findViewById(R.id.calculateZButton);

		final EditText xInput = (EditText) findViewById(R.id.xInput),
				yInput = (EditText) findViewById(R.id.yInput),
				zInput = (EditText) findViewById(R.id.zInput);
		
		lEQX = new LinearEqn(dp.getTime(), dp.getX()); 
		qEQX = new QuadEqn(dp.getTime(), dp.getX()); 

		lEQY = new LinearEqn(dp.getTime(), dp.getY()); 
		qEQY = new QuadEqn(dp.getTime(), dp.getY()); 

		lEQZ = new LinearEqn(dp.getTime(), dp.getZ()); 
		qEQZ = new QuadEqn(dp.getTime(), dp.getZ()); 

		
		final TextView xOutput = (TextView) findViewById(R.id.xOutput);
		final TextView yOutput = (TextView) findViewById(R.id.yOutput);
		final TextView zOutput = (TextView) findViewById(R.id.zOutput);
		
		xCalc.setOnClickListener(new View.OnClickListener() 
		{
			public void onClick(View v) 
			{

				solve(xOutput, regressionTypesX, xInput, dp.getTime(), dp.getX()); 
			}
		});

		yCalc.setOnClickListener(new View.OnClickListener() 
		{
			public void onClick(View v) 
			{

				solve(yOutput, regressionTypesY, yInput, dp.getTime(), dp.getY());
			}
		});

		zCalc.setOnClickListener(new View.OnClickListener() 
		{
			public void onClick(View v) 
			{

				solve(zOutput, regressionTypesZ, zInput, dp.getTime(), dp.getZ());
			}
		});

		regressionTypesX.setOnItemSelectedListener(this);
		regressionTypesY.setOnItemSelectedListener(this);
		regressionTypesZ.setOnItemSelectedListener(this);		

		loadData = new LoadData(dp, this,

				new TextView[]{ (TextView) findViewById(R.id.xLinearEqn),
				(TextView) findViewById(R.id.xQuadEqn),
				(TextView) findViewById(R.id.yLinearEqn),
				(TextView) findViewById(R.id.yQuadEqn),
				(TextView) findViewById(R.id.zLinearEqn),
				(TextView) findViewById(R.id.zQuadEqn)},

				new LinearEqn[]{lEQX, lEQY, lEQZ},
				new QuadEqn[] {qEQX, qEQY, qEQZ},
				true);

		loadData.execute((Void[]) null);
	}

	private void solve(TextView tv, Spinner regressionType, EditText input, Vector<Double> time, Vector<Double> data)
	{
		DecimalFormat df = new DecimalFormat("0.000000");

		if(!input.getText().toString().equals(""))
		{
			switch(tv.getId())
			{
			case R.id.xOutput: 
				switch(regressionType.getSelectedItemPosition())
				{
				case 0:
					tv.setText(df.format(lEQX.valueAt(Double.parseDouble(input.getText().toString()))));
					break;
				case 1:
					tv.setText(df.format(qEQX.valueAt(Double.parseDouble(input.getText().toString()))));
					break;
				}
				break;
			case R.id.yOutput: 
				switch(regressionType.getSelectedItemPosition())
				{
				case 0:
					tv.setText(df.format(lEQY.valueAt(Double.parseDouble(input.getText().toString()))));
					break;
				case 1:
					tv.setText(df.format(qEQY.valueAt(Double.parseDouble(input.getText().toString()))));
					break;
				}
				break;
			case R.id.zOutput: 
				switch(regressionType.getSelectedItemPosition())
				{
				case 0:
					tv.setText(df.format(lEQZ.valueAt(Double.parseDouble(input.getText().toString()))));
					break;
				case 1:
					tv.setText(df.format(qEQZ.valueAt(Double.parseDouble(input.getText().toString()))));
					break;
				}
				break;
			}
		}
		else
		{
			Toast.makeText(this, "Invalid Input", Toast.LENGTH_LONG).show();
		}
	}

	public void onItemSelected(AdapterView<?> parent, View v, int position,
			long id) 
	{		
		final TextView xOutput = (TextView) findViewById(R.id.xOutput),
				yOutput = (TextView) findViewById(R.id.yOutput),
				zOutput = (TextView) findViewById(R.id.zOutput);
		
		switch(v.getId())
		{
		case R.id.typeOfRegressionX: xOutput.setText(""); break;
		case R.id.typeOfRegressionY: yOutput.setText(""); break;
		case R.id.typeOfRegressionZ: zOutput.setText(""); break;
		}
		
		
		loadData = new LoadData(dp, this,
				new TextView[]{ (TextView) findViewById(R.id.xLinearEqn),
				(TextView) findViewById(R.id.xQuadEqn),
				(TextView) findViewById(R.id.yLinearEqn),
				(TextView) findViewById(R.id.yQuadEqn),
				(TextView) findViewById(R.id.zLinearEqn),
				(TextView) findViewById(R.id.zQuadEqn),
				xOutput, yOutput, zOutput},

				new LinearEqn[]{lEQX, lEQY, lEQZ},
				new QuadEqn[] {qEQX, qEQY, qEQZ},
				false);

		loadData.execute((Void[]) null);
	}

	public void onNothingSelected(AdapterView<?> parent) 
	{

	}

	@Override
	public void onConfigurationChanged(Configuration newConfig)
	{
		super.onConfigurationChanged(newConfig);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
	}

	private class LoadData extends AsyncTask<Void, Void, Void>
	{
		private TextView xL, xQ, yL, yQ, zL, zQ;
		private LinearEqn lEQX, lEQY, lEQZ;
		private QuadEqn qEQX, qEQY, qEQZ;
		private TimeXYZDataPackage dp;
		private ProgressDialog dialog;
		private boolean showDialog;
		private Context c;

		public LoadData(TimeXYZDataPackage dp, Context c, TextView[] eqns,
				LinearEqn[] linears, QuadEqn[] quads, boolean show)
		{
			this.dp = dp;
			this.c = c;

			xL = eqns[0];
			xQ = eqns[1];
			yL = eqns[2];
			yQ = eqns[3];
			zL = eqns[4];
			zQ = eqns[5];

			lEQX = linears[0];
			lEQY = linears[1];
			lEQZ = linears[2];

			qEQX = quads[0];
			qEQY = quads[1];
			qEQZ = quads[2];
			
			showDialog = show;
		}

		@Override
		protected void onPreExecute()
		{
			if(showDialog)
			{
				dialog = new ProgressDialog(c);
				dialog.setMessage("Loading, please wait...");
				dialog.setCancelable(false);
				dialog.show();
			}
		}

		@Override
		protected Void doInBackground(Void... arg0) 
		{
			lEQX = new LinearEqn(dp.getTime(), dp.getX()); 
			qEQX = new QuadEqn(dp.getTime(), dp.getX()); 

			lEQY = new LinearEqn(dp.getTime(), dp.getY()); 
			qEQY = new QuadEqn(dp.getTime(), dp.getY()); 

			lEQZ = new LinearEqn(dp.getTime(), dp.getZ()); 
			qEQZ = new QuadEqn(dp.getTime(), dp.getZ()); 
			return null;
		}

		@Override
		protected void onPostExecute(Void v)
		{
			xL.setText("X Linear Regression: " + lEQX.toString());
			xQ.setText("X Quadratic Regression: " + qEQX.toString());
			yL.setText("Y Linear Regression: " + lEQY.toString());
			yQ.setText("Y Quadratic Regression: " + qEQY.toString());
			zL.setText("Z Linear Regression: " + lEQZ.toString());
			zQ.setText("Z Quadratic Regression: " + qEQZ.toString());
			
			if(showDialog && dialog != null && dialog.isShowing())
				dialog.dismiss();
		}
	}
}