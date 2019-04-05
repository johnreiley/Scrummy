package com.threeguys.scrummy;

import android.util.Log;

/**
 * This class represents a single topic of discussion as part of a retrospective meeting which is a
 * part of the Scrum Software Development Methodology.
 */
public class Topic {

    private String title;
    private String actions;
    private String username;
    private Category category;
    private int votes;

    /**
     * Defines three possible categories representing emotion.
     */
    public enum Category {GOOD, NEUTRAL, BAD}

    Topic() {
        title = "";
        username = "";
        votes = 0;
        actions = "";
        category = Category.NEUTRAL;
    }

    Topic(String title, String username, int votes, String actions, Category category) {
        this.title = title;
        this.username = username;
        this.votes = votes;
        this.actions = actions;
        this.category = category;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getActions() {
        return actions;
    }

    public void setActions(String actions) {
        this.actions = actions;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public int getVotes() {
        return votes;
    }

    public void setVotes(int votes) {
        this.votes = votes;
    }

    public void addVote() {
        this.votes += 1;
        //Log.i("Topic addVote", "Vote added");
    }

    public void subVote() {
        if (votes > 0) {
            this.votes -= 1;
            //Log.i("Topic subVote", "Vote subtracted");
        }
        //Log.i("Topic subVote", "Votes already at 0");
    }
}
