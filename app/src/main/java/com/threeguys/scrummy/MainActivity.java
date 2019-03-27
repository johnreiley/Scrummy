package com.threeguys.scrummy;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

public class MainActivity extends AppCompatActivity {

    static final String SESSION_KEY =  "SESSION_KEY"; // used for passing sessions between activities
    static final String SESSION_TITLE_KEY = "session_title"; // used for passing the session title to the topic activity
    static final String TEMP_SAVE_PREF = "continue_session"; // used for storing temp session
    static final String SAVE_PREF = "saved_sessions"; // used for storing completed sessions
    static final String SESSION_LIST_KEY = "session_list_key"; // used for storing session list
    static final String CONTINUE_KEY = "continue_key"; // used for accessing temp session
    static final String ACTIVITY_KEY = "activity_key"; // used for accessing temp session activity
    static final String INDEX_KEY = "index_key"; // used for loading correct topic in sprint activity
    public static final String MAIN_TAG = MainActivity.class.getSimpleName();

    // ------- TEST STRINGS ------- //
    static final String USERNAME = "username"; // used for fetching and saving the user's data file



    AlertDialog titleDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.i(MAIN_TAG, "MainActivity Started");

        // check if there is a session in progress
        SharedPreferences spTemp = this.getSharedPreferences(TEMP_SAVE_PREF, MODE_PRIVATE);
        String sessionJson = spTemp.getString(CONTINUE_KEY, "no session");
        if (sessionJson.equals("no session")) {
            // hide the 'continue' button
            Button continueButton = findViewById(R.id._continueSessionButton);
            continueButton.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id._aboutMenuItem:
                Toast.makeText(getApplicationContext(),
                        "About menu item selected.",
                        Toast.LENGTH_SHORT).show();
                return true;
            case R.id._settingsMenuItem:
                //startActivity(new Intent(this, SettingsActivity.class));
                Toast.makeText(getApplicationContext(),
                        "Settings menu item selected.",
                        Toast.LENGTH_SHORT).show();
                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Begins a new session
     * @param view, the "new session" button
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
                    Toast.makeText(getApplicationContext(),
                            "Please enter a session title before continuing",
                            Toast.LENGTH_SHORT).show();
                } else {
                    Intent newIntent = new Intent(MainActivity.this, TopicActivity.class);
                    newIntent.putExtra(SESSION_TITLE_KEY, dialogTitle.getText().toString());
                    Session session = new Session();
                    Gson gson = new Gson();
                    String sessionJson = gson.toJson(session, Session.class);
                    newIntent.putExtra(SESSION_KEY, sessionJson);
                    startActivity(newIntent);
                    titleDialog.dismiss();
                }
            }
        });
    }

    /**
     * Loads the last partially complete session
     * @param view, the "continue session" button
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
        // check to make sure there is data
        if (continueSession != null && activityString != null) {
            //Which activity?
            switch (activityString) {
                case "TopicActivity":
                    // if there is data, call the Sprint activity and pass the session string
                    Intent topicIntent = new Intent(this, TopicActivity.class);
                    topicIntent.putExtra(SESSION_KEY, sessionJson);
                    startActivity(topicIntent);
                    break;
                case "VoteActivity":
                    // if there is data, call the Sprint activity and pass the session string
                    Intent voteIntent = new Intent(this, VoteActivity.class);
                    voteIntent.putExtra(SESSION_KEY, sessionJson);
                    startActivity(voteIntent);
                    break;
                case "SprintActivity":
                    // if there is data, call the Sprint activity and pass the session string
                    Intent sprintIntent = new Intent(this, SprintActivity.class);
                    sprintIntent.putExtra(SESSION_KEY, sessionJson);
                    sprintIntent.putExtra(INDEX_KEY, index);
                    startActivity(sprintIntent);
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
     * @param view, the "load session" button
     */
    public void onClickLoad(View view) {
        // check if there is any data saved.
        SharedPreferences spTemp = this.getSharedPreferences(SAVE_PREF, MODE_PRIVATE);
        String sessionJson = spTemp.getString(SESSION_LIST_KEY, "no saved data");
        if (sessionJson.equals("no saved data")) {
            // don't start the load activity because there is no data

            Toast.makeText(this,
                    "There is no saved data", Toast.LENGTH_SHORT).show();
        }
        else {
            // start the load activity
            Intent loadIntent = new Intent(this, LoadActivity.class);
            startActivity(loadIntent);
        }
    }

    @Override
    public void onBackPressed() {
        // do nothing
    }
}
