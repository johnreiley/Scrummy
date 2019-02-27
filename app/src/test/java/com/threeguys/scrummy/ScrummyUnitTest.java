package com.threeguys.scrummy;

import org.junit.Test;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

public class ScrummyUnitTest {

    @Test
    public void VoteActivityTest() {
        System.out.println("*** Begin VoteActivity Tests ***");

        VoteActivity activity = new VoteActivity();
        Session session = new Session();
        List<Topic> topics = new ArrayList<>();

        Topic t = new Topic();
        t.setTitle("Creating Unit Tests for Scrummy");
        t.setCategory(Topic.Category.NEUTRAL);
        t.setUsername("Mr. Awesome Coder");
        t.addVote();
        topics.add(t);

        t = new Topic();
        t.setTitle("Creating Second Topic for Unit Tests");
        t.setCategory(Topic.Category.BAD);
        t.setUsername("You Don't Know Me");
        t.addVote();
        t.addVote();
        t.addVote();
        topics.add(t);

        t = new Topic();
        t.setTitle("Creating Last Topic for Unit Tests");
        t.setCategory(Topic.Category.GOOD);
        t.setUsername("Zielke");
        t.subVote();
        t.subVote();
        t.subVote();
        topics.add(t);

        session.setTopics(topics);
        activity.setNewSession(session);

        Session activitySession = activity.getNewSession();
        List<Topic> activityTopics = activitySession.getTopics();

        //*** Initialized Data Tests ***//
        //Check each topic for correct starting values
        assert(activityTopics.get(0) != null);
        assert(activityTopics.get(0).getTitle().equals("Creating Unit Tests for Scrummy"));
        assert(activityTopics.get(0).getCategory() == Topic.Category.NEUTRAL);
        assert(activityTopics.get(0).getUsername().equals("Mr. Awesome Coder"));
        assert(activityTopics.get(0).getVotes() == 1);

        assert(activityTopics.get(1) != null);
        assert(activityTopics.get(1).getTitle().equals("Creating Second Topic for Unit Tests"));
        assert(activityTopics.get(1).getCategory() == Topic.Category.BAD);
        assert(activityTopics.get(1).getUsername().equals("You Don't Know Me"));
        assert(activityTopics.get(1).getVotes() == 3);

        assert(activityTopics.get(2) != null);
        assert(activityTopics.get(2).getTitle().equals("Creating Last Topic for Unit Tests"));
        assert(activityTopics.get(2).getCategory() == Topic.Category.GOOD);
        assert(activityTopics.get(2).getUsername().equals("Zielke"));
        assert(activityTopics.get(2).getVotes() == 0);

        System.out.println("Passed Initialized Data Tests");

        //*** onClickAddVote Tests ***//

        //Add votes to each topic
        activity.onClickAddVote(0);
        activity.onClickAddVote(1);
        activity.onClickAddVote(2);
        activity.onClickAddVote(2);

        activitySession = activity.getNewSession();
        activityTopics = activitySession.getTopics();

        assert(activityTopics.get(0).getVotes() == 2);
        assert(activityTopics.get(1).getVotes() == 4);
        assert(activityTopics.get(2).getVotes() == 2);

        System.out.println("Passed onClickAddVote Tests");

        //*** onClickSubVote Tests ***//

        //Subtract votes from each topic
        activity.onClickSubVote(0);
        activity.onClickSubVote(0);
        activity.onClickSubVote(0);
        activity.onClickSubVote(0);
        activity.onClickSubVote(1);
        activity.onClickSubVote(1);
        activity.onClickSubVote(2);

        activitySession = activity.getNewSession();
        activityTopics = activitySession.getTopics();

        assert(activityTopics.get(0).getVotes() == 0);
        assert(activityTopics.get(1).getVotes() == 2);
        assert(activityTopics.get(2).getVotes() == 1);

        System.out.println("Passed onClickAddVote Tests");

        //*** onClickStart Tests ***//

        //This should start the next activity
        //And sort the list in order by highest vote
        activity.onClickStart();

        activitySession = activity.getNewSession();
        activityTopics = activitySession.getTopics();

        //Check each topic for correct order and values
        assert(activityTopics.get(0).getTitle().equals("Creating Second Topic for Unit Tests"));
        assert(activityTopics.get(0).getCategory() == Topic.Category.BAD);
        assert(activityTopics.get(0).getUsername().equals("You Don't Know Me"));
        assert(activityTopics.get(0).getVotes() == 2);

        assert(activityTopics.get(1).getTitle().equals("Creating Last Topic for Unit Tests"));
        assert(activityTopics.get(1).getCategory() == Topic.Category.GOOD);
        assert(activityTopics.get(1).getUsername().equals("Zielke"));
        assert(activityTopics.get(1).getVotes() == 1);

        assert(activityTopics.get(2).getTitle().equals("Creating Unit Tests for Scrummy"));
        assert(activityTopics.get(2).getCategory() == Topic.Category.NEUTRAL);
        assert(activityTopics.get(2).getUsername().equals("Mr. Awesome Coder"));
        assert(activityTopics.get(2).getVotes() == 0);

        System.out.println("Passed onClickStart Tests");
        System.out.println("*** Passed all VoteActivity Tests ***");
    }
}
