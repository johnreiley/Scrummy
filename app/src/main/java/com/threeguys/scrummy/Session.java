package com.threeguys.scrummy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Session {

    private List<Topic> topics;
    private String Date;

    Session(){
        topics = new ArrayList<>();
    }

    public void sortByVote(){
        Collections.sort(topics, new Comparator<Topic>() {
            @Override public int compare(Topic t1, Topic t2) {
                return t1.getVotes() - t2.getVotes(); // Ascending
            }
        });
    }

    public List<Topic> getTopics() {
        return topics;
    }

    public void setTopics(List<Topic> topics) {
        this.topics = topics;
    }

    public void addTopic(Topic t) {
        topics.add(t);
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
            if (t.getCategory().equals(Topic.Category.GOOD)) {
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
            if (t.getCategory().equals(Topic.Category.NEUTRAL)) {
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
            if (t.getCategory().equals(Topic.Category.BAD)) {
                tl.add(t);
            }
        }
        return tl;
    }
}
