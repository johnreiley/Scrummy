package com.threeguys.scrummy;

import android.app.Activity;
import android.content.Context;

import java.lang.ref.WeakReference;

public abstract class Save {

    private Session saveSession;

    private Context saveContext;

    public void setSaveSession(Session saveSession) {
        this.saveSession = saveSession;
    }

    public Session getSaveSession() {
        return saveSession;
    }

    public Context getSaveContext() {
        return saveContext;
    }

    public void setSaveContext(Context saveContext) {
        this.saveContext = saveContext;
    }

    public abstract void save();
}
