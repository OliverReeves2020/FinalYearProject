package com.example.finalyearproject.functions;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.content.res.Resources;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Arrays;

public class AchievementEdit {

    private Context context;

    public AchievementEdit(Context context){
        this.context = context;
    }

    public void copyCsvFromRawToInternalStorage(Context context, int rawCsvId, String internalFilename) throws IOException {
        InputStream rawStream = context.getResources().openRawResource(rawCsvId);
        InputStreamReader rawReader = new InputStreamReader(rawStream);
        BufferedReader rawBufferedReader = new BufferedReader(rawReader);

        File internalFile = new File(context.getFilesDir(), internalFilename);
        FileOutputStream internalStreamOut = new FileOutputStream(internalFile);
        OutputStreamWriter internalWriter = new OutputStreamWriter(internalStreamOut);
        BufferedWriter internalBufferedWriter = new BufferedWriter(internalWriter);

        String line;
        int count=0;
        while ((line = rawBufferedReader.readLine()) != null) {
            internalBufferedWriter.write(line);
            internalBufferedWriter.newLine();
            count+=1;
        }

        rawBufferedReader.close();
        internalBufferedWriter.close();
        context.getSharedPreferences("UserStats",Context.MODE_PRIVATE).edit().putInt("MaxAchieves",count).commit();


        System.out.println("CSV file has been copied from raw to internal storage.");
    }

    public void transferCsvRow(Context context, String sourceFilename, String targetFilename, String searchValue) throws IOException {
        File sourceFile = new File(context.getFilesDir(), sourceFilename);
        File targetFile = new File(context.getFilesDir(), targetFilename);

        if (!targetFile.exists()) {
            targetFile.createNewFile();
        }

        FileInputStream sourceStream = new FileInputStream(sourceFile);
        FileInputStream targetStream = new FileInputStream(targetFile);

        BufferedReader sourceReader = new BufferedReader(new InputStreamReader(sourceStream));
        BufferedReader targetReader = new BufferedReader(new InputStreamReader(targetStream));
        StringBuilder sourceSb = new StringBuilder();
        StringBuilder targetSb = new StringBuilder();
        boolean found = false;


        SharedPreferences stats = context.getSharedPreferences("UserStats", Context.MODE_PRIVATE);
        int totalDays=stats.getInt("totalDays",0);
        int currentStreak=stats.getInt("currentStreak",0);
        int dailyAmount=stats.getInt("dailyAmount",0);

        // rows=
        //display name, tint, description, daily, current streak, total days

        String line;
        while ((line = sourceReader.readLine()) != null) {
            String[] values = line.split(",");

            System.out.println(values.length);
            System.out.println(values[0]);
            if(values.length<6){
                sourceSb.append(line).append("\n");
            }
            else if (Integer.parseInt(values[3])<= dailyAmount &&
                    Integer.parseInt(values[4])<= currentStreak &&
                    Integer.parseInt(values[5])<= totalDays) {

                System.out.println("setting"+values[0]);
                targetSb.append(line).append("\n");
                found = true;
            }

            else {
                sourceSb.append(line).append("\n");
            }
        }

        sourceReader.close();
        targetReader.close();

        if (!found) {
            System.out.println("No rows were found with the search value.");
            return;
        }

        FileOutputStream sourceStreamOut = new FileOutputStream(sourceFile);
        OutputStreamWriter sourceWriter = new OutputStreamWriter(sourceStreamOut);
        sourceWriter.write(sourceSb.toString());
        sourceWriter.close();

        FileOutputStream targetStreamOut = new FileOutputStream(targetFile, true);
        OutputStreamWriter targetWriter = new OutputStreamWriter(targetStreamOut);
        targetWriter.write(targetSb.toString());
        targetWriter.close();

        System.out.println("CSV row has been transferred.");
    }


}
