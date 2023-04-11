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

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

public class NotifyReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {

        //look at notification format settings
        //configure notification
        //build notification

        //create channel
        NotificationChannel channel = new NotificationChannel("channel_id", "Reminder", NotificationManager.IMPORTANCE_HIGH);
        channel.setDescription("the following notifications are used to motivate you and are essential functionality");
        channel.setLightColor(ContextCompat.getColor(context, R.color.purple_200));
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
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
                .setContentTitle("REcieveder")
                .setContentText("Message")
                .setAutoCancel(false)
                .setDeleteIntent(delPendingIntent)
                .addAction(com.google.android.material.R.drawable.mtrl_ic_check_mark, "Yes", yesPendingIntent)
                .addAction(com.google.android.material.R.drawable.mtrl_ic_cancel, "Maybe Later", noPendingIntent);

        assert (ContextCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS)
                == PackageManager.PERMISSION_GRANTED);
        notificationManager.notify(1, builder.build());
            // Permission is granted, perform the operation
            // ...

    }


}