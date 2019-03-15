package com.threeguys.scrummy;

import android.app.Activity;
import android.content.Context;

import java.lang.ref.WeakReference;
import java.util.List;

public abstract class Save {

    private Context saveContext;

    public Context getSaveContext() {
        return saveContext;
    }

    public void setSaveContext(Context saveContext) {
        this.saveContext = saveContext;
    }

    public abstract void save(Session session);

    public abstract void update(List<Session> list);
}
