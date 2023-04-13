package com.example.finalyearproject;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import androidx.core.app.ActivityCompat;
import androidx.preference.PreferenceManager;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.finalyearproject.ui.main.SectionsPagerAdapter;
import com.example.finalyearproject.databinding.ActivityMainBinding;


import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.util.Calendar;
import java.util.concurrent.CompletableFuture;

import jp.wasabeef.blurry.Blurry;


public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);

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
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences UserStats = this.getSharedPreferences("UserStats", Context.MODE_PRIVATE);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Snackbar.make(view, String.valueOf((UserStats.getInt("dailyAmount",0))), Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });



        //findViewById(R.id.ChartArea).setBackground(new BitmapDrawable(getResources(), blurredBitmap));



        //schedule alarm


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



        System.out.println("here");

    }




}