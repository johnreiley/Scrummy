package com.threeguys.scrummy;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static com.threeguys.scrummy.MainActivity.SESSION_KEY;
import static com.threeguys.scrummy.MainActivity.USERNAME;

public class LoadActivity extends AppCompatActivity {

    public static final String LOAD_ACTIVITY_TAG = LoadActivity.class.getSimpleName();

    List<Session> sessions;
    private RecyclerView recyclerView;
    private LoadSessionItemAdapter adapter;
    private String userSessions;
    private SessionList sessionsList;
    private ToggleButton loadCloud, loadLocal;
    private WeakReference<LoadActivity> reference;
    private Load loader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load);
        Log.i(LOAD_ACTIVITY_TAG, "LoadActivity Started");

        reference = new WeakReference<>(this);

        loadCloud = findViewById(R.id._loadCloudToggleButton);
        loadLocal = findViewById(R.id._loadLocalToggleButton);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        int loadMethod = Integer.valueOf(preferences.getString("load_method", "0"));

        if (loadMethod == 0) {
            loadCloud.setChecked(true);
            loadLocal.setChecked(false);
            loadCloudMethod(null);
        } else {
            loadCloud.setChecked(false);
            loadLocal.setChecked(true);
            loadLocalMethod(null);
        }

        //Load loader = new LoadCloud(reference,"Username");
        //loader.load(this);
//        sessions = loader.load(getApplicationContext()).getList();

//        Log.d(LOAD_ACTIVITY_TAG, "sessions.size() == " + sessions.size());
//
//        orderSessionsByDate();
//        Log.i(LOAD_ACTIVITY_TAG, "LoadActivity Data Loaded and ordered");

//        refreshAdapter();
    }

    public void orderSessionsByDate() {
            Collections.sort(sessions, new Comparator<Session>() {
                @Override
                public int compare(Session o1, Session o2) {
                    return o2.getDate().compareTo(o1.getDate());
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

    public void refreshAdapter() {
        if(adapter == null) {
            adapter = new LoadSessionItemAdapter(this, sessions);
            recyclerView = findViewById(R.id._loadSessionRecyclerView);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setAdapter(adapter);
        }
        else
        {
            adapter.setSessions(sessions);
            adapter.notifyDataSetChanged();
        }
    }

    public void onClickDelete(int position) {
        sessions.remove(position);
        if(loadCloud.isChecked()) {
            Save save = new SaveCloud();
            save.update(sessions);
        } else if (loadLocal.isChecked()) {
            Save save = new SaveLocal(getApplicationContext());
            save.update(sessions);
        }
        refreshAdapter();
    }

    public void onClickRename(final int position) {
        View v = (LayoutInflater.from(LoadActivity.this)).inflate(R.layout.title_dialog, null);

        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(LoadActivity.this);
        alertBuilder.setView(v);

        TextView dialogText1 = v.findViewById(R.id._dialogTextView1);
        TextView dialogText2 = v.findViewById(R.id._dialogTextView2);
        TextView dialogText3 = v.findViewById(R.id._dialogTextView3);
        final EditText dialogTitle = v.findViewById(R.id._dialogTitleEditText);

        alertBuilder.setCancelable(true).setPositiveButton("Rename", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Session session = sessions.get(position);
                session.setTitle(dialogTitle.getText().toString());
                sessions.set(position, session);
                if(loadCloud.isChecked()) {
                    Save save = new SaveCloud();
                    save.update(sessions);
                } else if (loadLocal.isChecked()) {
                    Save save = new SaveLocal(getApplicationContext());
                    save.update(sessions);
                }
                refreshAdapter();
            }
        });

        dialogText1.setEnabled(true);
        dialogText2.setEnabled(true);
        dialogText3.setEnabled(true);
        dialogTitle.setEnabled(true);

        dialogText1.setText(R.string.dialog_rename);
        dialogText2.setVisibility(View.GONE);
        dialogText3.setVisibility(View.GONE);
        dialogTitle.setText(sessions.get(position).getTitle());

        Dialog titleDialog = alertBuilder.create();
        titleDialog.show();
    }

    public void loadCloudMethod(View v) {
        if (loadLocal.isChecked()) {
            loadLocal.setChecked(false);
        }

        if (loadCloud.isChecked()) {
            findViewById(R.id._loadProgress).setVisibility(View.VISIBLE);
            loader = new LoadCloud(reference, "Username");
            loader.load(this);
        } else {
            sessions = new ArrayList<>();
            refreshAdapter();
        }
    }

    public void loadLocalMethod(View v) {
        if (loadCloud.isChecked()) {
            loadCloud.setChecked(false);
        }

        if (loadLocal.isChecked()) {
            loader = new LoadLocal();
            sessionsList = loader.load(this);
            sessions = sessionsList.getList();

            if (sessionsList == null) {
                Toast.makeText(getApplicationContext(),
                        "No local data found.",
                        Toast.LENGTH_SHORT).show();
            } else {
                refreshAdapter();
            }
        } else {
            sessions = new ArrayList<>();
            refreshAdapter();
        }
    }
}
