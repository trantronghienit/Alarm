package com.example.alarmpig.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.alarmpig.R;
import com.example.alarmpig.model.AlarmModel;
import com.example.alarmpig.receiver.AlarmReceiver;
import com.example.alarmpig.service.AlarmService;
import com.example.alarmpig.util.LogUtils;

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
                Intent intentToService = new Intent(MainActivity.this, AlarmService.class);
                stopService(intentToService);
                startAlarm();
            }
        });

        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentToService = new Intent(MainActivity.this, AlarmService.class);
                stopService(intentToService);
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
        AlarmReceiver alarmReceiver = new AlarmReceiver();
        List<AlarmModel> alarms  = appDatabase.AlarmDAO().getAllAlarm();
        for (AlarmModel item : alarms){
            alarmReceiver.setAlarm(MainActivity.this, item);
            LogUtils.i(item.toString());
        }
    }
}
