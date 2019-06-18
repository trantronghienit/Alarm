package com.example.alarmpig.view.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.alarmpig.R;
import com.example.alarmpig.model.AlarmModel;
import com.example.alarmpig.util.Constants;
import com.example.alarmpig.util.ViewUtils;

import java.util.Calendar;

public class AddAlarmActivity extends BaseActivity {


    private TimePicker mTimePicker;
    private EditText mLabel;
    private CheckBox mMon, mTues, mWed, mThurs, mFri, mSat, mSun;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_alarm);


        mTimePicker = findViewById(R.id.edit_alarm_time_picker);
        mTimePicker.setIs24HourView(true);

        mLabel = findViewById(R.id.edit_alarm_label);

        mMon = findViewById(R.id.edit_alarm_mon);
        mTues = findViewById(R.id.edit_alarm_tues);
        mWed = findViewById(R.id.edit_alarm_wed);
        mThurs = findViewById(R.id.edit_alarm_thurs);
        mFri = findViewById(R.id.edit_alarm_fri);
        mSat = findViewById(R.id.edit_alarm_sat);
        mSun = findViewById(R.id.edit_alarm_sun);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_alarm_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_save:
                addAlarm();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void addAlarm() {
        try {
            String label = TextUtils.isEmpty(mLabel.getText().toString()) ? "label_" + System.currentTimeMillis() : mLabel.getText().toString();
            String message = mLabel.getText().toString();
            AlarmModel alarm = new AlarmModel();

            final Calendar time = Calendar.getInstance();
            time.set(Calendar.MINUTE, ViewUtils.getTimePickerMinute(mTimePicker));
            time.set(Calendar.HOUR_OF_DAY, ViewUtils.getTimePickerHour(mTimePicker));

            alarm.minute = ViewUtils.getTimePickerMinute(mTimePicker);
            alarm.second = 0;
            alarm.hour = ViewUtils.getTimePickerHour(mTimePicker);
            alarm.label = label;
            alarm.message = message;
            alarm.active = true;
            alarm.setDay(Constants.MON, mMon.isChecked());
            alarm.setDay(Constants.TUES, mTues.isChecked());
            alarm.setDay(Constants.WED, mWed.isChecked());
            alarm.setDay(Constants.THURS, mThurs.isChecked());
            alarm.setDay(Constants.FRI, mFri.isChecked());
            alarm.setDay(Constants.SAT, mSat.isChecked());
            alarm.setDay(Constants.SUN, mSun.isChecked());
            alarm.convertDays();

            appDatabase.AlarmDAO().insertOnlySingleAlarm(alarm);
            Toast.makeText(this, "thêm thành công", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent();
            // to do get id
//            intent.putExtra(Constants.KEY_ALARM_ID , id);
            intent.putExtra(Constants.KEY_TYPE , Constants.ADD_TYPE);
            setResult(Activity.RESULT_OK, intent);

            finish();
        } catch (Exception e) {
            Toast.makeText(this, "Thêm alarm lỗi " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}
