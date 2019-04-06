package com.threeguys.scrummy;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.gson.Gson;

public class MainActivity extends AppCompatActivity {

    static final String SESSION_KEY =  "SESSION_KEY"; // used for passing sessions between activities
    static final String TEMP_SAVE_PREF = "continue_session"; // used for storing temp session
    static final String SAVE_PREF = "saved_sessions"; // used for storing completed sessions
    static final String SESSION_LIST_KEY = "session_list_key"; // used for storing session list
    static final String CONTINUE_KEY = "continue_key"; // used for accessing temp session
    static final String ACTIVITY_KEY = "activity_key"; // used for accessing temp session activity
    static final String INDEX_KEY = "index_key"; // used for loading correct topic in sprint activity
    static final String HOST_KEY = "host_key"; // used for loading correct topic in sprint activity
    public static final String MAIN_TAG = MainActivity.class.getSimpleName();

    private DatabaseReference activityRef;
    private ValueEventListener activityVEL;
    private DatabaseReference sessionDataRef;
    private ValueEventListener sessionVEL;
    private DatabaseReference indexRef;
    private ValueEventListener indexVEL;

    // ------- TEST STRINGS ------- //
    static final String USERNAME = "username"; // used for fetching and saving the user's data file
    private String userID;

    private FirebaseAuth mAuth;

    AlertDialog titleDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.i(MAIN_TAG, "MainActivity Started");

        mAuth = FirebaseAuth.getInstance();

        // check to make sure the user is still logged in
        if (mAuth == null) {
            Log.d(MAIN_TAG, "mAuth ======= NULL");
            finishAffinity();
        }

        userID = mAuth.getCurrentUser().getUid();

        final Button continueButton = findViewById(R.id._continueSessionButton);

        // Setup firebase database and update the sharedPreferences
        SharedPreferences spTemp = getSharedPreferences(TEMP_SAVE_PREF, MODE_PRIVATE);
        final SharedPreferences.Editor edit = spTemp.edit();
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        // Session database
        sessionDataRef = database.getReference().child("users").child(userID).child("session");
        sessionDataRef.addValueEventListener(sessionVEL = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String value = dataSnapshot.getValue(String.class);

                edit.putString(CONTINUE_KEY, value).apply();

                Log.d(MAIN_TAG, "FirebaseDatabase value is: " + value);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(MAIN_TAG, "Failed to read value.", error.toException());
            }
        });
        // Activity database
        activityRef = database.getReference().child("users").child(userID).child("activity");
        activityVEL = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String value = dataSnapshot.getValue(String.class);

                if(value != null) {
                    if (value.equals("MainActivity")) {
                        continueButton.setVisibility(View.GONE);
                    } else {
                        continueButton.setVisibility(View.VISIBLE);
                    }
                }

                edit.putString(ACTIVITY_KEY, value);
                edit.apply();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Failed to read value
                Log.w(MAIN_TAG, "Failed to read value.", error.toException());
            }
        };
        activityRef.addValueEventListener(activityVEL);
        // Update index in the case of a sprint activity
        indexRef = database.getReference().child("users").child(userID).child("currentTopic");
        indexRef.addValueEventListener(indexVEL = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Integer value = dataSnapshot.getValue(Integer.class);

                if (value != null)
                    edit.putString(INDEX_KEY, value.toString()).apply();

                Log.d(MAIN_TAG, "FirebaseDatabase value is: " + value);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(MAIN_TAG, "Failed to read value.", error.toException());
            }
        });

        // Check if there is a session in progress
        String sessionJson = spTemp.getString(CONTINUE_KEY, "no session");
        String activityJson = spTemp.getString(ACTIVITY_KEY, "no activity");
        Log.d(MAIN_TAG, "Session Json is: " + sessionJson);
        if (sessionJson.equals("no session") || sessionJson.equals(" ")
                || activityJson.equals("no activity")) {
            // Hide the 'continue' button
            continueButton.setVisibility(View.GONE);
        }
        else {
            continueButton.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main_activity, menu);
        return true;
    }

    @Override
    public void finish() {
        activityRef.removeEventListener(activityVEL);
        sessionDataRef.removeEventListener(sessionVEL);
        indexRef.removeEventListener(indexVEL);

        super.finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id._helpMenuItem:
                startActivity(new Intent(MainActivity.this, HelpActivity.class));
                finish();
                return true;
            case R.id._aboutMenuItem:
                startActivity(new Intent(MainActivity.this, AboutActivity.class));
                finish();
                return true;
            case R.id._settingsMenuItem:
                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                finish();
                return true;
            case R.id._signOutMenuItem:
                onClickSignOut(null);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Starts the signing out of the group account currently connected
     * @param v button view
     */
    public void onClickSignOut(View v) {
        try {
            mAuth.signOut();
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    /**
     * Begins a new session
     * @param view the "new session" button
     */
    public void onClickNew(View view) {
        View v = (LayoutInflater.from(MainActivity.this)).inflate(R.layout.title_dialog, null);

        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(MainActivity.this);
        alertBuilder.setView(v);

        TextView dialogText1 = v.findViewById(R.id._dialogTextView1);
        TextView dialogText2 = v.findViewById(R.id._dialogTextView2);
        TextView dialogText3 = v.findViewById(R.id._dialogTextView3);
        final EditText dialogTitle = v.findViewById(R.id._dialogTitleEditText);

        alertBuilder.setCancelable(true).setPositiveButton("Start", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        dialogText1.setEnabled(true);
        dialogText2.setEnabled(true);
        dialogText3.setEnabled(true);
        dialogTitle.setEnabled(true);

        if (findViewById(R.id._continueSessionButton).getVisibility() == View.GONE) {
            dialogText1.setText(R.string.dialog_new_name);
            dialogText2.setVisibility(View.GONE);
            dialogText3.setVisibility(View.GONE);
        } else {
            dialogText1.setText(R.string.dialog_warning_1);
            dialogText2.setText(R.string.dialog_warning_2);
            dialogText3.setText(R.string.dialog_new_name);
        }

        titleDialog = alertBuilder.create();
        titleDialog.show();

        titleDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Log.d(MAIN_TAG, "dialogTitle == " + dialogTitle.getText());

                if (TextUtils.isEmpty(dialogTitle.getText().toString())) {
                    dialogTitle.setError("Enter a session title");
                    // TODO: remove the code below
//                    Toast.makeText(getApplicationContext(),
//                            "Please enter a session title before continuing",
//                            Toast.LENGTH_SHORT).show();
                } else {
                    Intent newIntent = new Intent(MainActivity.this, TopicActivity.class);
                    //newIntent.putExtra(SESSION_TITLE_KEY, dialogTitle.getText().toString());
                    Session session = new Session();
                    session.setTitle(dialogTitle.getText().toString());
                    Gson gson = new Gson();
                    String sessionJson = gson.toJson(session, Session.class);
                    newIntent.putExtra(SESSION_KEY, sessionJson);
                    startActivity(newIntent);
                    finish();
                    titleDialog.dismiss();
                }
            }
        });
    }

    /**
     * Loads the last partially complete session
     * @param view the "continue session" button
     */
    public void onClickContinue(View view) {
        // access the session string in shared preferences
        SharedPreferences spTemp = this.getSharedPreferences(TEMP_SAVE_PREF, MODE_PRIVATE);
        String sessionJson = spTemp.getString(CONTINUE_KEY, "no session");
        String index = spTemp.getString(INDEX_KEY, "0");
        String activityString = spTemp.getString(ACTIVITY_KEY, "");
        // turn the string into a Session object with Gson
        Gson gson = new Gson();
        Session continueSession = gson.fromJson(sessionJson, Session.class);

        Log.d(MAIN_TAG,"Session Data === " + sessionJson +
                "|| Activity === " + activityString);

        // check to make sure there is data
        if (continueSession != null && activityString != null) {
            //Which activity?
            switch (activityString) {
                case "TopicActivity":
                    // if there is data, call the Sprint activity and pass the session string
                    Intent topicIntent = new Intent(this, TopicActivity.class);
                    topicIntent.putExtra(SESSION_KEY, sessionJson);
                    startActivity(topicIntent);
                    finish();
                    break;
                case "VoteActivity":
                    // if there is data, call the Sprint activity and pass the session string
                    Intent voteIntent = new Intent(this, VoteActivity.class);
                    voteIntent.putExtra(SESSION_KEY, sessionJson);
                    startActivity(voteIntent);
                    finish();
                    break;
                case "SprintActivity":
                    // if there is data, call the Sprint activity and pass the session string
                    Intent sprintIntent = new Intent(this, SprintActivity.class);
                    sprintIntent.putExtra(SESSION_KEY, sessionJson);
                    sprintIntent.putExtra(INDEX_KEY, index);
                    startActivity(sprintIntent);
                    finish();
                    break;
                default:
                    Toast.makeText(this,
                            "No active session", Toast.LENGTH_SHORT).show();
                    break;
            }
        } else {
            // if not, display text
            Toast.makeText(this,
                    "There are no topics in the session to continue", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Loads all previously completed sessions for viewing info
     * @param view the "load session" button
     */
    public void onClickLoad(View view) {
        // check if there is any data saved.

        // if (settings.loadMethod == "local")
        //------------------------------------------------------------------------------------------
//        SharedPreferences spTemp = this.getSharedPreferences(SAVE_PREF, MODE_PRIVATE);
//        String sessionJson = spTemp.getString(SESSION_LIST_KEY, "no saved data");
//        if (sessionJson.equals("no saved data")) {
//            // don't start the load activity because there is no data
//
//            Toast.makeText(this,
//                    "There is no saved data", Toast.LENGTH_SHORT).show();
//        }
//        else {
//            // start the load activity
//            Intent loadIntent = new Intent(this, LoadActivity.class);
//            startActivity(loadIntent);
//        }
        //------------------------------------------------------------------------------------------

        Intent loadIntent = new Intent(this, LoadActivity.class);
        startActivity(loadIntent);
        finish();

    }

    @Override
    public void onBackPressed() {
        // do nothing
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

    @Override
    public void onStop() {
        finish();

        super.onStop();
    }
}
