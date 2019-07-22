package com.example.alarmpig.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

import androidx.core.app.NotificationCompat;

import com.example.alarmpig.R;
import com.example.alarmpig.util.AudioUtil;
import com.example.alarmpig.util.Constants;
import com.example.alarmpig.util.LogUtils;
import com.example.alarmpig.view.activity.MainActivity;

import static com.example.alarmpig.App.CHANNEL_ID;

public class AlarmService extends Service {

    private long lastTime = 0;

    @Override
    public int onStartCommand(final Intent intent, int flags,final int startId) {
        LogUtils.s("onStartCommand: AlarmService");
        String input = "thông báo";
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, 0);

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Example Service")
                .setContentText(input)
                .setSmallIcon(R.drawable.ic_alarm_active)
                .setContentIntent(pendingIntent)
                .build();
//
        startForeground(1, notification);

        AudioUtil.play(this, true, new AudioUtil.OnMediaPlayListener() {
            @Override
            public void onPrepared() {
                LogUtils.i("play media onPrepared");
            }

            @Override
            public void onError() {
                LogUtils.e("play media error");
            }

            @Override
            public void onRepeatSuccess(int loopCount) {
                AudioUtil.repeat(3);
                LogUtils.i("play media onRepeatSuccess " + loopCount);
            }

            @Override
            public void onCompleted() {
                AlarmService.this.stopSelf(startId);
                LogUtils.s("stop service alarm");
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
                if (Constants.ENABLE_MAX_VOLUME && isValidCycle && intent.getAction() != null && intent.getAction().equals(Constants.ACTION_VOLUME_CHANGE)){
                    LogUtils.i("AlarmService onReceive: change volume button");
                    AudioUtil.maxVolume(context);
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
