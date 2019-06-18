package com.example.alarmpig.util;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.TimePicker;

import androidx.annotation.StringRes;

import com.example.alarmpig.App;
import com.google.gson.Gson;

public final class UtilHelper {
    private static Gson gson;

    public static String getStringRes(@StringRes int resString) {
        try {
            return App.getContext().getResources().getString(resString);
        } catch (Exception e) {
            return "";
        }
    }

    public static String getStringRes(@StringRes int resString, Object... formatArr) {
        try {
            return App.getContext().getResources().getString(resString, formatArr);
        } catch (Exception e) {
            return "";
        }
    }

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

    public static <T extends Activity> void switchActivity(T context, Class clazz, int requestCode) {
        if (context != null) {
            Intent intentToLaunch = new Intent(context, clazz);
            context.startActivityForResult(intentToLaunch , requestCode);
        }
    }

    public static <T extends Activity> void switchActivity(T context, Class clazz, Bundle data, int requestCode) {
        if (context != null) {
            Intent intentToLaunch = new Intent(context, clazz);
            intentToLaunch.putExtras(data);
            context.startActivityForResult(intentToLaunch ,requestCode);
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


    public static Gson getGson() {
        if (gson == null) {
            gson = new Gson();
        }
        return gson;
    }

    public static String[] getStringArray(int res){
        return App.getContext().getResources().getStringArray(res);
    }

}
