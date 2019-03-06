package com.threeguys.scrummy;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;

public class MainActivity extends AppCompatActivity {

    static final String SESSION_KEY =  "SESSION_KEY"; // used for passing sessions between activities
    static final String SP_FILE_NAME = "CONTINUE_SESSION";
    static final String CONTINUE_KEY = "CONTINUE_KEY";
    static final String ACTIVITY_KEY = "ACTIVITY_KEY";
    public static final String MAIN_TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    /**
     * Begins a new session
     * @param view, the "new session" button
     */
    public void onClickNew(View view) {
        // start the topic activity
        Intent newIntent = new Intent(this, TopicActivity.class);
        startActivity(newIntent);
    }

    /**
     * Loads the last partially complete session
     * @param view, the "continue session" button
     */
    public void onClickContinue(View view) {
        // access the session string in shared preferences
        SharedPreferences sessionSaveFile = this.getSharedPreferences(SP_FILE_NAME, Context.MODE_PRIVATE);
        String sessionJson = sessionSaveFile.getString(CONTINUE_KEY, "no session");

        if (!sessionJson.equals("no session")) {
            // turn the string into a Session object with Gson
            Gson gson = new Gson();
            Session continueSession = gson.fromJson(sessionJson, Session.class);
            // check to make sure there is data
            if (continueSession.getTopics().size() > 0) {

                //Which activity?
                switch (sessionSaveFile.getString(ACTIVITY_KEY,"")) {
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
        } else {
            Toast.makeText(this,
                    "There are no sessions to continue", Toast.LENGTH_SHORT).show();
        }

    }

    /**
     * Loads all previously completed sessions for viewing info
     * @param view, the "load session" button
     */
    public void onClickLoad(View view) {
        // start the load activity
        Intent loadIntent = new Intent(this, LoadActivity.class);
        startActivity(loadIntent);
    }
}
