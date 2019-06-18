package com.example.alarmpig.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.room.Room;

import com.example.alarmpig.db.AppDatabase;
import com.example.alarmpig.model.AlarmModel;
import com.example.alarmpig.util.AlarmController;
import com.example.alarmpig.util.LogUtils;

import java.util.List;

import static com.example.alarmpig.util.Constants.DATABASE_NAME;

public class AutoStart extends BroadcastReceiver {
    protected AppDatabase appDatabase;

    @Override
    public void onReceive(Context context, Intent intent) {
        LogUtils.r("boot completed start alarm");
        if (intent.getAction() != null &&
                intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            appDatabase = Room.databaseBuilder(context ,
                    AppDatabase.class, DATABASE_NAME)
                    .allowMainThreadQueries()
                    .fallbackToDestructiveMigration()
                    .build();
            List<AlarmModel> alarms  = appDatabase.AlarmDAO().getAllAlarm();
            for (AlarmModel item : alarms){
                AlarmController.setAlarm(context, item);
            }
        }
    }
}