package com.threeguys.scrummy;

import android.app.Activity;

import java.lang.ref.WeakReference;

public abstract class Save {

    private Session saveSession;
    private WeakReference<Activity> main;

    public abstract void save();
}
