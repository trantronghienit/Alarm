package com.example.alarmpig.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.alarmpig.model.AlarmModel;

public class AutoStart extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("test", "onReceive: AutoStart" );
        AlarmModel alarm = new AlarmModel();
        AlarmReceiver alarmReceiver = new AlarmReceiver();
        if (intent.getAction() != null &&
                intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            alarmReceiver.setAlarm(context, alarm);
        }
    }
}