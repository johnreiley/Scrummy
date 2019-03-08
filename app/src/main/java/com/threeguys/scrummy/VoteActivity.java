package com.threeguys.scrummy;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ExpandableListView;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.threeguys.scrummy.MainActivity.ACTIVITY_KEY;
import static com.threeguys.scrummy.MainActivity.CONTINUE_KEY;
import static com.threeguys.scrummy.MainActivity.TEMP_SAVE_PREF;

public class VoteActivity extends AppCompatActivity {

    private Session session;
    private ExpandableListView expandableListView;
    private VoteItemAdapter adapter;
    private HashMap<String, List<Topic>> childData;
    private List<String> groupData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vote);

        String sessionJson = (String)getIntent().getExtras().get(MainActivity.SESSION_KEY);
        Gson gson = new Gson();
        session = gson.fromJson(sessionJson, Session.class);

        groupData = new ArrayList<>();
        groupData.add("Good");
        groupData.add("Neutral");
        groupData.add("Bad");

        childData = new HashMap<>();
        childData.put(groupData.get(0), session.getGoodTopics());
        childData.put(groupData.get(1), session.getNeutralTopics());
        childData.put(groupData.get(2), session.getBadTopics());

        adapter = new VoteItemAdapter(this, childData, groupData);

        expandableListView = findViewById(R.id._voteCategoryExpandableListView);
        expandableListView.setAdapter(adapter);
    }

    @Override
    protected void onStop() {
        super.onStop();

        SharedPreferences sp = this.getSharedPreferences(TEMP_SAVE_PREF, MODE_PRIVATE);
        Gson gson = new Gson();

        String sessionJson = gson.toJson(session, Session.class);
        String activityJson = "VoteActivity";

        SharedPreferences.Editor editor = sp.edit();
        editor.putString(CONTINUE_KEY, sessionJson);
        editor.putString(ACTIVITY_KEY, activityJson);
        editor.apply();
    }

    /**
     * Starts the SprintActivity activity
     */
    public void onClickStart() {
        session.sortByVote();
        // start the sprint activity
        Intent sprintIntent = new Intent(this, SprintActivity.class);
        // turn the session into a string
        Gson gson = new Gson();
        String sessionJson = gson.toJson(session);

        // add the session string to the intent
        sprintIntent.putExtra(MainActivity.SESSION_KEY, sessionJson);
        startActivity(sprintIntent);
    }

    /**
     * Adds a vote to specified Topic upon clicking
     * the plus button
     */
    public void onClickAddVote(int index) {
        // add a vote
    }

    /**
     * Subtracts a vote from specified Topic upon clicking
     * the minus button
     */
    public void onClickSubVote(int index) {
        // subtract a vote
    }

    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }
}
