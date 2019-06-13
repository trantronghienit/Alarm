package com.example.alarmpig.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.SparseBooleanArray;

import androidx.annotation.IntDef;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Entity()
public class AlarmModel implements Parcelable {

    @PrimaryKey(autoGenerate = true)
    public long alarmId;
    public int hour;
    public int minute;
    public int second;
    @Ignore
    public SparseBooleanArray days;
    public boolean active;
    public String label;
    public String message;
    public long time;

    public AlarmModel(){
        days = buildBaseDaysArray();
    }

    protected AlarmModel(Parcel in) {
        alarmId = in.readLong();
        hour = in.readInt();
        minute = in.readInt();
        second = in.readInt();
        days = in.readSparseBooleanArray();
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
        dest.writeSparseBooleanArray(days);
        dest.writeByte((byte) (active ? 1 : 0));
        dest.writeString(label);
        dest.writeString(message);
        dest.writeLong(time);
    }

    public void setDay(@Days int dayInput, boolean isAlarmed) {
        days.append(dayInput, isAlarmed);
    }

    public boolean getDay(@Days int day) {
        return days.get(day);
    }

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({MON,TUES,WED,THURS,FRI,SAT,SUN})
    @interface Days{}
    public static final int MON = 1;
    public static final int TUES = 2;
    public static final int WED = 3;
    public static final int THURS = 4;
    public static final int FRI = 5;
    public static final int SAT = 6;
    public static final int SUN = 7;

    private static SparseBooleanArray buildDaysArray(@Days int... days) {

        final SparseBooleanArray array = buildBaseDaysArray();

        for (@Days int day : days) {
            array.append(day, true);
        }

        return array;

    }

    private static SparseBooleanArray buildBaseDaysArray() {

        final int numDays = 7;

        final SparseBooleanArray array = new SparseBooleanArray(numDays);

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
