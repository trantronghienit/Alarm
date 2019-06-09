package com.example.alarmpig;

import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.example.alarmpig.model.AlarmModel;
import com.example.alarmpig.receiver.Alarm;
import com.example.alarmpig.service.AlarmService;

import java.io.IOException;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private Button btnStart;
    private Button btnStop;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnStart = findViewById(R.id.btnStart);
        btnStop = findViewById(R.id.btnStop);
        final Alarm alarm = new Alarm();
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlarmModel model = new AlarmModel();
                Calendar rightNow = Calendar.getInstance();
                model.hour = rightNow.get(Calendar.HOUR_OF_DAY);
                model.minute = rightNow.get(Calendar.MINUTE);
                model.second = rightNow.get(Calendar.SECOND) + 6;
                alarm.setAlarm(MainActivity.this, model);
            }
        });

        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentToService = new Intent(MainActivity.this, AlarmService.class);
                stopService(intentToService);
            }
        });
    }
}
