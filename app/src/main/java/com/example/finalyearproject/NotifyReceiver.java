package com.example.finalyearproject;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.RingtoneManager;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceManager;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import ca.antonious.materialdaypicker.MaterialDayPicker;

public class NotifyReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {


        //look at notification format settings
        //configure notification dettion triggered on timeout
        //build notification
        assert (ContextCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS)
                == PackageManager.PERMISSION_GRANTED);


        //load notifcation prefrences
        Set<String> SelectedDays = new HashSet<String>();
        SelectedDays.add("Monday");
        SelectedDays.add("Tuesday");
        SelectedDays.add("Wednesday");
        SelectedDays.add("Thursday");
        SelectedDays.add("Friday");
        SelectedDays.add("Saturday");
        SelectedDays.add("Sunday");
        SharedPreferences notifcationPrefs = context.getSharedPreferences("notifactionPrefrences", Context.MODE_PRIVATE);
        Set<String> selectedDays = notifcationPrefs.getStringSet("selectedDays", SelectedDays);

        //check that notifaction is allowed today
        SimpleDateFormat f = new SimpleDateFormat("EEEE");
        System.out.println("-->"+selectedDays);
        if (selectedDays.contains(f.format(new Date()))) {


        boolean Silence = notifcationPrefs.getBoolean("Silence", false);
        String Title = notifcationPrefs.getString("Title", "FYP");
        String Text = notifcationPrefs.getString("Text", "shall we go for a walk");
        String yesAction = notifcationPrefs.getString("yesAction", "yes");
        String noAction = notifcationPrefs.getString("noAction", "maybe later");

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        //delete old channel


        //create channel
        NotificationChannel channel = new NotificationChannel("channel_id", "Reminder", NotificationManager.IMPORTANCE_HIGH);
        channel.setDescription("the following notifications are used to motivate you and are essential functionality");
        channel.setLightColor(ContextCompat.getColor(context, R.color.purple_200));
        channel.enableVibration(true);
        notificationManager.createNotificationChannel(channel);


        // Create a pending intent to handle the Yes
        Intent yesIntent = new Intent(context, MyBroadcastReceiver.class);
        yesIntent.setAction("yes");
        PendingIntent yesPendingIntent = PendingIntent.getBroadcast(context, 0, yesIntent, PendingIntent.FLAG_MUTABLE);

        //and Remind me later actions
        Intent noIntent = new Intent(context, MyBroadcastReceiver.class);
        noIntent.setAction("remind");
        PendingIntent noPendingIntent = PendingIntent.getBroadcast(context, 0, noIntent, PendingIntent.FLAG_MUTABLE);

        //when user dissmisses this will be called
        Intent delIntent = new Intent(context, MyBroadcastReceiver.class);
        delIntent.setAction("del");
        PendingIntent delPendingIntent = PendingIntent.getBroadcast(context, 0, delIntent, PendingIntent.FLAG_MUTABLE);

        // Create the notification with a Yes or No question
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "channel_id")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setColorized(true)
                .setColor(ContextCompat.getColor(context, R.color.purple_200))
                .setStyle(new NotificationCompat.BigTextStyle())
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentTitle(Title)
                .setContentText(Text)
                .setAutoCancel(false)
                .setDeleteIntent(delPendingIntent)
                .addAction(com.google.android.material.R.drawable.mtrl_ic_check_mark, yesAction, yesPendingIntent)
                .addAction(com.google.android.material.R.drawable.mtrl_ic_cancel, noAction, noPendingIntent);

        builder.setVibrate(new long[]{0, 500, 1000, 500, 1000, 500});
        builder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION), AudioManager.STREAM_NOTIFICATION);
        if (Silence) {
            builder.setSilent(true);
        }
        // Calculate the time remaining until the end of the day
        long timeDiff = TimeUnit.DAYS.toMillis(1) - System.currentTimeMillis() % TimeUnit.DAYS.toMillis(1);

        builder.setTimeoutAfter(timeDiff);

        notificationManager.notify(1, builder.build());
    }

    }


}