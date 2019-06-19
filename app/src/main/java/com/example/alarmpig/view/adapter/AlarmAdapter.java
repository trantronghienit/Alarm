package com.example.alarmpig.view.adapter;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.alarmpig.R;
import com.example.alarmpig.model.AlarmModel;
import com.example.alarmpig.util.AlarmUtils;
import com.example.alarmpig.util.Constants;
import com.example.alarmpig.util.UtilHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AlarmAdapter extends RecyclerView.Adapter<AlarmAdapter.ViewHolder> {

    private List<AlarmModel> mAlarms;

    private String[] mDays;
    private OnItemClickListener mOnItemClickListener;
    private OnItemCheckActiveListener mOnItemCheckActiveListener;
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
    public void onBindViewHolder(ViewHolder holder, final int position) {

        final Context c = holder.itemView.getContext();

        if (mAccentColor == -1) {
            mAccentColor = ContextCompat.getColor(c, R.color.accent);
        }

        if(mDays == null){
            mDays = UtilHelper.getStringArray(R.array.days_abbreviated);
        }

        final AlarmModel alarm = mAlarms.get(position);

        holder.time.setText(AlarmUtils.formatAlarm(alarm.hour , alarm.minute));
        holder.label.setText(alarm.label);
        holder.days.setText(buildSelectedDays(alarm));
        if (alarm.type == Constants.TYPE_ALARM){
            if (alarm.active){
                holder.active.setImageResource(R.drawable.ic_alarm_active);
            }else {
                holder.active.setImageResource(R.drawable.ic_alarm_unactive);
            }
        }else {
            if (alarm.active){
                holder.active.setImageResource(R.drawable.ic_notification_active);
            }else {
                holder.active.setImageResource(R.drawable.ic_notification_unactive);
            }
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mOnItemClickListener.onItemClick(alarm);
            }
        });

        holder.active.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mOnItemCheckActiveListener != null){
                    alarm.active = !alarm.active;
                    notifyItemChanged(position);
                    mOnItemCheckActiveListener.onItemCheckActive(alarm);
                }
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
        ImageView active;

        ViewHolder(View itemView) {
            super(itemView);

            time = itemView.findViewById(R.id.ar_time);
            amPm = itemView.findViewById(R.id.ar_am_pm);
            label = itemView.findViewById(R.id.ar_label);
            days = itemView.findViewById(R.id.ar_days);
            active = itemView.findViewById(R.id.ar_icon);

        }
    }
    private Spannable buildSelectedDays(AlarmModel alarm) {

        final int numDays = 7;
        final HashMap<Integer , Boolean> days = alarm.getMapFormStringDay();

        ArrayList<Integer> keyList = new ArrayList<Integer>(days.keySet());
        ArrayList<Boolean> valueList = new ArrayList<Boolean>(days.values());

        final SpannableStringBuilder builder = new SpannableStringBuilder();
        for (int i = 0; i < numDays; i++) {
            if (valueList.get(i)){ // is check
                builder.append(AlarmUtils.getDayFormIdDay(keyList.get(i)));
                builder.append(" ");
            }
        }
        return builder;

    }

    public void setOnItemCheckActiveListener(OnItemCheckActiveListener mOnItemCheckActiveListener) {
        this.mOnItemCheckActiveListener = mOnItemCheckActiveListener;
    }

    public interface OnItemClickListener {
        void onItemClick(AlarmModel model);
    }

    public interface OnItemCheckActiveListener {
        void onItemCheckActive(AlarmModel model);
    }
}
