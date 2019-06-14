package com.example.alarmpig.callback;

import com.example.alarmpig.model.ConfigAlarm;

public interface OnGetConfigInfoListener {

    void onSuccess(ConfigAlarm config);
    void onFailed(String message);
}
