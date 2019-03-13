package com.threeguys.scrummy;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import static com.threeguys.scrummy.MainActivity.SAVE_PREF;

public class SaveLocal extends Save {

    public SaveLocal(Session session, Context context) {
        setSaveSession(session);
        setSaveContext(context);
    }

    @Override
    public void save() {

        Gson gson = new Gson();
        String sessionJson = gson.toJson(getSaveSession());
        if (getSaveContext() instanceof SprintActivity) {
            SharedPreferences sp = getSaveContext().
                    getSharedPreferences(SAVE_PREF, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.putString(getSaveSession().getDate(), sessionJson);
            editor.apply();
        }
    }
}
