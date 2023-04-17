package com.example.finalyearproject.jobs;

import android.app.Service;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.widget.Toast;

import java.time.Duration;

public class DailyUpdate extends JobService {
    public DailyUpdate() {
    }

    @Override
    public boolean onStartJob(JobParameters params) {

        System.out.println("job called");
        //check if streak has been broken and configure new weightings

        //run achievemnt creator

        //update any values


        //notifaction settings changer
        SharedPreferences notifcationPrefs = this.getSharedPreferences("notifactionPrefrences", Context.MODE_PRIVATE);
        notifcationPrefs.edit().putString("Title","new").putString("Text","new description").commit();





        return false;
    }

    @Override
    public boolean onStopJob(JobParameters params) {

        System.out.println("job ended");

        return false;
    }


}