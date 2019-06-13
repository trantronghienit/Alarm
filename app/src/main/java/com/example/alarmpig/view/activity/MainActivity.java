package com.example.alarmpig.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import com.example.alarmpig.R;
import com.example.alarmpig.db.AppDatabase;
import com.example.alarmpig.model.AlarmModel;
import com.example.alarmpig.receiver.AlarmReceiver;
import com.example.alarmpig.service.AlarmService;

import java.util.Calendar;

import static com.example.alarmpig.util.Constans.DATABASE_NAME;

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
        final AlarmReceiver alarm = new AlarmReceiver();
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

        btnDashboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentToService = new Intent(MainActivity.this, DashboardAlarmActivity.class);
                startActivity(intentToService);
            }
        });
    }
}
