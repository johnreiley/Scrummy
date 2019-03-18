package com.threeguys.scrummy;

import android.app.Activity;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.CountDownTimer;
import android.provider.Settings;

import java.lang.ref.WeakReference;

public class Clock implements Runnable{

    private boolean isMute;
    private WeakReference<Activity> activity;
    private CountDownTimer counter;

    private MediaPlayer mp;

    public Clock(WeakReference<Activity> a) {
        activity = a;
        isMute = false;
        Context context = a.get().getApplicationContext();
        mp = MediaPlayer.create(context, Settings.System.DEFAULT_ALARM_ALERT_URI);


        //300,000 milliseconds = 5 minutes
        counter = new CountDownTimer(300000,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                //Update the TextView here
            }

            @Override
            public void onFinish() {
                finish();
            }
        };
    }

    @Override
    public void run() {
        counter.start();
    }

    public void play() {

    }

    public void pause() {

    }

    public void toggleMute() {
        if(isMute == false) {
            isMute = true;
            mp.stop();
        } else {
            isMute = false;
        }
    }

    public void reset() {

    }

    public void finish() {
        if (isMute == false) {
            mp.setLooping(true);
            mp.start();
        }
    }
}
