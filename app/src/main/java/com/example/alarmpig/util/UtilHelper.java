package com.example.alarmpig.util;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

import com.example.alarmpig.App;
import com.example.alarmpig.model.FirebaseInfoDevice;
import com.example.alarmpig.service.AlarmService;
import com.example.alarmpig.view.activity.MainActivity;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;

import java.security.InvalidParameterException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

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
    public static Gson getGson() {
        if (gson == null) {
            gson = new Gson();
        }
        return gson;
    }

    public static String[] getStringArray(int res){
        return App.getContext().getResources().getStringArray(res);
    }

    public static void saveInFirebase(String... values){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(Constants.ALARM_FIREBASE_REF);
        for (int i = 0; i < values.length; i++) {
            myRef.setValue(values[i]);
        }
    }

    public static void saveInfoAppInFirebase(FirebaseInfoDevice infoDevice,DatabaseReference.CompletionListener completionListener ){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(Constants.ALARM_FIREBASE_REF);
        myRef.setValue(infoDevice, completionListener);
    }

    public static String removeSpecial(String input){
        return input.replaceAll("[^A-Za-z0-9]" , "");
    }


    /**=========   SECURITY ===========================
     */
    public static SecretKey generateAESKey(int keysize)
            throws InvalidParameterException {
        try {
            if (Cipher.getMaxAllowedKeyLength(Constants.ALGORITHM) < keysize) {
                // this may be an issue if unlimited crypto is not installed
                throw new InvalidParameterException("Key size of " + keysize
                        + " not supported in this runtime");
            }

            final KeyGenerator keyGen = KeyGenerator.getInstance(Constants.ALGORITHM);
            keyGen.init(keysize);
            return keyGen.generateKey();
        } catch (final NoSuchAlgorithmException e) {
            // AES functionality is a requirement for any Java SE runtime
            throw new IllegalStateException(
                    "AES should always be present in a Java SE runtime", e);
        }
    }

    public static String generateStringAESKey(int keysize){
        return encodeAESKeyToBase64(generateAESKey(keysize));
    }

    public static SecretKey decodeBase64ToAESKey(final String encodedKey)
            throws IllegalArgumentException {
        try {
            // throws IllegalArgumentException - if src is not in valid Base64
            // scheme
            final byte[] keyData;
            final int keysize;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                keyData = Base64.getDecoder().decode(encodedKey);
                keysize = keyData.length * Byte.SIZE;
            }else {
                keyData = android.util.Base64.decode(encodedKey, android.util.Base64.DEFAULT);
                keysize = keyData.length * Byte.SIZE;
            }


            // this should be checked by a SecretKeyFactory, but that doesn't exist for AES
//            switch (keysize) {
//                case 128:
//                case 192:
//                case 256:
//                case 512:
//                    break;
//                default:
//                    throw new IllegalArgumentException("Invalid key size for AES: " + keysize);
//            }

            if (Cipher.getMaxAllowedKeyLength(Constants.ALGORITHM) < keysize) {
                // this may be an issue if unlimited crypto is not installed
                throw new IllegalArgumentException("Key size of " + keysize
                        + " not supported in this runtime");
            }

            // throws IllegalArgumentException - if key is empty
            final SecretKeySpec aesKey = new SecretKeySpec(keyData, Constants.ALGORITHM);
            return aesKey;
        } catch (final NoSuchAlgorithmException e) {
            // AES functionality is a requirement for any Java SE runtime
            throw new IllegalStateException(
                    "AES should always be present in a Java SE runtime", e);
        }
    }

    public static String encodeAESKeyToBase64(final SecretKey aesKey)
            throws IllegalArgumentException {
        if (!aesKey.getAlgorithm().equalsIgnoreCase(Constants.ALGORITHM)) {
            throw new IllegalArgumentException("Not an AES key");
        }

        final byte[] keyData = aesKey.getEncoded();
        final String encodedKey;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            encodedKey = Base64.getEncoder().encodeToString(keyData);
        }else {
            encodedKey = android.util.Base64.encodeToString(keyData, android.util.Base64.DEFAULT);
        }
        return encodedKey;
    }

    public static void stopSevicesAlarm(Context context) {
        Intent intentToService = new Intent(context, AlarmService.class);
        context.stopService(intentToService);
    }
}
