package com.threeguys.scrummy;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import static com.threeguys.scrummy.MainActivity.USERNAME;

public class SaveCloud extends Save {

    public static final String SAVE_CLOUD_TAG = SaveCloud.class.getSimpleName();
    private String sessionListString;
    private SessionList sessionList;

    private WeakReference<SprintActivity> sprintWeakRef;
    private String userID;

    private FirebaseStorage storage = FirebaseStorage.getInstance();

    public SaveCloud(WeakReference<SprintActivity> sprintWeakRef, String userID) {
        this.sprintWeakRef = sprintWeakRef;
        this.userID = userID;
    }

    public SaveCloud(String userID) {
        this.userID = userID;
    }

    // This constructor is only for debugging purposes
    public SaveCloud(Context context) {
        setSaveContext(context);
    }

    @Override
    public void save(final Session session) {
        // Grab the JSON object related to our list of sessions from Firebase.
        final Gson gson = new Gson();
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        StorageReference userDataRef = storageRef.child("/users/" + userID + ".txt");
        // TODO: find out how to check if the path exists yet or not

        // just the default value the app can handle
        final long ONE_MEGABYTE = 1024 * 1024;
        userDataRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {

                String data = new String(bytes);
                Log.d(SAVE_CLOUD_TAG, data);
                SaveCloud.this.sessionListString = data;
                Log.d(SAVE_CLOUD_TAG, "userSessions == " + SaveCloud.this.sessionListString);

                if (!TextUtils.isEmpty(sessionListString)) {
                    sessionList = gson.fromJson(sessionListString, SessionList.class);
                } else {
                    sessionList = new SessionList();
                }

                sessionList.addSession(session);
                final String sessionListJson = gson.toJson(sessionList, SessionList.class);

                Log.d(SAVE_CLOUD_TAG, "The variable sessionListJson == " + sessionListJson);

                // upload to Firebase
                byte sessionBytes[] = sessionListJson.getBytes();
                String path = "users/" + userID + ".txt"; // this will be implemented to use the usersname to name the file
                StorageReference userRef = SaveCloud.this.storage.getReference(path);

                UploadTask uploadTask = userRef.putBytes(sessionBytes);

                uploadTask.addOnSuccessListener(sprintWeakRef.get(), new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        sprintWeakRef.get().findViewById(R.id._loadProgress)
                                .setVisibility(View.GONE);
                        Intent intent = new Intent(sprintWeakRef.get(), MainActivity.class);
                        sprintWeakRef.get().startActivity(intent);
                        sprintWeakRef.get().activityDataRef.setValue("MainActivity");
                    }


                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                sessionList = new SessionList();
                sessionList.addSession(session);
                final String sessionListJson = gson.toJson(sessionList, SessionList.class);
                byte sessionBytes[] = sessionListJson.getBytes();
                String path = "users/" + userID + ".txt"; // this will be implemented to use the usersname to name the file
                StorageReference userRef = SaveCloud.this.storage.getReference(path);

                UploadTask uploadTask = userRef.putBytes(sessionBytes);

                uploadTask.addOnSuccessListener(sprintWeakRef.get(), new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        sprintWeakRef.get().findViewById(R.id._loadProgress)
                                .setVisibility(View.GONE);
                        Intent intent = new Intent(sprintWeakRef.get(), MainActivity.class);
                        sprintWeakRef.get().startActivity(intent);
                        sprintWeakRef.get().activityDataRef.setValue("MainActivity");
                    }
                });
            }
        });
    }
    @Override
    public void update (final List<Session> list) {
        Log.d(SAVE_CLOUD_TAG, "Size of session list == " + list.size());

        final Gson gson = new Gson();
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference userDataRef = storage.getReference().child("/users/" + userID + ".txt");

        sessionList = new SessionList();
        sessionList.setList(list);
        String sessionListJson = gson.toJson(sessionList, SessionList.class);

        Log.d(SAVE_CLOUD_TAG, "The variable sessionListJson == " + sessionListJson);

        // upload to Firebase
        byte sessionBytes[] = sessionListJson.getBytes();

        UploadTask uploadTask = userDataRef.putBytes(sessionBytes);
    }

    /**
     * Adds a session to the list of saved sessions.
     * @param session
     */
    public void add (final Session session) {
        sessionList = new SessionList();

        final Gson gson = new Gson();
        FirebaseStorage storage = FirebaseStorage.getInstance();
        final StorageReference storageRef = storage.getReference();
        StorageReference userDataRef = storageRef.child("/users/" + userID + ".txt");

        // just the default value the app can handle
        final long ONE_MEGABYTE = 1024 * 1024;
        userDataRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                // Data for "images/island.jpg" is returns, use this as needed
                String data = new String(bytes);
                Log.d(SAVE_CLOUD_TAG, data);
                Log.d(SAVE_CLOUD_TAG, "userSessions == " + data);

                Log.d(SAVE_CLOUD_TAG, "The variable \'userSessions\' == " + data);

                // If nothing was found
                if (!TextUtils.isEmpty(data)) {
                    sessionList = gson.fromJson(data, SessionList.class);
                }

                Log.i(SAVE_CLOUD_TAG, "Loaded Sessions");
                Log.d(SAVE_CLOUD_TAG, "Size of sessionList == " +
                        String.valueOf(sessionList.getList().size()));

                sessionList.addSession(session);
                update(sessionList.getList());
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.d(SAVE_CLOUD_TAG, "Failure to download file");
            }
        });
    }
}

