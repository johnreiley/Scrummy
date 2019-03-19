package com.threeguys.scrummy;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.CountDownTimer;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.gson.Gson;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.threeguys.scrummy.MainActivity.ACTIVITY_KEY;
import static com.threeguys.scrummy.MainActivity.CONTINUE_KEY;
import static com.threeguys.scrummy.MainActivity.INDEX_KEY;
import static com.threeguys.scrummy.MainActivity.SESSION_KEY;
import static com.threeguys.scrummy.MainActivity.TEMP_SAVE_PREF;

public class SprintActivity extends AppCompatActivity {

    public static final String SPRINT_TAG = SprintActivity.class.getSimpleName();

    Session session;
    private int topicNumber;
    private ImageButton playPause;
    private boolean isPaused;
    private ImageButton toggleAlarm;
    private boolean isMuted;
    private CountDownTimer timer;
    private long timeRemaining;
    private TextView time;
    private MediaPlayer mp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sprint);
        Log.i(SPRINT_TAG, "SprintActivity Started");

        // Initialize Member Data
        Gson gson = new Gson();
        String gsonSession = (String)getIntent().getExtras().get(SESSION_KEY);
        session = gson.fromJson(gsonSession, Session.class);
        // prioritize the topics by highest vote
        session.sortByVote();

        Log.i(SPRINT_TAG, "find out the index of the topic to load first");
        String index = (String)getIntent().getExtras().get(INDEX_KEY);
        if (Integer.valueOf(index) < 0) {
            Log.e(SPRINT_TAG, "WOAH!! INDEX is less than 0 ya'll.  " +
                    "Something done been messed up!");
            return;
        }
        topicNumber = Integer.valueOf(index);

        playPause = findViewById(R.id._playPauseTimeButton);
        toggleAlarm = findViewById(R.id._alarmButton);
        isPaused = false;
        isMuted = false;

        setupNextTopic();

        mp = MediaPlayer.create(getApplicationContext(), Settings.System.DEFAULT_ALARM_ALERT_URI);
        time = findViewById(R.id._timeValueTextView);
        time.setText("5:00");
        timeRemaining = 300000; //300000 milliseconds is 5 minutes
        refreshTimer();
    }

    @Override
    protected void onStop() {
        super.onStop();

        SharedPreferences sp = this.getSharedPreferences(TEMP_SAVE_PREF, MODE_PRIVATE);
        Gson gson = new Gson();

        if (topicNumber < session.getTopics().size()) {
            saveTopicActions();

            String sessionJson = gson.toJson(session, Session.class);
            String activityJson = "SprintActivity";

            SharedPreferences.Editor editor = sp.edit();
            editor.putString(CONTINUE_KEY, sessionJson);
            editor.putString(ACTIVITY_KEY, activityJson);
            editor.putString(INDEX_KEY, String.valueOf(topicNumber));
            editor.apply();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mp.pause();
    }


    public void onClickPrevTopic(View view) {
        saveTopicActions();
        topicNumber--;
        setupNextTopic();
        /*
        if (topicNumber > 0) {
            setupNextTopic();
        } else {
            findViewById(R.id._prevTopicButton).setVisibility(View.GONE);
        }
        */
        timeRemaining = 300000;
        refreshTimer();
    }


    public void onClickNextTopic(View view) {
        // add the actions to the session topic
        saveTopicActions();

        topicNumber++;

        if (topicNumber >= session.getTopics().size()) {
            // No more topics, save and quit
            Log.d(SPRINT_TAG, "Topic # == " + topicNumber +
                    "| getTopics.size() == " + session.getTopics().size());

            // get the date
            Date date = new Date();
            String strDateFormat = "yyyy/MM/dd a hh:mm:ss";
            DateFormat dateFormat = new SimpleDateFormat(strDateFormat);
            String formattedDate= dateFormat.format(date);

            // DEBUGGING
            Log.d(SPRINT_TAG, "DATE = " + formattedDate);

            // assign date to session date
            session.setDate(formattedDate);

            // save in SharedPreferences
            Save save = new SaveLocal(getApplicationContext());
            save.save(session);

            Log.i(SPRINT_TAG, "Session saved");

            // clear the temp file
            SharedPreferences sp = this.getSharedPreferences(TEMP_SAVE_PREF, MODE_PRIVATE);
            sp.edit().clear().apply();

            Log.i(SPRINT_TAG, "Temporary files cleared");

            // go back to the main menu
            Intent mainIntent = new Intent(this, MainActivity.class);
            startActivity(mainIntent);
        } else {
            setupNextTopic();
            timeRemaining = 300000;
            refreshTimer();
        }
    }

    /**
     * Sets the appropriate text for the next Topic
     */
    private void setupNextTopic() {
        // check whether or not to show the 'previous' button
        if (topicNumber > 0) {
            findViewById(R.id._prevTopicButton).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id._prevTopicButton).setVisibility(View.GONE);
        }

        TextView currentTopic = findViewById(R.id._currentTopicTextView);
        Button nextTopic = findViewById(R.id._nextTopicButton);
        TextView actionsTextView = findViewById(R.id._actionsMultiAutoCompleteTextView);

        currentTopic.setText(session.getTopics().get(topicNumber).getTitle());
        actionsTextView.setText(session.getTopics().get(topicNumber).getActions());
        // Is there more than one topic?
        if (topicNumber + 1 < session.getTopics().size()) {
            String nextText = getString(R.string.next_topic_button);
            String nextTopicText = nextText + " " +
                    session.getTopics().get(topicNumber + 1).getTitle();
            nextTopic.setText(nextTopicText);
        } else {
            nextTopic.setText(R.string.save_and_quit_button);
        }
    }

    private void saveTopicActions() {
        EditText actionsTextView = findViewById(R.id._actionsMultiAutoCompleteTextView);
        session.getTopics().get(topicNumber).setActions(actionsTextView.getText().toString());
        actionsTextView.setText("");
    }

    public void playPause(View v) {
        if(!isPaused) {
            playPause.setImageResource(android.R.drawable.ic_media_play);
            isPaused = true;
            mp.pause();
        } else {
            playPause.setImageResource(android.R.drawable.ic_media_pause);
            isPaused = false;
            refreshTimer();
        }
    }

    public void rewind(View v) {
        timeRemaining = 300000;
        refreshTimer();
        time.setText("5:00");
    }

    public void fastForward(View v) {
        timer.onFinish();
        timer.cancel();
        timeRemaining = 0;
        time.setText("0:00");
    }

    public void toggleAlarm(View v) {
        if(!isMuted) {
            toggleAlarm.setImageResource(android.R.drawable.ic_lock_silent_mode);
            isMuted = true;
            mp.pause();
        } else {
            toggleAlarm.setImageResource(android.R.drawable.ic_lock_silent_mode_off);
            isMuted = false;
        }
    }

    private void refreshTimer() {
        if (timer != null) {
            timer.cancel();
            mp.pause();
        }

        timer = new CountDownTimer(timeRemaining, 250) {
            @Override
            public void onTick(long millisUntilFinished) {
                if(isPaused) {
                    cancel();
                } else {
                    long minutes = Math.round(millisUntilFinished / 60000);
                    long seconds = Math.round(millisUntilFinished % 60000 / 1000);
                    String display = String.format("%01d:%02d", minutes, seconds);
                    time.setText(display);
                    timeRemaining = millisUntilFinished;
                }
            }

            @Override
            public void onFinish() {
                //Sound the alarm!
                time.setText("0:00");
                if(!isMuted){
                    mp.setLooping(true);
                    mp.start();
                }
            }
        }.start();
    }
}

