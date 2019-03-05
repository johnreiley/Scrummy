package com.threeguys.scrummy;

import org.junit.Test;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class ClassesUnitTest {


    @Test
    public void topicConstructorTest() {
        Topic t = new Topic();
        assertEquals(0, t.getVotes());
        assertEquals("No actions", t.getActions());
    }

    @Test
    public void topicVotesShouldNotGoBelowZero() {
        Topic t = new Topic();
        t.subVote();
        assertEquals(0, t.getVotes());
    }

    @Test
    public void sessionConstructorTest() {
        Session s = new Session();
        assertEquals(0, s.getTopics().size());
    }

    @Test
    public void sessionShouldSortByVote() {
        Session s = new Session();

        Topic t1 = new Topic();
        t1.setVotes(1);

        Topic t2 = new Topic();
        t2.setVotes(2);

        Topic t3 = new Topic();
        t3.setVotes(3);

        // put the topics in from greatest to least
        s.addTopic(t3);
        s.addTopic(t2);
        s.addTopic(t1);

        s.sortByVote();
        // now it should be sorted from least to greatest
        assertEquals(1, s.getTopics().get(0).getVotes());
        assertEquals(2, s.getTopics().get(1).getVotes());
        assertEquals(3, s.getTopics().get(2).getVotes());
    }


}
