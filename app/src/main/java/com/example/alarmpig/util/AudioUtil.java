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
    private static int count;
    private static boolean endRepeat;

    public static void play(Context context, boolean isVibrator , final OnMediaPlayListener listener){
        if (mp == null){
            mp = new MediaPlayer();
        }
        if (mp.isPlaying()){
            return;
        }
        mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            mp.setDataSource( context , Uri.parse("android.resource://com.example.alarmpig/" + R.raw.notification));
            mp.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mp.start();
        final AudioManager mAudioManager = (AudioManager) context.getSystemService(AUDIO_SERVICE);
        final int originalVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0);
        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener()
        {
            @Override
            public void onCompletion(MediaPlayer mp)
            {
                mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, originalVolume, 0);
                if (listener != null){
                    listener.onRepeatSuccess(count);
                    if(endRepeat){
                        listener.onCompleted();
                    }
                }

            }
        });
        mp.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                if (listener != null){
                    listener.onPrepared();
                }
            }
        });
        mp.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
                if (listener != null){
                    listener.onError();
                }
                return false;
            }
        });
        if (isVibrator){
            Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
            vibrator.vibrate(3000);
        }
    }

    public static void repeat(int counter){
        count++;
        if (mp != null && count < counter){
            LogUtils.i("Repeat media alarm " + count);
            endRepeat = false;
            mp.seekTo(0);
            mp.start();
        }else {
            LogUtils.i("end Repeat media alarm " + count);
            endRepeat = true;
        }
    }

    public static void stop(){
        if (mp != null &&  mp.isPlaying()){
            LogUtils.i("stop media alarm " + count);
            count = 0;
            mp.stop();
            mp.reset();
        }
    }

    public static void maxVolume(Context context) {
        LogUtils.i("maxVolume media alarm " + count);
        final AudioManager mAudioManager = (AudioManager) context.getSystemService(AUDIO_SERVICE);
        final int originalVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0);
    }

    public interface OnMediaPlayListener{
        void onPrepared();
        void onError();
        void onRepeatSuccess(int loopCount);
        void onCompleted();
    }
}
