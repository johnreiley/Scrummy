package com.threeguys.scrummy;

import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * This class represents a single retrospective meeting as a part of the Scrum Software Development
 * Methodology.
 */
public class Session {

    private static final String SESSION_TAG = Session.class.getSimpleName();
    private List<Topic> topics;
    private String title;
    private String date;

    /**
     * The default constructor creates an empty list of topics and sets the date to "".
     */
    Session(){
        this.topics = new ArrayList<>();
        date = "";
    }

    /**
     * Sorts all the topics by the number of votes they all have from highest to lowest count.
     */
    public void sortByVote(){
        Collections.sort(topics, new Comparator<Topic>() {
            @Override public int compare(Topic t1, Topic t2) {
                return t2.getVotes() - t1.getVotes(); // Descending
            }
        });
    }

    /**
     * Sorts all the topics by their category giving preference to bad, then neutral, then good.
     */
    public void sortByCategory() {
        List<Topic> temp = new ArrayList<>();
        temp.addAll(getBadTopics());
        temp.addAll(getNeutralTopics());
        temp.addAll(getGoodTopics());
        this.topics = temp;
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
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Finds all of the good topics in the topics list.
     * @return tl, a list of good topics
     */
    public List<Topic> getGoodTopics() {

        List<Topic> tl = new ArrayList<>();
        for (Topic t : topics ) {
            if (t.getCategory() == Topic.Category.GOOD) {
                Log.d(SESSION_TAG, "GoodTopics found: " + t.getTitle());
                tl.add(t);
            }
        }
        return tl;
    }

    /**
     * Finds all of the neutral topics in the topics list.
     * @return tl, a list of neutral topics
     */
    public List<Topic> getNeutralTopics() {

        List<Topic> tl = new ArrayList<>();
        for (Topic t : topics ) {
            if (t.getCategory() == Topic.Category.NEUTRAL) {
                Log.d(SESSION_TAG, "NeutralTopics found: " + t.getTitle());
                tl.add(t);
            }
        }
        return tl;
    }

    /**
     * Finds all of the bad topics in the topics list
     * @return tl, a list of bad topics.
     */
    public List<Topic> getBadTopics() {

        List<Topic> tl = new ArrayList<>();
        for (Topic t : topics ) {
            if (t.getCategory() == Topic.Category.BAD) {
                Log.d(SESSION_TAG, "BadTopics found: " + t.getTitle());
                tl.add(t);
            }
        }
        return tl;
    }

    /**
     * Replaces the topic at the given position with the given topic.
     * @param position, The index of the topic in the array of topics.
     * @param topic, The topic with the updated data.
     */
    public void updateTopic(int position, Topic topic) {
        topics.set(position, topic);
    }
}
