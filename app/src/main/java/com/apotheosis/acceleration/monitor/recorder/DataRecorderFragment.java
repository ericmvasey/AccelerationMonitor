package com.apotheosis.acceleration.monitor.recorder;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.apotheosis.acceleration.monitor.R;

public class DataRecorderFragment extends Fragment
{
    public interface OnCollectionToggle
    {
        void onToggle();
    }

    private OnCollectionToggle onCollectionToggleListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_data_recorder,
                container, false);

        Button pauseAccel = view.findViewById(R.id.pauseSensor);
        pauseAccel.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                onCollectionToggleListener.onToggle();
            }
        });

        return view;
    }

    public void setOnCollectionToggleListener(OnCollectionToggle toggle)
    {
        this.onCollectionToggleListener = toggle;
    }
}