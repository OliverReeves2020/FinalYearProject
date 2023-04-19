package com.example.finalyearproject;

import static android.content.Context.ALARM_SERVICE;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Toast;

import androidx.core.app.NotificationManagerCompat;

import com.example.finalyearproject.database.DatabaseHelper;
import com.example.finalyearproject.database.PromptEvaluator;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class MyBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        //if user responds with yes to the notification
        if(intent.getAction().equals("yes")) {


            try {
                // Handle the user's response of "yes"
                // add to total tally, streak, update last response, daily amount, trigger achievment giver

                SharedPreferences UserStats = context.getSharedPreferences("UserStats", Context.MODE_PRIVATE);
                SharedPreferences.Editor UserStatsEditor = UserStats.edit();
                int totalDays = UserStats.getInt("totalDays",0);
                int currentStreak = UserStats.getInt("currentStreak",0);
                int HighestStreak = UserStats.getInt("highestStreak",0);
                int dailyAmount = UserStats.getInt("dailyAmount",0);


                //add to database highest streak
                String prompt = context.getSharedPreferences("notifactionPrefrences", Context.MODE_PRIVATE).getString("Text",null);
                if (prompt!=null){
                    // Create a new instance of the DatabaseHelper class
                    DatabaseHelper dbHelper = new DatabaseHelper(context);

                    // Call the getPromptHighestStreak method with the prompt value
                    int highestStreak = dbHelper.getPromptHighestStreak(prompt);
                    if (highestStreak>currentStreak){
                        dbHelper.setPromptHighestStreak(prompt,highestStreak);
                    }
                    //add to database postive response
                    PromptEvaluator promptEvaluator = new PromptEvaluator(context);
                    promptEvaluator.evaluatePrompt(prompt, true);
                    promptEvaluator.close();
                }






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



                    long diffInMillies = Math.abs(currentDate.getTime() - dateFormat.parse(lastDate).getTime());
                    long diffInDays = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);

                    //if streak is broken
                    if (diffInDays >= 2) {
                        UserStatsEditor.putInt("dailyAmount",1);
                        UserStatsEditor.putInt("totalDays",1+totalDays);
                        UserStatsEditor.putInt("currentStreak",1);
                        UserStatsEditor.putString("lastDate",currentDateString);


                    } //else streak continues
                    else {
                        UserStatsEditor.putInt("dailyAmount",1);
                        UserStatsEditor.putInt("totalDays",1+totalDays);
                        UserStatsEditor.putInt("currentStreak",1+currentStreak);
                        UserStatsEditor.putString("lastDate",currentDateString);
                    }
                    //if current streak is greater than highest recorde add
                    if(currentStreak>HighestStreak){
                        UserStatsEditor.putInt("HighestStreak",currentStreak);
                    }


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

            //schedule another notification for 30mins
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.MINUTE, 30);
            Intent newintent = new Intent(context, NotifyReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, newintent, PendingIntent.FLAG_IMMUTABLE);
            AlarmManager alarmManager = (AlarmManager)  context.getSystemService(ALARM_SERVICE);
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),pendingIntent);

            //cancel current notifcation
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
            notificationManager.cancel(1);
        }
        //if user dismiss the notification
        //decrease notification score of weighted picker
        else if(intent.getAction().equals("del")){
            //add to database postive response
            //add to database highest streak
            String prompt = context.getSharedPreferences("notifactionPrefrences", Context.MODE_PRIVATE).getString("Text",null);
            if (prompt!=null){
                //add to database negative response
                PromptEvaluator promptEvaluator = new PromptEvaluator(context);
                promptEvaluator.evaluatePrompt(prompt, false);
                promptEvaluator.close();
            }
        }
        //if yes done inside app
        else if(intent.getAction().equals("yesapp")) {


            try {


                SharedPreferences UserStats = context.getSharedPreferences("UserStats", Context.MODE_PRIVATE);
                SharedPreferences.Editor UserStatsEditor = UserStats.edit();
                int totalDays = UserStats.getInt("totalDays",0);
                int currentStreak = UserStats.getInt("currentStreak",0);
                int HighestStreak = UserStats.getInt("highestStreak",0);
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
                }
                //else if date is later/newer
                else if(currentDate.compareTo(dateFormat.parse(lastDate))>0){



                    long diffInMillies = Math.abs(currentDate.getTime() - dateFormat.parse(lastDate).getTime());
                    long diffInDays = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);

                    //if streak is broken
                    if (diffInDays >= 2) {
                        UserStatsEditor.putInt("dailyAmount",1);
                        UserStatsEditor.putInt("totalDays",1+totalDays);
                        UserStatsEditor.putInt("currentStreak",1);
                        UserStatsEditor.putString("lastDate",currentDateString);


                    } //else streak continues
                    else {
                        UserStatsEditor.putInt("dailyAmount",1);
                        UserStatsEditor.putInt("totalDays",1+totalDays);
                        UserStatsEditor.putInt("currentStreak",1+currentStreak);
                        UserStatsEditor.putString("lastDate",currentDateString);
                    }
                    //if current streak is greater than highest recorde add
                    if(currentStreak>HighestStreak){
                        UserStatsEditor.putInt("HighestStreak",currentStreak);
                    }


                }


                //apply changes
                UserStatsEditor.apply();
                //remove notification in case
                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
                notificationManager.cancel(1);
                //display to user
                //call achivement trigger

            } catch (ParseException e) {
                Toast.makeText(context,"retry",Toast.LENGTH_SHORT);
            }


        }
    }
}

