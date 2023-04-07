package com.example.finalyearproject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class MyBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals("com.example.myapp.YES")) {
            // Handle the user's response of "yes"
        } else if(intent.getAction().equals("com.example.myapp.NO")) {
            // Handle the user's response of "no"
        }
    }
}

