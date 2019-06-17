package com.example.alarmpig.util;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.ActivityCompat;

import com.example.alarmpig.model.AlarmModel;
import com.example.alarmpig.model.Days;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import static com.example.alarmpig.util.Constants.KEY_FRI;
import static com.example.alarmpig.util.Constants.KEY_MON;
import static com.example.alarmpig.util.Constants.KEY_SAT;
import static com.example.alarmpig.util.Constants.KEY_SUN;
import static com.example.alarmpig.util.Constants.KEY_THURS;
import static com.example.alarmpig.util.Constants.KEY_TUES;

public final class AlarmUtils {

    private static final SimpleDateFormat TIME_FORMAT =
            new SimpleDateFormat("h:mm", Locale.getDefault());
    private static final SimpleDateFormat AM_PM_FORMAT =
            new SimpleDateFormat("a", Locale.getDefault());

    private static final int REQUEST_ALARM = 1;
    private static final String[] PERMISSIONS_ALARM = {
            Manifest.permission.VIBRATE
    };

    private AlarmUtils() {
        throw new AssertionError();
    }

    public static void checkAlarmPermissions(Activity activity) {

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return;
        }

        final int permission = ActivityCompat.checkSelfPermission(
                activity, Manifest.permission.VIBRATE
        );

        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_ALARM,
                    REQUEST_ALARM
            );
        }

    }

    public static String getReadableTime(long time) {
        return TIME_FORMAT.format(time);
    }

    public static String getAmPm(long time) {
        return AM_PM_FORMAT.format(time);
    }

    // default time 7
    public static int getTimeFormString(String timeString, int index) {
        String result = timeString.split(":")[index];
        if (result.isEmpty()) {
            return Constants.TIME_DEFAULT;
        }
        return Integer.parseInt(result);
    }


    public static void getDayOfWeek(HashMap<Integer, Boolean> days, List<String> daysOfWeek) {
        if (days != null && daysOfWeek != null) {
            for (String day : daysOfWeek) {
                int dayActive = getKeyFormStringDay(day);
                if (dayActive != -1) {
                    days.put(dayActive, true);
                }
            }
        }
    }

    public static int getKeyFormStringDay(String keyDay) {
        switch (keyDay) {
            case KEY_MON:
                return Constants.MON;
            case KEY_TUES:
                return Constants.TUES;
            case Constants.KEY_WED:
                return Constants.WED;
            case KEY_THURS:
                return Constants.THURS;
            case KEY_FRI:
                return Constants.FRI;
            case KEY_SAT:
                return Constants.SAT;
            case KEY_SUN:
                return Constants.SUN;
            default:
                return -1;
        }
    }

    public static String getDayFormIdDay(int dayId) {
        switch (dayId) {
            case Constants.MON:
                return Constants.KEY_MON;
            case Constants.TUES:
                return Constants.KEY_TUES;
            case Constants.WED:
                return Constants.KEY_WED;
            case Constants.THURS:
                return Constants.KEY_THURS;
            case Constants.FRI:
                return Constants.KEY_FRI;
            case Constants.SAT:
                return Constants.KEY_SAT;
            case Constants.SUN:
                return Constants.KEY_SUN;
            default:
                return "";
        }
    }
}
