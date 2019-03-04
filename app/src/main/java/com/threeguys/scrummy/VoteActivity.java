package com.threeguys.scrummy;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ExpandableListView;

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

        groupData.add("Good");
        groupData.add("Neutral");
        groupData.add("Bad");

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
