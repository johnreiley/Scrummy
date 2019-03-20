package com.threeguys.scrummy;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;

import java.util.List;

import static android.content.Context.MODE_PRIVATE;
import static com.threeguys.scrummy.MainActivity.SAVE_PREF;
import static com.threeguys.scrummy.MainActivity.SESSION_LIST_KEY;

public class SaveLocal extends Save {

    static final String SAVELOCAL_TAG = SaveLocal.class.getSimpleName();

    public SaveLocal(Context context) {
        setSaveContext(context);
    }

    @Override
    public void save(Session session) {

        Gson gson = new Gson();
        Load load = new LoadLocal();
        SessionList sessionList = load.load(getSaveContext());
        sessionList.addSession(session);
        String sessionListJson = gson.toJson(sessionList);

        SharedPreferences sp = getSaveContext().getSharedPreferences(SAVE_PREF, MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(SESSION_LIST_KEY, sessionListJson);
        editor.apply();
    }

    @Override
    public void update(List<Session> list) {

        Gson gson = new Gson();
        SessionList sessionList = new SessionList();
        sessionList.setList(list);
        String sessionListJson = gson.toJson(sessionList);

        Log.d(SAVELOCAL_TAG, "SaveContext is an instance of SprintActivity");
        SharedPreferences sp = getSaveContext().getSharedPreferences(SAVE_PREF, MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(SESSION_LIST_KEY, sessionListJson);
        editor.apply();
    }
}
