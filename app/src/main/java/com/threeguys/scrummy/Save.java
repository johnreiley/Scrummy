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

    /**
     * Stores the session into the child class' database (i.e. SharedPreferences, FireBase).
     * @param session A completed session to be stored
     */
    public abstract void save(Session session);

    /**
     * Replaces the current list of Sessions with the specified list.
     * @param list A user handled list to replace the current list
     */
    public abstract void update(List<Session> list);
}
