package com.threeguys.scrummy;

import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Session {

    private List<Topic> topics;
    private String Date;

    Session(){
        topics = new ArrayList<>();
        Date = "No Date";
    }

    public void sortByVote(){
        Collections.sort(topics, new Comparator<Topic>() {
            @Override public int compare(Topic t1, Topic t2) {
                return t2.getVotes() - t1.getVotes(); // Descending
            }
        });
    }

    public void sortByCategory() {
        List<Topic> temp = new ArrayList<>();
        temp.addAll(getBadTopics());
        temp.addAll(getNeutralTopics());
        temp.addAll(getGoodTopics());
        topics = temp;
    }

    public List<Topic> getTopics() {
        return topics;
    }

    public void setTopics(List<Topic> topics) {
        this.topics = topics;
        sortByCategory();
    }

    public void addTopic(Topic t) {
        topics.add(t);
        sortByCategory();
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    /**
     * finds all of the good topics in the topics list
     * @return tl, a list of good topics
     */
    public List<Topic> getGoodTopics() {

        List<Topic> tl = new ArrayList<>();
        for (Topic t : topics ) {
            if (t.getCategory() == Topic.Category.GOOD) {
                //Log.i("Good Topic Loop", "Found a good one: " + t.getTitle());
                tl.add(t);
            }
        }
        return tl;
    }

    /**
     * finds all of the neutral topics in the topics list
     * @return tl, a list of neutral topics
     */
    public List<Topic> getNeutralTopics() {

        List<Topic> tl = new ArrayList<>();
        for (Topic t : topics ) {
            if (t.getCategory() == Topic.Category.NEUTRAL) {
                //Log.i("Neutral Topic Loop", "Found a neutral one: " + t.getTitle());
                tl.add(t);
            }
        }
        return tl;
    }

    /**
     * finds all of the bad topics in the topics list
     * @return tl, a list of bad topics
     */
    public List<Topic> getBadTopics() {

        List<Topic> tl = new ArrayList<>();
        for (Topic t : topics ) {
            if (t.getCategory() == Topic.Category.BAD) {
                //Log.i("Bad Topic Loop", "Found a bad one: " + t.getTitle());
                tl.add(t);
            }
        }
        return tl;
    }

    public void updateTopic(int position, Topic t) {
        topics.set(position, t);
    }
}
