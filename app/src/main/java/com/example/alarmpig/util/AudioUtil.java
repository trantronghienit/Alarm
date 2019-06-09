package com.example.alarmpig.util;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Vibrator;

import com.example.alarmpig.R;

import java.io.IOException;

import static android.content.Context.AUDIO_SERVICE;

public final class AudioUtil {
    private static MediaPlayer mp;

    public static void play(Context context,boolean isVibrator){
        if (mp == null){
            mp = new MediaPlayer();
        }
        maxVolume(context);
        mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            mp.setDataSource( context , Uri.parse("android.resource://com.example.alarmpig/" + R.raw.notification));
            mp.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mp.start();
//        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener()
//        {
//            @Override
//            public void onCompletion(MediaPlayer mp)
//            {
//                mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, originalVolume, 0);
//            }
//        });
        if (isVibrator){
            Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
            vibrator.vibrate(3000);
        }
    }

    public static void stop(){
        if (mp != null &&  mp.isPlaying()){
            mp.stop();
            mp.reset();
        }
    }

    public static void maxVolume(Context context) {
        final AudioManager mAudioManager = (AudioManager) context.getSystemService(AUDIO_SERVICE);
        final int originalVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0);
    }
}
