package com.example.alarmpig;

import android.app.Application;
import android.content.Context;

import androidx.work.Configuration;
import androidx.work.WorkManager;

public class App extends Application {

    private static Context context;
    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
    }

    public static Context getContext() {
        return context;
    }
}
