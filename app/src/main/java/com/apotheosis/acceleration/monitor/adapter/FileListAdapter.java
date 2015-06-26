package com.apotheosis.acceleration.monitor.adapter;

import android.content.Context;
import android.media.MediaScannerConnection;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.apotheosis.acceleration.util.FileUtilities;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileListAdapter extends BaseAdapter
{
    private List<String> fileNames;
    private Context c;

    public FileListAdapter(Context c)
    {
        this.c = c;
        refreshFiles();
    }

    public void refreshFiles()
    {
        refreshFiles(c);
        fileNames = FileUtilities.getFileList();
        notifyDataSetChanged();
    }

    @Override
    public int getCount()
    {
        return fileNames.size();
    }

    @Override
    public String getItem(int position)
    {
        return fileNames.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        if(convertView == null)
        {
            TextView tv = (TextView) LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_1, null);
            tv.setText(getItem(position));
            return tv;
        }
        return convertView;
    }
    private void refreshFiles(Context c)
    {
        File f = new File(FileUtilities.path);
        ArrayList<String> paths = new ArrayList<>();

        if(f.isDirectory())
        {
            String[] files = f.list();

            for(String file : files)
            {
                paths.add(FileUtilities.path + "/" + file);
            }
        }
        MediaScannerConnection.scanFile(c, paths.toArray(new String[paths.size()]), null, null);
    }

}
