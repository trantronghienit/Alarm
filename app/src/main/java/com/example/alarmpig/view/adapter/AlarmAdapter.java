package com.example.alarmpig.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.alarmpig.R;
import com.example.alarmpig.model.AlarmModel;
import com.example.alarmpig.util.AlarmUtils;

import java.util.List;

public class AlarmAdapter extends RecyclerView.Adapter<AlarmAdapter.ViewHolder> {

    private List<AlarmModel> mAlarms;

    private String[] mDays;
    private OnItemClickListener mOnItemClickListener;
    private int mAccentColor = -1;

    public AlarmAdapter(OnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final Context c = parent.getContext();
        final View v = LayoutInflater.from(c).inflate(R.layout.alarm_row, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        final Context c = holder.itemView.getContext();

        if (mAccentColor == -1) {
            mAccentColor = ContextCompat.getColor(c, R.color.accent);
        }

//        if(mDays == null){
//            mDays = c.getResources().getStringArray(R.array.days_abbreviated);
//        }

        final AlarmModel alarm = mAlarms.get(position);

        holder.time.setText(AlarmUtils.getReadableTime(alarm.time));
        holder.amPm.setText(AlarmUtils.getAmPm(alarm.time));
        holder.label.setText(alarm.label);
//        holder.days.setText(buildSelectedDays(alarm));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mOnItemClickListener.onItemClick(alarm);
            }
        });

    }

    @Override
    public int getItemCount() {
        return (mAlarms == null) ? 0 : mAlarms.size();
    }

    public void setAlarms(List<AlarmModel> alarms) {
        mAlarms = alarms;
        notifyDataSetChanged();
    }

    static final class ViewHolder extends RecyclerView.ViewHolder {

        TextView time, amPm, label, days;

        ViewHolder(View itemView) {
            super(itemView);

            time = itemView.findViewById(R.id.ar_time);
            amPm = itemView.findViewById(R.id.ar_am_pm);
            label = itemView.findViewById(R.id.ar_label);
            days = itemView.findViewById(R.id.ar_days);

        }
    }

    public interface OnItemClickListener {
        void onItemClick(AlarmModel model);
    }
}
