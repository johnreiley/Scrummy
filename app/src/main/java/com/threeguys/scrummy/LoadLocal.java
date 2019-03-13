package com.threeguys.scrummy;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;

import static android.content.Context.MODE_PRIVATE;
import static com.threeguys.scrummy.MainActivity.SAVE_PREF;
import static com.threeguys.scrummy.MainActivity.SESSION_LIST_KEY;


public class LoadLocal implements Load {

    @Override
    public SessionList load(Context context) {
        SessionList sessions = new SessionList();

        Gson gson = new Gson();
        SharedPreferences sp = context.getSharedPreferences(SAVE_PREF, MODE_PRIVATE);
        String saves = sp.getString(SESSION_LIST_KEY, "no session");

        if (!saves.equals("no session")) {
            sessions = gson.fromJson(saves, SessionList.class);
        }

        Log.i("LoadLocal", "Loaded Sessions");
        
        return sessions;
    }
}