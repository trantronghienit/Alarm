package com.example.alarmpig.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import androidx.annotation.IntDef;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.example.alarmpig.util.UtilHelper;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Type;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

@Entity()
public class AlarmModel implements Parcelable {

    @PrimaryKey(autoGenerate = true)
    public long alarmId;
    public int hour;
    public int minute;
    public int second;
    @Ignore
    public HashMap<Integer, Boolean> days;
    public String dayString;
    public boolean active;
    public String label;
    public String message;
    public long time;

    public AlarmModel(){
        days = buildBaseDaysArray();
        convertDays();
    }

    protected AlarmModel(Parcel in) {
        alarmId = in.readLong();
        hour = in.readInt();
        minute = in.readInt();
        second = in.readInt();
        dayString = in.readString();
        active = in.readByte() != 0;
        label = in.readString();
        message = in.readString();
        time = in.readLong();
    }

    public static final Creator<AlarmModel> CREATOR = new Creator<AlarmModel>() {
        @Override
        public AlarmModel createFromParcel(Parcel in) {
            return new AlarmModel(in);
        }

        @Override
        public AlarmModel[] newArray(int size) {
            return new AlarmModel[size];
        }
    };

    public void setDay(@Days int dayInput, boolean isAlarmed) {
        if (days == null && !TextUtils.isEmpty(dayString)){
            Type type = new TypeToken<Map<String, Boolean>>(){}.getType();
            days = UtilHelper.getGson().fromJson(this.dayString , type);
        }
        days.put(dayInput, isAlarmed);
    }

    public boolean getDay(@Days int day) {
        Type type = new TypeToken<HashMap<Integer, Boolean>>(){}.getType();
        days = UtilHelper.getGson().fromJson(this.dayString , type);
        if (days.isEmpty()){
            return false;
        }
        boolean isKeyPresent = days.containsKey(day);
        if (isKeyPresent){
            return days.get(day);
        }else {
            return false;
        }

    }

    public void convertDays(){
        try {
            Type type = new TypeToken<HashMap<Integer, Boolean>>(){}.getType();
            this.dayString = UtilHelper.getGson().toJson(days , type);
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(alarmId);
        dest.writeInt(hour);
        dest.writeInt(minute);
        dest.writeInt(second);
        dest.writeString(dayString);
        dest.writeByte((byte) (active ? 1 : 0));
        dest.writeString(label);
        dest.writeString(message);
        dest.writeLong(time);
    }

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({MON,TUES,WED,THURS,FRI,SAT,SUN})
    @interface Days{}
    public static final int MON = Calendar.MONDAY;
    public static final int TUES = Calendar.TUESDAY;
    public static final int WED = Calendar.WEDNESDAY;
    public static final int THURS = Calendar.THURSDAY;
    public static final int FRI = Calendar.FRIDAY;
    public static final int SAT = Calendar.SATURDAY;
    public static final int SUN = Calendar.SUNDAY;

    private static HashMap<Integer, Boolean> buildBaseDaysArray() {

        final HashMap<Integer, Boolean> array = new HashMap<>();

        array.put(MON, false);
        array.put(TUES, false);
        array.put(WED, false);
        array.put(THURS, false);
        array.put(FRI, false);
        array.put(SAT, false);
        array.put(SUN, false);

        return array;

    }
}
