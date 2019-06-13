package com.example.alarmpig.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.StringDef;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.example.alarmpig.util.UtilHelper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Type;
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
    public HashMap<String, Boolean> days;
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

    public void setDay(@Days String dayInput, boolean isAlarmed) {
        days.put(dayInput, isAlarmed);
    }

    public boolean getDay(@Days String day) {
        Type type = new TypeToken<Map<String, Boolean>>(){}.getType();
        Map<String, Boolean> data = UtilHelper.getGson().fromJson(this.dayString , type);
        if (data.isEmpty()){
            return false;
        }
        return data.get(day);
    }

    public void convertDays(){
        JSONObject json = new JSONObject(days);
        this.dayString = json.toString();
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
    @StringDef({MON,TUES,WED,THURS,FRI,SAT,SUN})
    @interface Days{}
    public static final String MON = "MON";
    public static final String TUES = "TUES";
    public static final String WED = "WED";
    public static final String THURS = "THURS";
    public static final String FRI = "FRI";
    public static final String SAT = "SAT";
    public static final String SUN = "SUN";

    private static HashMap<String, Boolean> buildBaseDaysArray() {

        final HashMap<String, Boolean> array = new HashMap<>();

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
