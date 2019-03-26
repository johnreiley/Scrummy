package com.threeguys.scrummy;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import static com.threeguys.scrummy.LoadActivity.LOAD_ACTIVITY_TAG;
import static com.threeguys.scrummy.MainActivity.USERNAME;


/**
 * Loads a list of sessions from FireBase.
 * Implements the Load class.
 */
public class LoadCloud implements Load {

    public static final String LOAD_CLOUD_TAG = LoadCloud.class.getSimpleName();
    private String userSessions;
    private SessionList sessions;
    private WeakReference<LoadActivity> loadWeakRef;
    private String username;

    public LoadCloud() {

    }

    public LoadCloud(WeakReference<LoadActivity> loadActivityWeakReference, String username) {
        this.loadWeakRef = loadActivityWeakReference;
        this.username = username;
    }

    @Override
    public SessionList load(Context context) {

        sessions = new SessionList();

        final Gson gson = new Gson();
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        StorageReference userDataRef = storageRef.child("/users/" + USERNAME + ".txt");

        // just the default value the app can handle
        final long ONE_MEGABYTE = 1024 * 1024;
        userDataRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                // Data for "images/island.jpg" is returns, use this as needed
                String data = new String(bytes);
                Log.d(LOAD_CLOUD_TAG, data);
                userSessions = data;
                Log.d(LOAD_CLOUD_TAG, "userSessions == " + userSessions);

                Log.d(LOAD_CLOUD_TAG, "The variable \'userSessions\' == " + userSessions);

                // If nothing was found
                if (!TextUtils.isEmpty(userSessions)) {
                    sessions = gson.fromJson(userSessions, SessionList.class);
                }

                Log.i(LOAD_CLOUD_TAG, "Loaded Sessions");
                Log.d(LOAD_CLOUD_TAG, "Size of sessionList == " +
                        String.valueOf(sessions.getList().size()));

                loadWeakRef.get().sessions = sessions.getList();
                loadWeakRef.get().orderSessionsByDate();
                loadWeakRef.get().findViewById(R.id._loadProgress).setVisibility(View.GONE);
                loadWeakRef.get().refreshAdapter();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });

        return sessions;
    }
}
