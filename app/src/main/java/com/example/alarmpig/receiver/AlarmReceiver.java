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
import android.util.Log;

import com.example.alarmpig.R;
import com.example.alarmpig.model.AlarmModel;
import com.example.alarmpig.service.AlarmService;
import com.example.alarmpig.util.AlarmUtils;
import com.example.alarmpig.util.LogUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class AlarmReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {
        LogUtils.r("start Receiver alarm");
        startServiceAlarm(context);
    }

    private void startServiceAlarm(Context context){
        Intent intentToService = new Intent(context, AlarmService.class);
        context.startService(intentToService);
    }

    private void showNotification(Context context, Intent intent) {
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = new Notification(R.drawable.ic_alarm_white, "Wake up alarm", System.currentTimeMillis());
        notification.flags = Notification.FLAG_INSISTENT;
        notification.sound = Uri.parse("android.resource://com.example.alarmpig/" + R.raw.notification);
        notification.contentIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);;
        manager.notify(1, notification);
    }

    public void setAlarm(Context context, AlarmModel info) {
        LogUtils.r("start send broadcast alarm");
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmReceiver.class);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat format1= new SimpleDateFormat("hh:mm:ss");
        Date dt1 = null;
        try {
            dt1 = format1.parse(info.hour + ":" + info.minute + info.second);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        calendar.setTime(dt1);
//        calendar.setTimeInMillis(System.currentTimeMillis());
//        calendar.set(Calendar.HOUR, );
//        calendar.set(Calendar.MINUTE, );
//        calendar.set(Calendar.SECOND, );

        final HashMap<Integer , Boolean> days = info.getMapFormStringDay();
        ArrayList<Integer> keyList = new ArrayList<Integer>(days.keySet());
        ArrayList<Boolean> valueList = new ArrayList<Boolean>(days.values());
        for (int i = 0; i < keyList.size(); i++) {
            if (valueList.get(i)){ // is check
                calendar.set(Calendar.DAY_OF_WEEK, keyList.get(i));
            }
        }

        am.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                AlarmManager.ELAPSED_REALTIME, alarmIntent);

        ComponentName receiver = new ComponentName(context, AlarmReceiver.class);
        PackageManager pm = context.getPackageManager();
        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);
    }

    public void cancelAlarm(Context context) {
        Intent intent = new Intent(context, AlarmReceiver.class);
        PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(sender);
    }
}