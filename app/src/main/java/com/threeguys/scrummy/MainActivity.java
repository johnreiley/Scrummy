package com.threeguys.scrummy;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.menu.MenuView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.gson.Gson;

public class MainActivity extends AppCompatActivity {

    static final String SESSION_KEY =  "SESSION_KEY"; // used for passing sessions between activities
    static final String TEMP_SAVE_PREF = "continue_session"; // used for storing temp session
    static final String SAVE_PREF = "saved_sessions"; // used for storing completed sessions
    static final String CONTINUE_KEY = "continue_key"; // used for accessing temp session
    static final String ACTIVITY_KEY = "activity_key"; // used for accessing temp session activity
    static final String LOAD_KEY = "load_key"; // used for accessing the load activity
    public static final String MAIN_TAG = MainActivity.class.getSimpleName();

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

    /**
     * Begins a new session
     * @param view, the "new session" button
     */
    public void onClickNew(View view) {
        if (findViewById(R.id._continueSessionButton).getVisibility() == View.GONE) {
            Intent newIntent = new Intent(this, TopicActivity.class);
            startActivity(newIntent);
        } else {
            //TODO: make a pop-up dialogue for this
            Toast.makeText(this, R.string.new_dialogue_warning, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Loads the last partially complete session
     * @param view, the "continue session" button
     */
    public void onClickContinue(View view) {
        // access the session string in shared preferences
        SharedPreferences spTemp = this.getSharedPreferences(TEMP_SAVE_PREF, MODE_PRIVATE);
        String sessionJson = spTemp.getString(CONTINUE_KEY, "no session");

        // turn the string into a Session object with Gson
        Gson gson = new Gson();
        Session continueSession = gson.fromJson(sessionJson, Session.class);
        // check to make sure there is data
        if (continueSession.getTopics().size() > 0) {

            //Which activity?
            switch (spTemp.getString(ACTIVITY_KEY,"")) {
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
        String sessionJson = spTemp.getString(LOAD_KEY, "no saved data");
        if (sessionJson.equals("no saved data")) {
            // don't start the load activity because there is not data
            Toast.makeText(this,
                    "There is no saved data", Toast.LENGTH_SHORT).show();
        }
        else {
            // start the load activity
            Intent loadIntent = new Intent(this, LoadActivity.class);
            startActivity(loadIntent);
        }

    }
}
