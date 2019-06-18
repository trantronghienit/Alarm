package com.example.alarmpig.view.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.alarmpig.R;
import com.example.alarmpig.model.AlarmModel;
import com.example.alarmpig.util.AlarmController;
import com.example.alarmpig.util.Constants;
import com.example.alarmpig.util.UtilHelper;
import com.example.alarmpig.view.adapter.AlarmAdapter;
import com.example.alarmpig.view.adapter.DividerItemDecoration;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import static com.example.alarmpig.util.Constants.PARCELABLE_DATA;

public class DashboardAlarmActivity extends BaseActivity implements AlarmAdapter.OnItemClickListener, AlarmAdapter.OnItemCheckActiveListener {

    private RecyclerView mRcAlarm;
    private FloatingActionButton mAddAlarm;
    private TextView mTxtEmpty;
    private List<AlarmModel> mList;
    private AlarmAdapter alarmAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard_alarm);
        mList = appDatabase.AlarmDAO().getAllAlarm();

        mRcAlarm = findViewById(R.id.recycler);
        mAddAlarm = findViewById(R.id.add_alarm);
        mTxtEmpty = findViewById(R.id.empty_view);

        mTxtEmpty.setVisibility(View.GONE);

        alarmAdapter = new AlarmAdapter(this);
        alarmAdapter.setOnItemCheckActiveListener(this);
        alarmAdapter.setAlarms(mList);
        mRcAlarm.setAdapter(alarmAdapter);
        mRcAlarm.addItemDecoration(new DividerItemDecoration(this));
        mRcAlarm.setLayoutManager(new LinearLayoutManager(this));
        mRcAlarm.setItemAnimator(new DefaultItemAnimator());

        mAddAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UtilHelper.switchActivity(DashboardAlarmActivity.this, AddAlarmActivity.class ,Constants.ADD_REQUEST_CODE);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mList != null) {
            mList.clear();
            mList.addAll(appDatabase.AlarmDAO().getAllAlarm());
            alarmAdapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == Activity.RESULT_OK && data != null && data.getExtras() != null) {
            int type = data.getExtras().getInt(Constants.KEY_TYPE);
            int idAlarm;
            switch (type) {
                case Constants.DEL_TYPE:
                    idAlarm = data.getIntExtra(Constants.KEY_ALARM_ID, -1);
                    if (idAlarm != -1) {
                        AlarmController.cancelAlarm(DashboardAlarmActivity.this, idAlarm);
                    }
                    break;
                case Constants.EDIT_TYPE:
                case Constants.ADD_TYPE:
                    idAlarm = data.getIntExtra(Constants.KEY_ALARM_ID, -1);
                    AlarmModel model = appDatabase.AlarmDAO().fetchOneAlarmbyAlarmId(idAlarm);
                    if (model != null) {
                        AlarmController.setAlarm(DashboardAlarmActivity.this, model);
                    }
                    break;
                default:
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onItemClick(AlarmModel model) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(PARCELABLE_DATA, model);
        UtilHelper.switchActivity(DashboardAlarmActivity.this, EditAlarmActivity.class, bundle, Constants.EDIT_RESULT_CODE);
    }

    @Override
    public void onItemCheckActive(AlarmModel model) {
        if (model.active) {
            AlarmController.setAlarm(this, model);
        } else {
            AlarmController.cancelAlarm(this, model.alarmId);
        }

    }
}
