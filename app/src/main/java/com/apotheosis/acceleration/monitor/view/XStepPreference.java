package com.apotheosis.acceleration.monitor.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Parcel;
import android.os.Parcelable;
import android.preference.Preference;
import android.util.AttributeSet;
import android.view.View;

import com.apotheosis.acceleration.monitor.R;

public class XStepPreference extends Preference 
{
	private double xStep;
	
	public XStepPreference(Context context, AttributeSet attrs) 
	{
		super(context, attrs);
		setWidgetLayoutResource(R.layout.preference_x_step);
	}

	@Override
	protected void onBindView(View view) 
	{
		super.onBindView(view);
		// view is your layout expanded and added to the dialog
		// find and hang on to your views here, add click listeners etc
		// basically things you would do in onCreate

	}
	
	@Override
	protected void onClick()
	{
		new XStepDialog(getContext(), xStep, new XStepDialog.OnXStepChangedListener() 
		{
			@Override
			public void onOk(XStepDialog dialog, double stepVal, int selectedButton) 
			{
				if(!callChangeListener(stepVal))
					return;
				
				switch(selectedButton)
				{
				case R.id.autoStepOption: xStep = -1; break;
				case R.id.manualStepOption: xStep = stepVal; break;
				}
				
				if(xStep <= 0)
					xStep = -1.0;
				
				persistString(String.valueOf(xStep));
				notifyChanged();
			}
			
			@Override
			public void onCancel(XStepDialog dialog) 
			{
				//nothing to do here
			}
		}).show();
	}
	
	@Override 
	protected Object onGetDefaultValue(TypedArray a, int index) 
	{
		// This preference type's value type is Integer, so we read the default value from the attributes as an Integer.
		return a.getString(index);
	}

	@Override 
	protected void onSetInitialValue(boolean restoreValue, Object defaultValue) 
	{
		if (restoreValue) 
		{ // Restore state
			xStep = Double.valueOf(getPersistedString("-1"));
		} 
		else 
		{ // Set state
			String value = (String) defaultValue;
			this.xStep = Double.valueOf(value);
			persistString(value);
		}
	}

	/*
	 * Suppose a client uses this preference type without persisting. We
	 * must save the instance state so it is able to, for example, survive
	 * orientation changes.
	 */
	@Override 
	protected Parcelable onSaveInstanceState() 
	{
		final Parcelable superState = super.onSaveInstanceState();
		if (isPersistent()) return superState; // No need to save instance state since it's persistent

		final SavedState myState = new SavedState(superState);
		myState.xStep = xStep;
		return myState;
	}

	@Override 
	protected void onRestoreInstanceState(Parcelable state) 
	{
		if (!state.getClass().equals(SavedState.class)) {
			// Didn't save state for us in onSaveInstanceState
			super.onRestoreInstanceState(state);
			return;
		}

		// Restore the instance state
		SavedState myState = (SavedState) state;
		super.onRestoreInstanceState(myState.getSuperState());
		this.xStep = myState.xStep;
		notifyChanged();
	}

	/**
	 * SavedState, a subclass of {@link BaseSavedState}, will store the state
	 * of MyPreference, a subclass of Preference.
	 * <p>
	 * It is important to always call through to super methods.
	 */
	private static class SavedState extends BaseSavedState 
	{
		double xStep;

		public SavedState(Parcel source) 
		{
			super(source);
			xStep = Double.parseDouble(source.readString());
		}

		@Override public void writeToParcel(Parcel dest, int flags) 
		{
			super.writeToParcel(dest, flags);
			dest.writeString(String.valueOf(xStep));
		}

		public SavedState(Parcelable superState) 
		{
			super(superState);
		}

		@SuppressWarnings("unused") 
		public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator<SavedState>() {
			public SavedState createFromParcel(Parcel in) 
			{
				return new SavedState(in);
			}

			public SavedState[] newArray(int size) 
			{
				return new SavedState[size];
			}
		};
	}
	
}
