package com.threeguys.scrummy;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import static android.content.Context.MODE_PRIVATE;
import static com.threeguys.scrummy.MainActivity.SAVE_PREF;
import static com.threeguys.scrummy.MainActivity.SESSION_LIST_KEY;

public class SaveLocal extends Save {

    public SaveLocal(Session session, Context context) {
        setSaveSession(session);
        setSaveContext(context);
    }

    @Override
    public void save() {

        Gson gson = new Gson();
        Load load = new LoadLocal();
        SessionList sessionList = load.load(getSaveContext());
        sessionList.addSession(getSaveSession());
        String sessionListJson = gson.toJson(sessionList);
        if (getSaveContext() instanceof SprintActivity) {
            SharedPreferences sp = getSaveContext().
                    getSharedPreferences(SAVE_PREF, MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.putString(SESSION_LIST_KEY, sessionListJson);
            editor.apply();
        }
    }
}
