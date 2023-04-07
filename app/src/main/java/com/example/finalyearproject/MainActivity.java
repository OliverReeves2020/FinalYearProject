package com.example.finalyearproject;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.app.NotificationCompat;
import androidx.media.app.NotificationCompat.DecoratedMediaCustomViewStyle;

import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.finalyearproject.ui.main.SectionsPagerAdapter;
import com.example.finalyearproject.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {



        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        ViewPager viewPager = binding.viewPager;
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = binding.tabs;
        tabs.setupWithViewPager(viewPager);
        FloatingActionButton fab = binding.fab;

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });



        //create the notifcation
        // Create a notification channel
        System.out.println("here");
        NotificationChannel channel = new NotificationChannel("channel_id", "Channel Name", NotificationManager.IMPORTANCE_HIGH);
        channel.setDescription("Channel description");
        channel.setLightColor(ContextCompat.getColor(this,R.color.purple_200));
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);

        System.out.println("intent");
        Intent yesIntent = new Intent("com.example.myapp.YES");
        PendingIntent yesPendingIntent = PendingIntent.getBroadcast(this, 0, yesIntent, PendingIntent.FLAG_MUTABLE);

        Intent noIntent = new Intent("com.example.myapp.NO");
        PendingIntent noPendingIntent = PendingIntent.getBroadcast(this, 0, noIntent, PendingIntent.FLAG_MUTABLE);


        System.out.println("create");
        // Create a notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "channel_id")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setColorized(true)
                .setColor(ContextCompat.getColor(this,R.color.purple_200))
                .setStyle(new DecoratedMediaCustomViewStyle().setShowCancelButton(true))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentTitle("Title")
                .setContentText("Message")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setTimeoutAfter(10000)
                .addAction(com.google.android.material.R.drawable.mtrl_ic_check_mark, "Yes", yesPendingIntent)
                .addAction(com.google.android.material.R.drawable.mtrl_ic_cancel, "No", noPendingIntent);

        System.out.println("show");
        // Show the notification
        //NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, 1);
            return;
        }
        notificationManager.notify(1, builder.build());


        // Start the foreground service with the notification
        Intent serviceIntent = new Intent(this, MyForegroundService.class);
        serviceIntent.putExtra("notification_id", 1);
        serviceIntent.putExtra("notification", builder.build());
        ContextCompat.startForegroundService(this, serviceIntent);

        System.out.println("here");

    }


}