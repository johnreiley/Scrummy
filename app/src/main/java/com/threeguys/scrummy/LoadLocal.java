package com.threeguys.scrummy;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class LoadLocal implements Load {

    @Override
    public SessionList load(Context context) {
        SessionList sessions;

        Gson gson = new Gson();
        SharedPreferences sp = context.getSharedPreferences(MainActivity.SAVE_PREF, Context.MODE_PRIVATE);
        String saves = sp.toString();

        sessions = gson.fromJson(saves, SessionList.class);

        Log.i("LoadLocal", "Loaded Sessions");
        
        return sessions;
    }
}
