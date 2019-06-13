package com.example.alarmpig.view.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.alarmpig.R;
import com.example.alarmpig.model.AlarmModel;
import com.example.alarmpig.util.UtilHelper;
import com.example.alarmpig.view.adapter.AlarmAdapter;
import com.example.alarmpig.view.adapter.DividerItemDecoration;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import static com.example.alarmpig.util.Constans.PARCELABLE_DATA;

public class DashboardAlarmActivity extends BaseActivity implements AlarmAdapter.OnItemClickListener {

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
        alarmAdapter.setAlarms(mList);
        mRcAlarm.setAdapter(alarmAdapter);
        mRcAlarm.addItemDecoration(new DividerItemDecoration(this));
        mRcAlarm.setLayoutManager(new LinearLayoutManager(this));
        mRcAlarm.setItemAnimator(new DefaultItemAnimator());

        mAddAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UtilHelper.switchActivity(DashboardAlarmActivity.this , AddAlarmActivity.class);
            }
        });
    }

    @Override
    public void onItemClick(AlarmModel model) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(PARCELABLE_DATA , model);
        UtilHelper.switchActivity(DashboardAlarmActivity.this , EditAlarmActivity.class ,bundle );
    }
}
