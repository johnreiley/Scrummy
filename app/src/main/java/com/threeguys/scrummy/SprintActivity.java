package com.threeguys.scrummy;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.MultiAutoCompleteTextView;
import android.widget.TextView;

import com.google.gson.Gson;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import static com.threeguys.scrummy.MainActivity.ACTIVITY_KEY;
import static com.threeguys.scrummy.MainActivity.CONTINUE_KEY;
import static com.threeguys.scrummy.MainActivity.SESSION_KEY;
import static com.threeguys.scrummy.MainActivity.TEMP_SAVE_PREF;

public class SprintActivity extends AppCompatActivity {

    public static final String SPRINT_TAG = SprintActivity.class.getSimpleName();

    Session session;
    private int topicNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sprint);
        Log.i(SPRINT_TAG, "SprintActivity Started");

        // Initialize Member Data
        Gson gson = new Gson();
        String gsonSession = (String)getIntent().getExtras().get(SESSION_KEY);
        session = gson.fromJson(gsonSession, Session.class);
        topicNumber = 0;

        TextView currentTopic = findViewById(R.id._currentTopicTextView);
        Button nextTopic = findViewById(R.id._nextTopicButton);

        currentTopic.setText(session.getTopics().get(0).getTitle());
        // Is there more than one topic?
        if (session.getTopics().size() > 1) {
            nextTopic.setText(getNextTopicText());
        } else {
            nextTopic.setText(R.string.save_and_quit_button);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        SharedPreferences sp = this.getSharedPreferences(TEMP_SAVE_PREF, MODE_PRIVATE);
        Gson gson = new Gson();

        if (topicNumber < session.getTopics().size()) {
            String sessionJson = gson.toJson(session, Session.class);
            String activityJson = "SprintActivity";

            SharedPreferences.Editor editor = sp.edit();
            editor.putString(CONTINUE_KEY, sessionJson);
            editor.putString(ACTIVITY_KEY, activityJson);
            editor.apply();
        }
    }


    public void onClickNextTopic(View view) {
        TextView currentTopic = findViewById(R.id._currentTopicTextView);
        Button nextTopic = findViewById(R.id._nextTopicButton);
        MultiAutoCompleteTextView actionsView = findViewById(R.id._actionsMultiAutoCompleteTextView);

        // add the actions to the session topic
        session.getTopics().get(topicNumber).setActions(actionsView.getText().toString());
        actionsView.setText("");

        topicNumber++;

        if (topicNumber >= session.getTopics().size()) {
            // No more topics, save and quit
            Log.d(SPRINT_TAG, "Topic # == " + topicNumber +
                    "| getTopics.size() == " + session.getTopics().size());

            // get the date
            Date date = new Date();
            String strDateFormat = "yyyy/MM/dd hh:mm:ss";
            DateFormat dateFormat = new SimpleDateFormat(strDateFormat);
            String formattedDate= dateFormat.format(date);

            // DEBUGGING
            Log.d(SPRINT_TAG, "DATE = " + formattedDate);

            // assign date to session date
            session.setDate(formattedDate);

            // save in SharedPreferences
            Save save = new SaveLocal(session, getApplicationContext());
            save.save();

            // clear the temp file
            SharedPreferences sp = this.getSharedPreferences(TEMP_SAVE_PREF, MODE_PRIVATE);
            sp.edit().clear().apply();

            // go back to the main menu
            Intent mainIntent = new Intent(this, MainActivity.class);
            startActivity(mainIntent);
        } else {

            // Go to next Topic
            currentTopic.setText(session.getTopics().get(topicNumber).getTitle());

            // Check if there is a next topic
            if (topicNumber + 1 < session.getTopics().size()) {
                nextTopic.setText(getNextTopicText());
            } else {
                nextTopic.setText(R.string.save_and_quit_button);
            }
        }
    }

    private String getNextTopicText() {
        String nextText = getString(R.string.next_topic_button);
        return nextText + " " + session.getTopics().get(topicNumber + 1).getTitle();
    }
}

