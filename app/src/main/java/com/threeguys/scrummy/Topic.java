package com.threeguys.scrummy;

public class Topic {

    private String title;
    private String actions;
    private String username;
    private Category category;
    private int votes;

    public enum Category {GOOD, NEUTRAL, BAD}

    Topic() {
        votes = 0;
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

    public void addVote() {
        this.votes += 1;
    }

    public void subVote() {
        if (votes > 0) {
            this.votes -= 1;
        }
    }
}
