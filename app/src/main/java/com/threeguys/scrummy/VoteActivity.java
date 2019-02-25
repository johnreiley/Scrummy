package com.threeguys.scrummy;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class VoteActivity extends AppCompatActivity {

    private Session newSession;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vote);
    }

    /**
     * Starts the SprintActivity activity
     */
    public void onClickStart() {
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
