package com.example.alarmpig.util;

import android.util.Log;

public final class LogUtils {

    // info
    public static void i(String message){
        Log.i(Constants.INFO , message);
    }

    // receiver
    public static void r(String message){
        Log.i(Constants.RECEIVER , message);
    }

    // service
    public static void s(String message){
        Log.i(Constants.SERVICE , message);
    }

    // file
    public static void f(String message){
        Log.i(Constants.FILE , message);
    }

    // error
    public static void e(String message){
        Log.i(Constants.ERROR , message);
    }
}
