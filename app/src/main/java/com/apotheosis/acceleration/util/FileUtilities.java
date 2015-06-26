package com.apotheosis.acceleration.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import android.os.Environment;
import android.util.Log;

public class FileUtilities
{
	public static String fileName;
	public static boolean dataCollectionRunning = false;
	public static final String path = Environment.getExternalStorageDirectory().toString() + "/accelerometer_data/";

	private BufferedWriter bw;

	public FileUtilities(File f) throws IOException
	{		
		FileWriter fw = new FileWriter(f, true);
		bw = new BufferedWriter(fw);
		Log.d("FILE", Environment.getExternalStorageDirectory() + "/accelerometer_data/" + fileName + ".csv");
	}

	public void appendToFileBuffered(String data) throws IOException
	{
		bw.write(data);
		bw.newLine();
		bw.flush();
	}
	
	public void purge() throws IOException
	{
		bw.flush();
		bw.close();
	}
	
	public static void setFileName(String fileName)
	{
		FileUtilities.fileName = fileName;
	}

	public static String getFileName()
	{
		return fileName;
	}

	public static List<String> getFileList()
	{
		File dir = new File(FileUtilities.path);
		String[] nameList = dir.list();
		if(nameList != null)
		{
			List<String> fileNames = new ArrayList<>();

			for(String fileName: nameList)
			{
				if(fileName.endsWith(".csv") &&
						!fileName.contains("(Velocity Graph)") &&
						!fileName.contains("(Position Graph)"));
				{
					String edit = fileName;
					edit = edit.replace(".csv", "");
					fileNames.add(edit);
				}
			}
			return fileNames;
		}
		else
		{
			return new ArrayList<>();
		}
	}

	public static boolean fileExists(String fileName)
	{
		File f = new File(FileUtilities.path + fileName + ".csv");
		return f.exists();
	}



	public static TimeXYZDataPackage readAccelerationData(String fileName) throws IOException, BadDataException
	{
		BufferedReader fileReader = new BufferedReader(new InputStreamReader(
				new DataInputStream(new FileInputStream(FileUtilities.path + 
						fileName + ".csv"))));
		CSVReader csvReader = new CSVReader(fileReader);

		Vector<Double> 
		time = new Vector<>(),
		x = new Vector<>(),
		y = new Vector<>(),
		z = new Vector<>();
		String[] line;
		
		csvReader.readNext();
		line = csvReader.readNext();
		
		while (line != null)
		{
			try
			{
				time.add(Double.parseDouble(line[0]));
				x.add(Double.parseDouble(line[1]));
				y.add(Double.parseDouble(line[2]));
				z.add(Double.parseDouble(line[3]));
			}
			catch(NumberFormatException e)
			{
				fileReader.close();
				csvReader.close();
				throw new BadDataException();
			}
			line = csvReader.readNext();
		}
		
		fileReader.close();
		csvReader.close();

		TimeXYZDataPackage dp = new TimeXYZDataPackage(time, x, y, z);
		dp.setType(TimeXYZDataPackage.DataType.ACCELERATION);
		dp.setTitle(fileName);
		return dp;
	}
}