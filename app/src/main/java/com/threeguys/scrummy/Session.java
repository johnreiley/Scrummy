package com.threeguys.scrummy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Session {

    private List<Topic> topics;

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
}
