package com.example.alarmpig.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.IBinder;
import android.os.Vibrator;
import android.util.Log;

import com.example.alarmpig.R;
import com.example.alarmpig.receiver.Alarm;
import com.example.alarmpig.util.AudioUtil;
import com.example.alarmpig.util.Constans;

import java.io.IOException;

public class AlarmService extends Service {

    private long lastTime = 0;
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("test", "onStartCommand: AlarmService");
        AudioUtil.play(this , true);
        checkPressButtonVolume();
        return START_STICKY;
    }

    private void checkPressButtonVolume() {
        final BroadcastReceiver vReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                long currentTime = System.currentTimeMillis();
                boolean isValidCycle = currentTime - lastTime < 1000; // 1 giay
                if (isValidCycle && intent.getAction() != null && intent.getAction().equals(Constans.ACTION_VOLUME_CHANGE)){
                    Log.i("test", "onReceive: change volume button");
//                    AudioUtil.maxVolume(context);
                }
                lastTime = currentTime;
            }
        };
        registerReceiver(vReceiver, new IntentFilter(Constans.ACTION_VOLUME_CHANGE));
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
