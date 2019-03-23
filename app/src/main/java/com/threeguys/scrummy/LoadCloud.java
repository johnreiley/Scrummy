package com.threeguys.scrummy;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;

import java.util.List;

import static com.threeguys.scrummy.MainActivity.USERNAME;


/**
 * Loads a list of sessions from FireBase.
 * Implements the Load class.
 */
public class LoadCloud implements Load {

    public static final String LOAD_CLOUD_TAG = LoadCloud.class.getSimpleName();
    private String userSessions;
    private SessionList sessions;

    public LoadCloud() {
    }

    @Override
    public SessionList load(Context context) {
        sessions = new SessionList();

        // Grab the JSON object related to our list of sessions from SharedPreferences.
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
                LoadCloud.this.userSessions = data;
                Log.d(LOAD_CLOUD_TAG, "userSessions == " + LoadCloud.this.userSessions);

                Log.d(LOAD_CLOUD_TAG, "The variable \'userSessions\' == " + userSessions);

                // If nothing was found
                if (!TextUtils.isEmpty(userSessions)) {
                    sessions = gson.fromJson(userSessions, SessionList.class);
                }

                Log.i("LoadLocal", "Loaded Sessions");
                Log.d(LOAD_CLOUD_TAG, "Size of sessionList == " +
                        String.valueOf(sessions.getList().size()));

                // GET THIS WORKING!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!



                // consider using a callback function (putting a bunch of SaveCloud's functionality
                // inside a separate class.
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
