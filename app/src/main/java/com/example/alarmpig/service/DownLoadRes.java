package com.example.alarmpig.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.example.alarmpig.util.LogUtils;

public class DownLoadRes extends Service {

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LogUtils.s("onStartCommand: AlarmService");
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }
}
