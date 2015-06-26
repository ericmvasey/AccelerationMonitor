package com.apotheosis.acceleration.monitor.view;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.apotheosis.acceleration.monitor.R;

public class XStepDialog 
{	
	final AlertDialog dialog;
	final OnXStepChangedListener listener;
	private double stepValue;
	
	public interface OnXStepChangedListener 
	{
		void onCancel(XStepDialog dialog);
		void onOk(XStepDialog dialog, double stepVal, int selectedButton);
	}
	
	public XStepDialog(final Context c, double xStep2, OnXStepChangedListener l)
	{
		this.listener = l;
		View v = LayoutInflater.from(c).inflate(R.layout.preference_x_step_dialog, null);
		
		final EditText xStep = (EditText) v.findViewById(R.id.xStepValEditText);
		final RadioGroup rg = (RadioGroup) v.findViewById(R.id.xStepRdioGroup);
		final TextView description = (TextView) v.findViewById(R.id.xStepValDescription);
		final RadioButton auto = (RadioButton) rg.findViewById(R.id.autoStepOption),
				manual = (RadioButton) rg.findViewById(R.id.manualStepOption);
		
		if(xStep2 != -1.0)
		{
			xStep.setText(String.valueOf(xStep2));
			auto.setChecked(false);
			manual.setChecked(true);
		}
		else
		{
			xStep.setText("");
			auto.setChecked(true);
			manual.setChecked(false);
		}
		
		switch(rg.getCheckedRadioButtonId())
		{
		case R.id.manualStepOption: xStep.setEnabled(true); description.setEnabled(true); break;
		default: xStep.setEnabled(false); description.setEnabled(false); break;
		}
		
		rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() 
		{
			public void onCheckedChanged(RadioGroup group, int checkedId) 
			{
				switch(checkedId)
				{
				case R.id.manualStepOption: xStep.setEnabled(true); description.setEnabled(true); break;
				default: xStep.setEnabled(false); description.setEnabled(false); break;
				}
			}
		});
		
		dialog = new AlertDialog.Builder(c)
		.setPositiveButton("Set", new DialogInterface.OnClickListener() 
		{
			public void onClick(DialogInterface dialog, int which) 
			{
				if(XStepDialog.this.listener != null)
				{
					if(!xStep.getText().toString().equalsIgnoreCase(""))
					{
						stepValue = Double.valueOf(xStep.getText().toString());
						XStepDialog.this.listener.onOk(XStepDialog.this, stepValue, rg.getCheckedRadioButtonId());
					}
					else
						XStepDialog.this.listener.onOk(XStepDialog.this, -1, rg.getCheckedRadioButtonId());
				}
				
			}
		})
		.setNegativeButton("Cancel", new DialogInterface.OnClickListener() 
		{
			public void onClick(DialogInterface dialog, int which) 
			{
				if(XStepDialog.this.listener != null)
					XStepDialog.this.listener.onCancel(XStepDialog.this);
			}
		})
		.setOnCancelListener(new DialogInterface.OnCancelListener()
		{
			public void onCancel(DialogInterface dialog) 
			{
				if(XStepDialog.this.listener != null)
					XStepDialog.this.listener.onCancel(XStepDialog.this);
			}
			
		})
		.create();
		
		dialog.setView(v);
	}
	
	public void show()
	{
		dialog.show();
	}
	
	public AlertDialog getDialog()
	{
		return dialog;
	}
}
