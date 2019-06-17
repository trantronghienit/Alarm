package com.example.alarmpig.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

import com.example.alarmpig.util.AudioUtil;
import com.example.alarmpig.util.Constants;
import com.example.alarmpig.util.LogUtils;

public class AlarmService extends Service {

    private long lastTime = 0;
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LogUtils.s("onStartCommand: AlarmService");
        AudioUtil.play(this, true, new AudioUtil.OnMediaPlayListener() {
            @Override
            public void onPrepared() {
                LogUtils.e("play media onPrepared");
            }

            @Override
            public void onError() {
                LogUtils.e("play media error");
            }

            @Override
            public void onCompletion(int loopCount) {
                AudioUtil.loop(3);
                LogUtils.e("play media complete");
            }
        });
        checkPressButtonVolume();
        return START_STICKY;
    }

    private void checkPressButtonVolume() {
        final BroadcastReceiver vReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                long currentTime = System.currentTimeMillis();
                boolean isValidCycle = currentTime - lastTime < 1000; // 1 giay
                if (isValidCycle && intent.getAction() != null && intent.getAction().equals(Constants.ACTION_VOLUME_CHANGE)){
                    LogUtils.i("AlarmService onReceive: change volume button");
                    // todo
//                    AudioUtil.maxVolume(context);
                }
                lastTime = currentTime;
            }
        };
        registerReceiver(vReceiver, new IntentFilter(Constants.ACTION_VOLUME_CHANGE));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        AudioUtil.stop();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
