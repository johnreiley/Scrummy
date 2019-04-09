package com.threeguys.scrummy;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.lang.ref.WeakReference;
import java.net.URI;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.threeguys.scrummy.MainActivity.ACTIVITY_KEY;
import static com.threeguys.scrummy.MainActivity.CONTINUE_KEY;
import static com.threeguys.scrummy.MainActivity.HOST_KEY;
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
    private long timerDuration;
    private long timeRemaining;
    private TextView time;
    private MediaPlayer mp;
    private boolean isMenuDisabled, saveCloud, saveLocal;
    private String userID;
    private boolean isHost;

    private DatabaseReference sessionDataRef;
    private ValueEventListener sessionVEL;
    public DatabaseReference activityDataRef;
    private ValueEventListener activityVEL;
    private DatabaseReference currentTopicDataRef;
    private ValueEventListener currentTopicVEL;

    AlertDialog changeTimeDialogue;

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
        isMenuDisabled = false;

        setupNextTopic();

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        Integer minutes = Integer.valueOf(preferences.getString("timer_minutes", "5"));
        Integer seconds = Integer.valueOf(preferences.getString("timer_seconds", "0"));
        saveCloud = preferences.getBoolean("save_cloud", true);
        saveLocal = preferences.getBoolean("save_local", false);
        Log.d(SPRINT_TAG, "saveCloud = " + saveCloud);
        Log.d(SPRINT_TAG, "saveLocal = " + saveLocal);
        Uri alarmRingtone = Uri.parse(preferences.getString("alarm_ringtone", Settings.System.DEFAULT_ALARM_ALERT_URI.toString()));

        timerDuration = (minutes * 60000) + (seconds * 1000);
        mp = MediaPlayer.create(getApplicationContext(), alarmRingtone);
        time = findViewById(R.id._timeValueTextView);

        //timerDuration = 300000; //300000 milliseconds is 5 minutes
        timeRemaining = timerDuration + 1000;
        refreshTimer();

        userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Setup Firebase database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        // Setup activity Firebase
        activityDataRef = database.getReference().child("users").child(userID).child("activity");
        activityDataRef.setValue("SprintActivity");
        activityDataRef.addValueEventListener(activityVEL = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String activity = dataSnapshot.getValue(String.class);

                switch (activity){
                    case "TopicActivity":
                        break;
                    case "VoteActivity":
                        break;
                    case "SprintActivity":
                        break;
                    case "MainActivity":
                        quit();
                        break;
                    default:
                        Log.wtf(SPRINT_TAG, "The activity in database is: " + activity);
                        break;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Failed to read value
                Log.w(SPRINT_TAG, "Failed to read value.", error.toException());
            }
        });
        // Setup session Firebase
        sessionDataRef = database.getReference().child("users").child(userID).child("session");
        updateFirebaseSession();
        sessionDataRef.addValueEventListener(sessionVEL = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String value = dataSnapshot.getValue(String.class);

                Gson gson = new Gson();
                session = gson.fromJson(value, Session.class);

                Log.d(SPRINT_TAG, "FirebaseDatabase value is: " + value);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(SPRINT_TAG, "Failed to read value.", error.toException());
            }
        });
        // Setup current topic Firebase
        currentTopicDataRef = database.getReference().child("users").child(userID).child("currentTopic");
        currentTopicDataRef.setValue(topicNumber);
        currentTopicDataRef.addValueEventListener(currentTopicVEL = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Integer value = dataSnapshot.getValue(Integer.class);

                topicNumber = value;
                if(topicNumber < session.getTopics().size())
                    setupNextTopic();

                Log.d(SPRINT_TAG, "FirebaseDatabase value is: " + value);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(SPRINT_TAG, "Failed to read value.", error.toException());
            }
        });

        isHost = getIntent().getBooleanExtra(HOST_KEY, false);

        Log.d(SPRINT_TAG, "Host value is: " + isHost);

        if(!isHost) disableAll();
    }

    @Override
    public void finish() {
        sessionDataRef.removeEventListener(sessionVEL);
        activityDataRef.removeEventListener(activityVEL);
        currentTopicDataRef.removeEventListener(currentTopicVEL);

        super.finish();
    }

    /**
     * Disables all buttons and clock if not the host of the session
     */
    private void disableAll() {
        // Actions
        findViewById(R.id._actionsMultiAutoCompleteTextView).setVisibility(View.GONE);
        findViewById(R.id._actionsTextView).setVisibility(View.GONE);

        // Buttons
        findViewById(R.id._nextTopicButton).setVisibility(View.GONE);
        findViewById(R.id._prevTopicButton).setVisibility(View.GONE);

        // Clock
        findViewById(R.id._timeValueTextView).setVisibility(View.GONE);
        findViewById(R.id._playPauseTimeButton).setVisibility(View.GONE);
        findViewById(R.id._resetTimeButton).setVisibility(View.GONE);
        findViewById(R.id._stopTimeButton).setVisibility(View.GONE);
        findViewById(R.id._alarmButton).setVisibility(View.GONE);
        if(timer != null) timer.cancel();
        if(mp != null) mp.stop();
        isMuted = true;
    }

    /**
     * Quit without saving. Used for cloud saving, so only one person saves.
     */
    private void quit()
    {
        // Clear the temp file
        SharedPreferences sp = this.getSharedPreferences(TEMP_SAVE_PREF, MODE_PRIVATE);
        sp.edit().clear().apply();

        if(timer != null) timer.cancel();
        isMuted = true;
        if(mp != null) mp.stop();

        // Go to MainActivity
        Intent mainIntent = new Intent(this, MainActivity.class);
        startActivity(mainIntent);
        finish();
    }

    /**
     * Pushes the user's session to the FireBase and updates continue key.
     */
    private void updateFirebaseSession() {
        Gson gson = new Gson();
        String sessionJson = gson.toJson(session, Session.class);
        sessionDataRef.setValue(sessionJson);
        Log.i(SPRINT_TAG, "Firebase Updated");

        String activityJson = "SprintActivity";

        SharedPreferences sp = this.getSharedPreferences(TEMP_SAVE_PREF, MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(CONTINUE_KEY, sessionJson);
        editor.putString(ACTIVITY_KEY, activityJson);
        editor.apply();
        Log.i(SPRINT_TAG, "Firebase Updated The Continue Button");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_sprint_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id._changeTimeMenuItem && !isMenuDisabled) {
            onClickTimerDuration();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Displays a dialogue to enable the user to edit the duration of the timer.
     */
    private void onClickTimerDuration() {
        Log.d(SPRINT_TAG, "entered onclick Timer Duration");
        View v = (LayoutInflater.from(this)).inflate(R.layout.change_timer_duration_dialog, null);

        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
        alertBuilder.setView(v);

        final Spinner minute = v.findViewById(R.id._minuteInputSpinner);
        final Spinner second = v.findViewById(R.id._secondInputSpinner);

        minute.setSelection(Math.round(timerDuration / 60000));
        second.setSelection(Math.round(timerDuration % 60000 / 1000));

        alertBuilder.setCancelable(true).setPositiveButton(
                "Update Timer",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });

        changeTimeDialogue = alertBuilder.create();
        changeTimeDialogue.show();
        changeTimeDialogue.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onClickUpdateTime(minute.getSelectedItemPosition(), second.getSelectedItemPosition());
                    }
                });
    }

    /**
     * Changes the timer duration to match the given minutes and seconds values.
     * @param minutes the number of minutes left on the timer.
     * @param seconds the number of seconds left on the timer.
     */
    private void onClickUpdateTime(int minutes, int seconds) {
        timerDuration = 0;
        timerDuration += minutes * 60000;
        timerDuration += seconds * 1000;
        timeRemaining = timerDuration + 1000; //Add one second because the timer rounds down.
        refreshTimer();
        time.setText(String.format("%01d:%02d", minutes, seconds));

        changeTimeDialogue.dismiss();
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

        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mp.isPlaying()) {
            mp.pause();
        }
    }

    /**
     * Changes the current topic to the previous one in the list. This effectively serves as a back button.
     * @param view the View that represents the button that is clicked.
     */
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
        timeRemaining = timerDuration + 1000;
        refreshTimer();
        currentTopicDataRef.setValue(topicNumber);
    }

    /**
     * Changes the current topic to the next one in the list. This effectively serves as a continue button.
     * @param view the View that represents the button that is clicked.
     */
    public void onClickNextTopic(View view) {
        // add the actions to the session topic
        saveTopicActions();

        topicNumber++;

        if (topicNumber >= session.getTopics().size()) {
            // No more topics, save and quit
            saveAndQuit();
        } else {
            setupNextTopic();
            timeRemaining = timerDuration + 1000;
            refreshTimer();
        }
        currentTopicDataRef.setValue(topicNumber);
    }

    /**
     * Saves all of the session data and starts the Main Activity.
     */
    private void saveAndQuit() {
        Log.d(SPRINT_TAG, "Topic # == " + topicNumber +
                "| getTopics.size() == " + session.getTopics().size());

        // get the date
        Date date = new Date();
        String strDateFormat = "yyyy/MM/dd HH:mm:ss"; // I changed 'hh' to 'HH' and removed the 'a'
        DateFormat dateFormat = new SimpleDateFormat(strDateFormat);
        String formattedDate= dateFormat.format(date);

        Log.d(SPRINT_TAG, "DATE = " + formattedDate);

        session.setDate(formattedDate);

        isMuted = true;
        timer.cancel();
        findViewById(R.id._loadProgress).setVisibility(View.VISIBLE);
        findViewById(R.id._nextTopicButton).setEnabled(false);
        findViewById(R.id._prevTopicButton).setEnabled(false);
        findViewById(R.id._stopTimeButton).setEnabled(false);
        findViewById(R.id._resetTimeButton).setEnabled(false);
        findViewById(R.id._playPauseTimeButton).setEnabled(false);
        isMenuDisabled = true;

        // clear the temp file
        SharedPreferences sp = this.getSharedPreferences(TEMP_SAVE_PREF, MODE_PRIVATE);
        sp.edit().clear().apply();

        // check to make sure it was cleared
        String sessionJson = sp.getString(CONTINUE_KEY, "no session");
        Log.d(SPRINT_TAG, "sessionJson === " + sessionJson);

        Log.i(SPRINT_TAG, "Temporary files cleared");

        if(saveLocal || (saveCloud && !isConnected())) {
            Save save = new SaveLocal(this);
            save.save(session);
            Log.i(SPRINT_TAG, "SaveAndQuit: Saved to Device");
            if (saveCloud && !saveLocal){
                Toast.makeText(getApplicationContext(),
                        "No internet connection. Saved to local device instead of cloud.",
                        Toast.LENGTH_LONG).show();
            }
        }

        if(saveCloud && isConnected()) {
            Save save = new SaveCloud(new WeakReference<>(this), userID);
            save.save(session);
            Log.i(SPRINT_TAG, "SaveAndQuit: Saved to Cloud");
        }else {
            Log.i(SPRINT_TAG, "Session saved");
            activityDataRef.setValue("MainActivity");
        }
    }

    /**
     * Sets the appropriate text for the next Topic
     */
    private void setupNextTopic() {
        // check whether or not to show the 'previous' button
        if (topicNumber > 0) {
            if(isHost)
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

    /**
     * Saves the data in the text box as the actions of the topic.
     */
    private void saveTopicActions() {
        EditText actionsTextView = findViewById(R.id._actionsMultiAutoCompleteTextView);
        session.getTopics().get(topicNumber).setActions(actionsTextView.getText().toString());
        actionsTextView.setText("");
    }

    /**
     * Toggles play and pause for the countdown timer.
     * @param v the View representing the button that was clicked.
     */
    public void playPause(View v) {
        if(!isPaused) {
            playPause.setImageResource(android.R.drawable.ic_media_play);
            isPaused = true;
            if (mp.isPlaying()) {
                mp.pause();
            }
        } else {
            playPause.setImageResource(android.R.drawable.ic_media_pause);
            isPaused = false;
            refreshTimer();
        }
    }

    /**
     * Resets the countdown timer to the starting time.
     * @param v the View representing the button that was clicked.
     */
    public void rewind(View v) {
        timeRemaining = timerDuration + 1000;
        refreshTimer();
        int minutes = Math.round(timerDuration / 60000);
        int seconds = Math.round(timerDuration % 60000 / 1000);
        time.setText(String.format("%01d:%02d", minutes, seconds));
    }

    /**
     * Sets the remaining time of the countdown timer to zero and stops it.
     * @param v the View representing the button that was clicked.
     */
    public void fastForward(View v) {
        timer.onFinish();
        timer.cancel();
        timeRemaining = 0;
        time.setText("0:00");
    }

    /**
     * Toggles the alarm between muted and not muted.
     * @param v the View representing the button that was clicked.
     */
    public void toggleAlarm(View v) {
        if(!isMuted) {
            toggleAlarm.setImageResource(android.R.drawable.ic_lock_silent_mode);
            isMuted = true;
            if (mp.isPlaying()) {
                mp.pause();
            }
        } else {
            toggleAlarm.setImageResource(android.R.drawable.ic_lock_silent_mode_off);
            isMuted = false;
        }
    }

    /**
     * Stops the previous countdown timer and replaces it with a new one which has the desired values.
     */
    private void refreshTimer() {
        if (timer != null) {
            timer.cancel();
            if (mp.isPlaying()) {
                mp.pause();
            }
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
}

