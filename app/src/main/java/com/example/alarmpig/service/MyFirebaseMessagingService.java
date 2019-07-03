package com.example.alarmpig.service;

import android.content.Intent;
import android.widget.Toast;

import com.example.alarmpig.util.Constants;
import com.example.alarmpig.util.LogUtils;
import com.example.alarmpig.util.SharedPrefs;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        LogUtils.s("From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            LogUtils.s("Message data payload: " + remoteMessage.getData());
//            Toast.makeText(this, "" + remoteMessage.getData(), Toast.LENGTH_SHORT).show();
            if (/* Check if data needs to be processed by long running job */ true) {
                // For long-running tasks (10 seconds or more) use Firebase Job Dispatcher.
//                scheduleJob();
            } else {
                // Handle message within 10 seconds
//                handleNow();
            }
        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            LogUtils.s("Message Notification Body: " + remoteMessage.getNotification().getBody());
            Intent intentToService = new Intent(this, AlarmService.class);
            stopService(intentToService);
        }

    }

//    @Override
//    public void onNewToken(String s) {
//        super.onNewToken(s);
//        // save token to local and push to firebase SplashActivity -> pushTokenFMCToFirebase
//        SharedPrefs.getInstance().put(Constants.TOKEN , s);
//        LogUtils.k("save new token: " + s);
//    }
}
