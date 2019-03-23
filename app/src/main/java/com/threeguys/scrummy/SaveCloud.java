package com.threeguys.scrummy;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;

import java.util.List;

import static com.threeguys.scrummy.MainActivity.USERNAME;

public class SaveCloud extends Save {

    public static final String SAVE_CLOUD_TAG = SaveCloud.class.getSimpleName();
    private String userSessions;
    private Session session;
    private SessionList sessionList;

    private FirebaseStorage storage = FirebaseStorage.getInstance();

    public SaveCloud() {
    }

    // This constructor is only for debugging purposes
    public SaveCloud(Context context) {
        setSaveContext(context);
    }

    @Override
    public void save(Session session) {
        // load the session from Firebase

        // --------------------------- TEST CODE ---------------------------------------------------
        // -----------------------------------------------------------------------------------------
        this.session = session;

        // Grab the JSON object related to our list of sessions from Firebase.
        final Gson gson = new Gson();
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        StorageReference userDataRef = storageRef.child("/users/" + USERNAME + ".txt");

        // just the default value the app can handle
        final long ONE_MEGABYTE = 1024 * 1024;

        userDataRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {

                String data = new String(bytes);
                Log.d(SAVE_CLOUD_TAG, data);
                SaveCloud.this.userSessions = data;
                Log.d(SAVE_CLOUD_TAG, "userSessions == " + SaveCloud.this.userSessions);

                // If nothing was found
                if (!TextUtils.isEmpty(userSessions)) {
                    sessionList = gson.fromJson(userSessions, SessionList.class);
                }

                sessionList.addSession(SaveCloud.this.session);
                String sessionListJson = gson.toJson(sessionList, SessionList.class);

                Log.d(SAVE_CLOUD_TAG, "The variable sessionListJson == " + sessionListJson);


                // upload to Firebase
                byte sessionBytes[] = sessionListJson.getBytes();
                String path = "users/" + USERNAME + ".txt"; // this will be implemented to use the usersname to name the file
                StorageReference userRef = SaveCloud.this.storage.getReference(path);

                UploadTask uploadTask = userRef.putBytes(sessionBytes);

                // consider using a callback function (putting a bunch of SaveCloud's functionality
                // inside a separate class.

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });

        Log.d(SAVE_CLOUD_TAG, "The variable \'userSessions\' == " + userSessions);

        //------------------------------------------------------------------------------------------
        //------------------------------------------------------------------------------------------

    }

    @Override
    public void update(List<Session> list) {

        final Gson gson = new Gson();
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference userDataRef = storage.getReference().child("/users/" + USERNAME + ".txt");

        sessionList = new SessionList();
        sessionList.setList(list);
        String sessionListJson = gson.toJson(sessionList, SessionList.class);

        Log.d(SAVE_CLOUD_TAG, "The variable sessionListJson == " + sessionListJson);

        // upload to Firebase
        byte sessionBytes[] = sessionListJson.getBytes();
        String path = "users/" + USERNAME + ".txt"; // this will be implemented to use the username to name the file

        UploadTask uploadTask = userDataRef.putBytes(sessionBytes);
    }
}
