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
import static com.threeguys.scrummy.MainActivity.TEMP_SAVE_PREF;

public class SprintActivity extends AppCompatActivity {

    public static final String SPRINT_TAG = SprintActivity.class.getSimpleName();

    Session session;
    private int topicNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sprint);

        // Initialize Member Data
        Gson gson = new Gson();
        String gsonSession = (String)getIntent().getExtras().get(MainActivity.SESSION_KEY);
        session = gson.fromJson(gsonSession, Session.class);
        topicNumber = 0;

        TextView currentTopic = findViewById(R.id._currentTopicTextView);
        Button nextTopic = findViewById(R.id._nextTopicButton);

        currentTopic.setText(session.getTopics().get(0).getTitle());
        // Is there more than one topic?
        if (session.getTopics().size() > 1) {
            nextTopic.setText(session.getTopics().get(1).getTitle());
        } else {
            nextTopic.setText(R.string.save_and_quit_button);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        SharedPreferences sp = this.getSharedPreferences(TEMP_SAVE_PREF, MODE_PRIVATE);
        Gson gson = new Gson();

        String sessionJson = gson.toJson(session, Session.class);
        String activityJson = "SprintActivity";

        SharedPreferences.Editor editor = sp.edit();
        editor.putString(CONTINUE_KEY, sessionJson);
        editor.putString(ACTIVITY_KEY, activityJson);
        editor.apply();
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

            // assign date to session date

            // convert session to String
            Gson gson = new Gson();


            // save in SharedPreferences


            //TODO save
            Date date = new Date();
            String strDateFormat = "yyyy/MM/dd hh:mm:ss";
            DateFormat dateFormat = new SimpleDateFormat(strDateFormat);
            String formattedDate= dateFormat.format(date);

            //return;
            Intent mainIntent = new Intent(this, MainActivity.class);
            startActivity(mainIntent);
        } else {

            // Go to next Topic
            currentTopic.setText(session.getTopics().get(topicNumber).getTitle());

            // Check if there is a next topic
            if (topicNumber + 1 < session.getTopics().size()) {
                nextTopic.setText(session.getTopics().get(topicNumber + 1).getTitle());
            } else {
                nextTopic.setText(R.string.save_and_quit_button);
            }
        }
    }
}
