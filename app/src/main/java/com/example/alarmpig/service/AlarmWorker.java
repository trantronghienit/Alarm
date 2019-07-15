package com.example.alarmpig.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import androidx.annotation.NonNull;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.alarmpig.util.AudioUtil;
import com.example.alarmpig.util.Constants;
import com.example.alarmpig.util.LogUtils;

public class AlarmWorker extends Worker implements AudioUtil.OnMediaPlayListener{

    private long lastTime = 0;
    private Context context;
    private Result result;

    public AlarmWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        this.context = context;
    }

    @NonNull
    @Override
    public Result doWork() {
        AudioUtil.play(context, true, this);
        checkPressButtonVolume();
        return result;
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
        if (context != null){
            context.registerReceiver(vReceiver, new IntentFilter(Constants.ACTION_VOLUME_CHANGE));
        }

    }

    @Override
    public void onPrepared() {
        LogUtils.i("play media onPrepared");

    }

    @Override
    public void onError() {
        LogUtils.e("play media error");
        result = Result.failure();
    }

    @Override
    public void onRepeatSuccess(int loopCount) {
        AudioUtil.repeat(3);
        LogUtils.i("play media onRepeatSuccess " + loopCount);
        result = Result.retry();
    }

    @Override
    public void onCompleted() {
        LogUtils.s("stop service alarm");
        WorkManager.getInstance().cancelAllWorkByTag(Constants.TAG_ALARM_WORKER);
        //WorkManager.getInstance().cancelWorkById(workId);
    }
}
