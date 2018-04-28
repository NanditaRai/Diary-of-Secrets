package com.example.diaryofsecrets.navigation;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Nandita Rai on 2/3/2018.
 */

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.diaryofsecrets.DiaryPreference;
import com.example.diaryofsecrets.MyApplication;

/**
 * Created by ptyagi on 4/18/17.
 */

public class AlarmBootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            //only enabling one type of notifications for demo purposes
            DiaryPreference diaryPreference = new DiaryPreference(MyApplication.getContext());
            if(diaryPreference.getAlarm() != null) {
                String[] time = diaryPreference.getAlarm().split(",");
                NotificationHelper.scheduleRepeatingRTCNotification(context, time[0], time[1]);
            }
        }
    }
}