package com.example.alarmpig.view.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
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

import static com.example.alarmpig.util.Constants.PARCELABLE_DATA;

public class EditAlarmActivity extends BaseActivity {

    private AlarmModel mAlarm;
    private TimePicker mTimePicker;
    private EditText mLabel;
    private CheckBox mMon, mTues, mWed, mThurs, mFri, mSat, mSun;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_alarm);
        if (getIntent().getExtras() != null) {
            mAlarm = getIntent().getExtras().getParcelable(PARCELABLE_DATA);
        }

        mTimePicker = findViewById(R.id.edit_alarm_time_picker);

        mLabel = findViewById(R.id.edit_alarm_label);

        mMon = findViewById(R.id.edit_alarm_mon);
        mTues = findViewById(R.id.edit_alarm_tues);
        mWed = findViewById(R.id.edit_alarm_wed);
        mThurs = findViewById(R.id.edit_alarm_thurs);
        mFri = findViewById(R.id.edit_alarm_fri);
        mSat = findViewById(R.id.edit_alarm_sat);
        mSun = findViewById(R.id.edit_alarm_sun);

        updateData();
    }

    private void updateData() {
        ViewUtils.setTimePickerTime(mTimePicker, mAlarm.time);
        mLabel.setText(mAlarm.label);
        setDayCheckboxes(mAlarm);
    }

    private void setDayCheckboxes(AlarmModel mAlarm) {
        mMon.setChecked(mAlarm.getDay(Constants.MON));
        mTues.setChecked(mAlarm.getDay(Constants.TUES));
        mWed.setChecked(mAlarm.getDay(Constants.WED));
        mThurs.setChecked(mAlarm.getDay(Constants.THURS));
        mFri.setChecked(mAlarm.getDay(Constants.FRI));
        mSat.setChecked(mAlarm.getDay(Constants.SAT));
        mSun.setChecked(mAlarm.getDay(Constants.SUN));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_alarm_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_save:
                save();
                break;
            case R.id.action_delete:
                delete();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void save() {
        try {
            String label = mLabel.getText().toString();
            String message = mLabel.getText().toString();

            final Calendar time = Calendar.getInstance();
            time.set(Calendar.MINUTE, ViewUtils.getTimePickerMinute(mTimePicker));
            time.set(Calendar.HOUR_OF_DAY, ViewUtils.getTimePickerHour(mTimePicker));
            mAlarm.time = time.getTimeInMillis();
            mAlarm.minute = ViewUtils.getTimePickerMinute(mTimePicker);
            mAlarm.second = 0;
            mAlarm.hour = ViewUtils.getTimePickerHour(mTimePicker);
            mAlarm.label = label;
            mAlarm.message = message;
            mAlarm.active = true;
            mAlarm.setDay(Constants.MON, mMon.isChecked());
            mAlarm.setDay(Constants.TUES, mTues.isChecked());
            mAlarm.setDay(Constants.WED, mWed.isChecked());
            mAlarm.setDay(Constants.THURS, mThurs.isChecked());
            mAlarm.setDay(Constants.FRI, mFri.isChecked());
            mAlarm.setDay(Constants.SAT, mSat.isChecked());
            mAlarm.setDay(Constants.SUN, mSun.isChecked());
            mAlarm.convertDays();
            appDatabase.AlarmDAO().updateAlarm(mAlarm);
            Toast.makeText(this, "sửa thành công", Toast.LENGTH_SHORT).show();
            finish();
        } catch (Exception e) {
            Toast.makeText(this, "sửa alarm lỗi " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void delete() {
        final AlertDialog.Builder builder =
                new AlertDialog.Builder(this, R.style.DeleteAlarmDialogTheme);
        builder.setTitle(R.string.delete_dialog_title);
        builder.setMessage(R.string.delete_dialog_content);
        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (mAlarm != null) {
                    appDatabase.AlarmDAO().deleteAlarm(mAlarm);
                    Toast.makeText(EditAlarmActivity.this, "Xóa báo thức thành công", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(EditAlarmActivity.this, "Xóa báo thức không thành công", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });
        builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();

    }
}
