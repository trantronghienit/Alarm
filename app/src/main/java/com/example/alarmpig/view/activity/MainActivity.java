package com.example.alarmpig.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.alarmpig.R;
import com.example.alarmpig.model.AlarmModel;
import com.example.alarmpig.service.AlarmService;
import com.example.alarmpig.util.AlarmController;
import com.example.alarmpig.util.LogUtils;
import com.example.alarmpig.util.UtilHelper;

import java.util.List;

public class MainActivity extends BaseActivity {

    private Button btnStart;
    private Button btnStop;
    private Button btnDashboard;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnStart = findViewById(R.id.btnStart);
        btnStop = findViewById(R.id.btnStop);
        btnDashboard = findViewById(R.id.btnDashboard);
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UtilHelper.stopSevicesAlarm(MainActivity.this);
                stopAlarm();
                startAlarm();
            }
        });

        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UtilHelper.stopSevicesAlarm(MainActivity.this);
                stopAlarm();
            }
        });

        btnDashboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentToService = new Intent(MainActivity.this, DashboardAlarmActivity.class);
                startActivity(intentToService);
            }
        });
    }

    private void startAlarm() {
        List<AlarmModel> alarms = appDatabase.AlarmDAO().getAllAlarm();
        for (AlarmModel item : alarms) {
            AlarmController.setAlarm(this, item);
            LogUtils.i(item.toString());
        }
    }

    private void stopAlarm() {
        List<AlarmModel> alarmModels = appDatabase.AlarmDAO().getAllAlarm();
        AlarmController.cancelAlarmAll(this, alarmModels);
    }

}
