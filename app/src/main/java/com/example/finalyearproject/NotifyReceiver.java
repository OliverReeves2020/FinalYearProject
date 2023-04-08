package com.example.finalyearproject;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

public class NotifyReceiver extends BroadcastReceiver {

    @SuppressLint("MissingPermission")
    @Override
    public void onReceive(Context context, Intent intent) {

        //look at notification format settings
        //configure notification
        //build notification





        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        //Toast.makeText(this, "MyAlarmService.onStart()", Toast.LENGTH_LONG).show();
        NotificationChannel channel = new NotificationChannel("channel_id", "Channel Name", NotificationManager.IMPORTANCE_HIGH);
        channel.setDescription("Channel description");
        channel.setLightColor(ContextCompat.getColor(context,R.color.purple_200));
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.createNotificationChannel(channel);

        // Create a pending intent to handle the Yes and No actions
        Intent yesIntent = new Intent(context,MyBroadcastReceiver.class);
        yesIntent.setAction("com.example.myapp.YES");
        PendingIntent yesPendingIntent = PendingIntent.getBroadcast(context, 0, yesIntent, PendingIntent.FLAG_MUTABLE);

        Intent noIntent = new Intent(context,MyBroadcastReceiver.class);
        noIntent.setAction("com.example.myapp.YES");
        PendingIntent noPendingIntent = PendingIntent.getBroadcast(context, 0, noIntent, PendingIntent.FLAG_MUTABLE);


        // Create the notification with a Yes or No question
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "channel_id")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setColorized(true)
                .setColor(ContextCompat.getColor(context,R.color.purple_200))
                .setStyle(new androidx.media.app.NotificationCompat.DecoratedMediaCustomViewStyle().setShowCancelButton(true))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentTitle("REcieveder")
                .setContentText("Message")
                .setAutoCancel(false)
                .addAction(com.google.android.material.R.drawable.mtrl_ic_check_mark, "Yes", yesPendingIntent)
                .addAction(com.google.android.material.R.drawable.mtrl_ic_cancel, "No", noPendingIntent);

        if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS)
                == PackageManager.PERMISSION_GRANTED) {
            notificationManager.notify(1, builder.build());
            // Permission is granted, perform the operation
            // ...
        } else {
            // Permission is not granted, request it from the user

        }

    }
}