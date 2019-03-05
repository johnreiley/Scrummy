package com.threeguys.scrummy;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ExpandableListView;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class VoteActivity extends AppCompatActivity {

    private Session newSession;
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
        newSession = gson.fromJson(sessionJson, Session.class);

        groupData = new ArrayList<>();
        groupData.add("Good");
        groupData.add("Neutral");
        groupData.add("Bad");

        childData = new HashMap<>();
        childData.put(groupData.get(0),newSession.getGoodTopics());
        childData.put(groupData.get(1),newSession.getNeutralTopics());
        childData.put(groupData.get(2),newSession.getBadTopics());

        adapter = new VoteItemAdapter(this, childData, groupData);

        expandableListView = findViewById(R.id._voteCategoryExpandableListView);
        expandableListView.setAdapter(adapter);
    }

    /**
     * Starts the SprintActivity activity
     */
    public void onClickStart() {
        newSession.sortByVote();
        // start the sprint activity
        Intent sprintIntent = new Intent(this, SprintActivity.class);
        // turn the session into a string
        Gson gson = new Gson();
        String sessionJson = gson.toJson(newSession);

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

    public Session getNewSession() {
        return newSession;
    }

    public void setNewSession(Session newSession) {
        this.newSession = newSession;
    }
}
