package com.example.alarmpig.util;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.TimePicker;

public final class UtilHelper {

    public static <T extends Activity> void switchActivity(T context, Class clazz) {
        if (context != null) {
            Intent intentToLaunch = new Intent(context, clazz);
            context.startActivity(intentToLaunch);
        }
    }

    public static <T extends Activity> void switchActivity(T context, Class clazz, Bundle data) {
        if (context != null) {
            Intent intentToLaunch = new Intent(context, clazz);
            intentToLaunch.putExtras(data);
            context.startActivity(intentToLaunch);
        }
    }

    public static <T extends Activity> void switchActivity(T context, Class clazz, Bundle data, boolean notBack) {
        if (context != null) {
            Intent intentToLaunch = new Intent(context, clazz);
            intentToLaunch.putExtras(data);
            if (notBack) {
                intentToLaunch.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            }
            context.startActivity(intentToLaunch);
            if (notBack) context.finish();
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    public static int getTimePickerMinute(TimePicker picker) {
        return (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                ? picker.getMinute()
                : picker.getCurrentMinute();
    }

    @TargetApi(Build.VERSION_CODES.M)
    public static int getTimePickerHour(TimePicker picker) {
        return (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                ? picker.getHour()
                : picker.getCurrentHour();
    }

}
