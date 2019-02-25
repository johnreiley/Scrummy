package com.threeguys.scrummy;

import android.app.Activity;
import android.os.CountDownTimer;

import java.lang.ref.WeakReference;

public class Clock implements Runnable{

    private boolean isMute;
    private WeakReference<Activity> activity;
    private CountDownTimer counter;

    public Clock(WeakReference<Activity> a) {
        activity = a;
        isMute = false;

        //300,000 milliseconds = 5 minutes
        counter = new CountDownTimer(300000,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                //Update the TextView here
            }

            @Override
            public void onFinish() {
                //Sound alarm here
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

    }

    public void reset() {

    }

    public void finish() {

    }
}
