package com.example.finalyearproject;

import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;
import androidx.viewpager.widget.ViewPager;

import com.example.finalyearproject.databinding.ActivityMainBinding;
import com.example.finalyearproject.jobs.DailyUpdate;
import com.example.finalyearproject.ui.main.SectionsPagerAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import java.util.Calendar;


public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);


        SharedPreferences pref = getSharedPreferences("startup", MODE_PRIVATE);
        if (pref.getString("start", "false").equals("false")) {

        Dialog onboardingDialog = new Dialog(this);
        onboardingDialog.setContentView(R.layout.dialog_onboarding);
        onboardingDialog.show();
        Button btnNext = onboardingDialog.findViewById(R.id.btn_next);
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                pref.edit().putString("start", "true").apply();
                onboardingDialog.dismiss();


                // Proceed to the next step
            }
        });

    }


        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        //get bindings view pager of main and set adapter to it
        ViewPager viewPager = binding.viewPager;
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = binding.tabs;
        tabs.setupWithViewPager(viewPager);

        // Set tab titles
        final int[] TAB_TITLES = new int[] {R.string.tab_text_1, R.string.tab_text_2};
        for (int i = 0; i < TAB_TITLES.length; i++) {
            tabs.getTabAt(i).setText(TAB_TITLES[i]);
        }

        //start background animation purely aesthetics
        AnimationDrawable animationDrawable = (AnimationDrawable) viewPager.getBackground();
        animationDrawable.setEnterFadeDuration(2500);
        animationDrawable.setExitFadeDuration(5000);
        animationDrawable.start();
        FloatingActionButton fab = binding.fab;
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //SharedPreferences UserStats = this.getSharedPreferences("UserStats", Context.MODE_PRIVATE);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Snackbar.make(view, "recorded activity, well done!", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

                Intent intent = new Intent(view.getContext(),MyBroadcastReceiver.class);
                intent.setAction("yes");
                view.getContext().sendBroadcast(intent);

            }
        });




        // Set the time to trigger the alarm (e.g., 6 pm every day)
        Calendar calendar = Calendar.getInstance();
        //calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.add(Calendar.SECOND, 2);

        // Create a pending intent to trigger the service
        Intent intent = new Intent(this, NotifyReceiver.class);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        // Get the alarm manager and schedule the periodic alarm
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),pendingIntent);

       //alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
         //AlarmManager.INTERVAL_FIFTEEN_MINUTES, pendingIntent);




        // Set a window of 2 hours (2pm to 4pm)
        //long lasttime=2 * 60 * 60 * 1000;
        //alarmManager.setWindow(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),lasttime, pendingIntent);
        Toast.makeText(this, "Starting Service Alarm", Toast.LENGTH_LONG).show();



        scheduleJob();





        System.out.println("here");

    }

    private void scheduleJob() {
        JobScheduler jobScheduler = (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);
        ComponentName componentName = new ComponentName(this, DailyUpdate.class);

        // Set the conditions under which the job should run.
        //JobInfo jobInfo = new JobInfo.Builder(1, componentName)
        //        .setRequiresCharging(true)
        //        .setRequiresDeviceIdle(true)
        //        .setPeriodic(AlarmManager.INTERVAL_DAY)
        //        .setPersisted(true)
        //        .build();

        // Set the conditions under which the job should run.
        JobInfo jobInfo = new JobInfo.Builder(1, componentName)
                .setMinimumLatency(100)
                .setPersisted(true)
                .build();


        // Schedule the job.
        int resultCode = jobScheduler.schedule(jobInfo);
        if (resultCode == JobScheduler.RESULT_SUCCESS) {
            Toast.makeText(this, "job completed", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "job failed",Toast.LENGTH_SHORT).show();
        }
    }





}