package com.threeguys.scrummy;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class LoadLocal implements Load {

    @Override
    public List<Session> load(Context context) {
        List<Session> sessions = new ArrayList<>();

        Gson gson = new Gson();
        SharedPreferences sp = context.getSharedPreferences(MainActivity.SAVE_PREF, Context.MODE_PRIVATE);
        String saves = sp.toString();

        sessions.add(gson.fromJson(saves, Session.class)); // TODO have a class to hold a list of sessions

        return sessions;
    }
}
