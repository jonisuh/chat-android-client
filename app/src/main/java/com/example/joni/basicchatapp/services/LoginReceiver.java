package com.example.joni.basicchatapp.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class LoginReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String message = intent.getStringExtra("message");

        Toast t = Toast.makeText(context, message, Toast.LENGTH_SHORT);
        t.show();
    }
} 