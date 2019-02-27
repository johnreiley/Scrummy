package com.threeguys.scrummy;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

public class VoteActivity extends AppCompatActivity {

    private Session newSession;
    private RecyclerView recyclerView;
    private VoteItemAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vote);

        adapter = new VoteItemAdapter(newSession.getTopics());

        recyclerView = findViewById(R.id._voteItemRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
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
    public void onClickAddVote() {
        // add a vote
    }

    /**
     * Subtracts a vote from specified Topic upon clicking
     * the minus button
     */
    public void onClickSubVote() {
        // subtract a vote
    }
}
