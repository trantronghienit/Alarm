package com.example.alarmpig.util;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import com.example.alarmpig.model.AlarmModel;
import com.example.alarmpig.receiver.AlarmReceiver;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class AlarmController {


    public static void setAlarm(Context context, AlarmModel info) {
        LogUtils.i("start alarm id: " + info.alarmId + "\ttime: " + info.hour + ":" + info.minute);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());

        calendar.set(Calendar.HOUR_OF_DAY, info.hour);
        calendar.set(Calendar.MINUTE, info.minute);
        calendar.set(Calendar.SECOND, info.second);
        //Add a day if alarm is set for before current time, so the alarm is triggered the next day
        if (calendar.before(Calendar.getInstance())) {
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra(Constants.HOUR, info.hour);
        intent.putExtra(Constants.MINUTE, info.minute);
        final HashMap<Integer, Boolean> days = info.getMapFormStringDay();
        ArrayList<Integer> keyList = new ArrayList<Integer>(days.keySet());
        ArrayList<Boolean> valueList = new ArrayList<Boolean>(days.values());
        ArrayList<Integer> daysId = new ArrayList<>();
        for (int i = 0; i < keyList.size(); i++) {
            if (valueList.get(i)) { // is check
                LogUtils.i("day alarm " + AlarmUtils.getDayFormIdDay(keyList.get(i)));
                daysId.add(keyList.get(i));
            }
        }
        intent.putExtra(Constants.DAY, daysId);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(context, info.alarmId, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP,
                calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, alarmIntent);

        ComponentName receiver = new ComponentName(context, AlarmReceiver.class);
        PackageManager pm = context.getPackageManager();
        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);
    }

    public static void cancelAlarm(Context context,int requestCode) {
        Intent intent = new Intent(context, AlarmReceiver.class);
        PendingIntent pIntent = PendingIntent.getBroadcast(context, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pIntent);
        LogUtils.i("cancel alarm id " + requestCode);
    }

    public static void cancelAlarmAll(Context context,List<AlarmModel> alarmModels) {
        Intent intent = new Intent(context, AlarmReceiver.class);
        for (AlarmModel item : alarmModels){
            PendingIntent pIntent = PendingIntent.getBroadcast(context, item.alarmId, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            alarmManager.cancel(pIntent);
            LogUtils.i("cancel alarm id " + item.alarmId + "label: " + item.label);
        }
    }

}
