package com.example.finalyearproject;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MyBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        //if user responds with yes
        if(intent.getAction().equals("yes")) {


            try {
                Toast.makeText(context, "yes selected", Toast.LENGTH_LONG).show();
                // Handle the user's response of "yes"
                // add to total tally, streak, update last response, daily amount, trigger achievment giver

                SharedPreferences UserStats = context.getSharedPreferences("UserStats", Context.MODE_PRIVATE);
                SharedPreferences.Editor UserStatsEditor = UserStats.edit();
                int totalDays = UserStats.getInt("totalDays",0);
                int currentStreak = UserStats.getInt("currentStreak",0);
                int dailyAmount = UserStats.getInt("dailyAmount",0);
                String lastDate = UserStats.getString("lastDate",null);

                DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                // get the current date as a Date object
                Date currentDate = new Date();
                // format the date using the SimpleDateFormat object
                String currentDateString = dateFormat.format(currentDate);

                //if no previous date has been recorded then record current and update tally
                if(lastDate==null){
                    UserStatsEditor.putString("lastDate",currentDateString);
                    UserStatsEditor.putInt("currentStreak",1);
                    UserStatsEditor.putInt("totalDays",1);
                    UserStatsEditor.putInt("dailyAmount",1);
                }
                //else if date matches todays date

                else if(lastDate.equals(currentDateString)){
                    UserStatsEditor.putInt("dailyAmount",1+dailyAmount);

                    Toast.makeText(context, "here", Toast.LENGTH_SHORT).show();
                }
                //else if date is later/newer
                else if(currentDate.compareTo(dateFormat.parse(lastDate))>0){
                    UserStatsEditor.putInt("dailyAmount",1);
                    UserStatsEditor.putInt("totalDays",1+totalDays);
                    UserStatsEditor.putInt("currentStreak",1+currentStreak);
                    UserStatsEditor.putString("lastDate",currentDateString);
                }


                //apply changes
                UserStatsEditor.apply();
                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
                notificationManager.cancel(1);
                //display to user
                //call achivement trigger

            } catch (ParseException e) {
                Toast.makeText(context,"retry",Toast.LENGTH_SHORT);
            }


        }
        //if user responds with remind me later
            //schedule a new notifcation of x time
            //close current notifcation
        else if (intent.getAction().equals("remind")) {

            Toast.makeText(context, "will remind you later", Toast.LENGTH_LONG).show();
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
            notificationManager.cancel(1);
        }
        //if user dismiss the notification
        //decrease notification score of weighted picker
        else if(intent.getAction().equals("del")){
            Toast.makeText(context, "deleted", Toast.LENGTH_LONG).show();
        }
    }
}

