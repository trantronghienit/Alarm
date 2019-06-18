package com.example.alarmpig.receiver;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.util.SparseIntArray;

import androidx.core.app.NotificationCompat;

import com.example.alarmpig.R;
import com.example.alarmpig.model.AlarmModel;
import com.example.alarmpig.service.AlarmService;
import com.example.alarmpig.util.AlarmUtils;
import com.example.alarmpig.util.Constants;
import com.example.alarmpig.util.LogUtils;
import com.example.alarmpig.util.UtilHelper;
import com.example.alarmpig.view.activity.MainActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class AlarmReceiver extends BroadcastReceiver {
    private static final int TIME_VIBRATE = 1000;

    @Override
    public void onReceive(Context context, Intent intent) {
        LogUtils.r("start Receiver alarm");
        if (intent.getExtras() != null) {
            Bundle bundle = intent.getExtras();
            int hour = bundle.getInt(Constants.HOUR, 0);
            ArrayList<Integer> day = bundle.getIntegerArrayList(Constants.DAY);
            int minute = bundle.getInt(Constants.MINUTE, 0);

            if (day != null && AlarmUtils.checkExistsCurrentDay(day)){
                LogUtils.r("Receiver alarm day: " + AlarmUtils.getDayFormIdDay(Calendar.getInstance().get(Calendar.DAY_OF_WEEK)) + ":" + hour + ":" + minute);
                startServiceAlarm(context);
                showNotification(context, intent);
            }else {
                LogUtils.r("not alarm with day: " + AlarmUtils.getDayFormIdDay(Calendar.getInstance().get(Calendar.DAY_OF_WEEK)) + ":" + hour + ":" + minute);
            }
        }
    }

    private void startServiceAlarm(Context context) {
        Intent intentToService = new Intent(context, AlarmService.class);
        context.startService(intentToService);
    }

    private void showNotification(Context context, Intent intent) {
        int index = intent.getIntExtra(Constants.KEY_TYPE, 0);
        Intent notificationIntent = new Intent(context, MainActivity.class);
        notificationIntent
                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        int requestID = (int) System.currentTimeMillis();
        PendingIntent contentIntent = PendingIntent
                .getActivity(context, requestID, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle(UtilHelper.getStringRes(R.string.app_name))
                        .setContentText("nhấn vào để tắt báo thức")
//                        .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
//                        .setDefaults(Notification.DEFAULT_SOUND)
                        .setAutoCancel(true)
                        .setPriority(6)
                        .setVibrate(new long[]{TIME_VIBRATE, TIME_VIBRATE, TIME_VIBRATE, TIME_VIBRATE,
                                TIME_VIBRATE})
                        .setContentIntent(contentIntent);
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(index, builder.build());
    }
}