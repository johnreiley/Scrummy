package com.threeguys.scrummy;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
import com.google.firebase.auth.FirebaseAuth;
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
    private SessionList sessionsList;
    private ToggleButton loadCloud, loadLocal;
    private WeakReference<LoadActivity> reference;
    private Load loader;

    public int getLoadMethod() {
        return loadMethod;
    }

    private int loadMethod;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load);
        Log.i(LOAD_ACTIVITY_TAG, "LoadActivity Started");

        mAuth = FirebaseAuth.getInstance();

        reference = new WeakReference<>(this);

        loadCloud = findViewById(R.id._loadCloudToggleButton);
        loadLocal = findViewById(R.id._loadLocalToggleButton);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        loadMethod = Integer.valueOf(preferences.getString("load_method", "0"));

        if (loadMethod == 0 && isConnected()) {
            loadCloud.setChecked(true);
            loadLocal.setChecked(false);
            loadCloudMethod(null);
        } else {
            loadCloud.setChecked(false);
            loadLocal.setChecked(true);
            loadLocalMethod(null);
        }

        if (!isConnected()) {
            loadCloud.setEnabled(false);
        }
    }

    /**
     * Orders by year, month, day, am/pm, hour, then minutes.
     */
    public void orderSessionsByDate() {
            Collections.sort(sessions, new Comparator<Session>() {
                @Override
                public int compare(Session o1, Session o2) {
                    return o2.getDate().compareTo(o1.getDate());
                }
            });
    }

    /**
     * Called when a session is clicked
     * @param index the number of session clicked in the session list
     */
    public void onClickSession(int index) {
        Log.i("onClickSession", "Session clicked: " + sessions.get(index).getDate());

        Gson gson = new Gson();
        String sessionJson = gson.toJson(sessions.get(index), Session.class);

        Intent viewIntent = new Intent(this, ViewSessionActivity.class);
        viewIntent.putExtra(SESSION_KEY, sessionJson);
        startActivity(viewIntent);
    }

    /**
     * Resets the adapter to display the latest data.
     */
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

    /**
     * Deletes the session at the given position.
     * @param position the index number of the session clicked in the session list.
     */
    public void onClickDelete(int position) {
        sessions.remove(position);
        if(loadCloud.isChecked()) {
            Save save = new SaveCloud(mAuth.getUid());
            save.update(sessions);
        } else if (loadLocal.isChecked()) {
            Save save = new SaveLocal(getApplicationContext());
            save.update(sessions);
        }
        refreshAdapter();
    }

    /**
     * Renames the session at the given position.
     * @param position the index number of the session clicked in the session list.
     */
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
                    Save save = new SaveCloud(mAuth.getUid());
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

    /**
     * Copies the session at the given position and adds it to the corresponding list.
     * Copies both from local to cloud and from cloud to local.
     * @param position the index number of the session clicked in the session list.
     */
    public void onClickCopy(final int position) {
        Session copy = sessions.get(position);

        if(loadCloud.isChecked()) {
            LoadLocal loadLocal = new LoadLocal();
            SessionList sessionList = loadLocal.load(this);
            List<Session> localSessions = sessionList.getList();

            localSessions.add(copy);
            Save save = new SaveLocal(getApplicationContext());
            save.update(localSessions);
            Toast.makeText(getApplicationContext(),
                    "Saved to local device.",
                    Toast.LENGTH_SHORT).show();
        } else if (loadLocal.isChecked()) {
            SaveCloud save = new SaveCloud(mAuth.getUid());
            save.add(copy);
            Toast.makeText(getApplicationContext(),
                    "Saved to cloud.",
                    Toast.LENGTH_SHORT).show();
        }
        refreshAdapter();
    }

    /**
     * Toggles the loaded data to show cloud data.
     * @param v the View representing the button clicked.
     */
    public void loadCloudMethod(View v) {
        if (loadLocal.isChecked()) {
            loadLocal.setChecked(false);
        }

        if (loadCloud.isChecked()) {
            loadMethod = 0;
            findViewById(R.id._loadProgress).setVisibility(View.VISIBLE);
            loader = new LoadCloud(reference, mAuth.getCurrentUser().getUid());
            loader.load(this);
        } else {
            sessions = new ArrayList<>();
            refreshAdapter();
        }
    }

    /**
     * Toggles the loaded data to show local data.
     * @param v the View representing the button clicked.
     */
    public void loadLocalMethod(View v) {
        if (loadCloud.isChecked()) {
            loadCloud.setChecked(false);
        }

        if (loadLocal.isChecked()) {
            loadMethod = 1;
            findViewById(R.id._loadProgress).setVisibility(View.GONE);
            loader = new LoadLocal();
            sessionsList = loader.load(this);
            sessions = sessionsList.getList();

            if (sessionsList == null) {
                Toast.makeText(getApplicationContext(),
                        "No local data found.",
                        Toast.LENGTH_SHORT).show();
            } else {
                orderSessionsByDate();
                refreshAdapter();
            }
        } else {
            sessions = new ArrayList<>();
            refreshAdapter();
        }
    }

    /**
     * Checks to see if the device is connected to the internet via wifi or mobile data
     * @return true if connected, false if not connected
     */
    public boolean isConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager)getApplicationContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobileInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        if ((wifiInfo != null && wifiInfo.isConnected()) || (mobileInfo != null && mobileInfo.isConnected())) {
            return true;
        } else {
            return false;
        }
    }
}
