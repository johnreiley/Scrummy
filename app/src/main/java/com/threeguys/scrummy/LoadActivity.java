package com.threeguys.scrummy;

import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static com.threeguys.scrummy.MainActivity.SESSION_KEY;

public class LoadActivity extends AppCompatActivity {

    public static final String LOADACTIVITY_TAG = LoadActivity.class.getSimpleName();

    List<Session> sessions;
    private RecyclerView recyclerView;
    private LoadSessionItemAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load);
        Log.i(LOADACTIVITY_TAG, "LoadActivity Started");

        Load loader = new LoadLocal();
        sessions = loader.load(getApplicationContext()).getList();
        orderSessionsByDate();
        Log.i(LOADACTIVITY_TAG, "LoadActivity Data Loaded and ordered");

        adapter = new LoadSessionItemAdapter(this, sessions);

        recyclerView = findViewById(R.id._loadSessionRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    private void orderSessionsByDate() {
            Collections.sort(sessions, new Comparator<Session>() {
                @Override
                public int compare(Session o1, Session o2) {
                    return o1.getDate().compareTo(o2.getDate());
                }
            });
    }

    public void onClickSession(int index) {
        Log.i("onClickSession", "Session clicked: " + sessions.get(index).getDate());

        Gson gson = new Gson();
        String sessionJson = gson.toJson(sessions.get(index), Session.class);

        Intent viewIntent = new Intent(this, ViewSessionActivity.class);
        viewIntent.putExtra(SESSION_KEY, sessionJson);
        startActivity(viewIntent);
    }

    public void onClickPopup(View v) {
        PopupMenu popup = new PopupMenu(this, v);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.load_item_popup, popup.getMenu());
        popup.show();
    }
}
