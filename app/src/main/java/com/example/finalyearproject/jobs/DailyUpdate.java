package com.example.finalyearproject.jobs;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;

import com.example.finalyearproject.R;
import com.example.finalyearproject.database.DatabaseHelper;
import com.example.finalyearproject.database.PromptEvaluator;
import com.example.finalyearproject.functions.AchievementEdit;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class DailyUpdate extends JobService {
    public DailyUpdate() {
    }

    @Override
    public boolean onStartJob(JobParameters params) {

        //check if any achievements have been earned
        try {
            new AchievementEdit(this).transferCsvRow(this,"lockedachievements.csv","achievements.csv","Button 6");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

       try{
       DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
       // get the current date as a Date object
       Date currentDate = new Date();
       SharedPreferences UserStats = this.getSharedPreferences("UserStats", Context.MODE_PRIVATE);
       String lastDate = UserStats.getString("lastDate",null);
       long diffInMillies = Math.abs(currentDate.getTime() - dateFormat.parse(lastDate).getTime());
       long diffInDays = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
       //if last date was more than streak break time>=2
       if (diffInDays>=3){


           // Create an instance of PromptEvaluator
           PromptEvaluator promptEvaluator = new PromptEvaluator(this);


           // Create a new instance of the DatabaseHelper class
           DatabaseHelper dbHelper = new DatabaseHelper(this);

           Map<String, Integer> promptScores = promptEvaluator.evaluateAllPrompts(this);
           Map<String, Double> normalizedScores = new HashMap<>();
           int totalScore = 0;

           // Calculate the total sum of scores
           for (int score : promptScores.values()) {
               totalScore += score;
           }

           // Iterate over each entry and normalize the score
           for (Map.Entry<String, Integer> entry : promptScores.entrySet()) {
               String key = entry.getKey();
               int score = entry.getValue();
               double percentageScore = (double) score / totalScore * 100.0;
               normalizedScores.put(key, percentageScore);
           }

           // Randomly pick a prompt using the normalized scores
           double rand = Math.random()*100;
           System.out.println(rand);
           double cumulativeProb = 0.0;
           String selectedPrompt = null;
           for (Map.Entry<String, Double> entry : normalizedScores.entrySet()) {
               System.out.println(entry.getKey() + ": " + entry.getValue());
           }
           for (Map.Entry<String, Double> entry : normalizedScores.entrySet()) {
               String text = entry.getKey();
               double prob = entry.getValue();
               cumulativeProb += prob;
               if (rand <= cumulativeProb) {
                   selectedPrompt = text;
                   break;
               }
           }

           //notifaction settings changer
           SharedPreferences notifcationPrefs = this.getSharedPreferences("notifactionPrefrences", Context.MODE_PRIVATE);
           notifcationPrefs.edit().putString("Title","FYP").putString("Text",selectedPrompt).commit();




       }
           //rea
       } catch (ParseException e) {

       }


        return false;
    }

    //method to give achievements


    @Override
    public boolean onStopJob(JobParameters params) {

        System.out.println("job ended");

        return false;
    }


}