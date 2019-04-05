package com.threeguys.scrummy;

import android.content.Context;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;

import org.junit.Test;
import static org.junit.Assert.*;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class ClassesUnitTest {

    private final String DUMMY_UID = "Mt2aEswgpATrnT9ExmySWxcPli12";

    @Test
    public void topicConstructorTest() {
        Topic t = new Topic();
        assertEquals(0, t.getVotes());
        assertEquals("", t.getActions());
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
        s.addTopic(t1);
        s.addTopic(t2);
        s.addTopic(t3);

        s.sortByVote();
        // now it should be sorted from greatest to least
        assertEquals(3, s.getTopics().get(0).getVotes());
        assertEquals(2, s.getTopics().get(1).getVotes());
        assertEquals(1, s.getTopics().get(2).getVotes());
    }


    // THIS ISN'T WORKING BECAUSE OF FIREBASE STUFF !!
/*

    //----------- Test the SaveCloud class ------------//
    @Test
    public void loadCloudAndSaveCloud() {
        FirebaseApp firebaseApp = FirebaseApp.getInstance();
        FirebaseAuth FireAuth = FirebaseAuth.getInstance();
        FireAuth.signInWithEmailAndPassword("email@dummy.com", "UnitTestAccount");
        FirebaseStorage FireStore = FirebaseStorage.getInstance();

        Session testSession = new Session();
        Save saveCloud = new SaveCloud(new WeakReference<>(new SprintActivity()), DUMMY_UID);
        // reset the sessions
        saveCloud.save(testSession);

        // populate the session with values
        testSession.setTitle("Test Session");
        testSession.addTopic(new Topic(
                "Topic 1",
                "User",
                1,
                "Test",
                Topic.Category.GOOD
        ));

        saveCloud.save(testSession);

        // now load the session back and check
        Load loadCloud = new LoadCloud(new WeakReference<>(new LoadActivity()), DUMMY_UID);

        SessionList testSessionList = loadCloud.load(new LoadActivity().getApplicationContext());
        testSession = new Session();
        testSession = testSessionList.getList().get(0);

        assertEquals("Test Session", testSession.getTitle());
        assertEquals("Topic 1", testSession.getTopics().get(0).getTitle());
        assertEquals("User", testSession.getTopics().get(0).getUsername());
        assertEquals(1, testSession.getTopics().get(0).getVotes());
        assertEquals("Test", testSession.getTopics().get(0).getActions());
        assertEquals(Topic.Category.GOOD, testSession.getTopics().get(0).getCategory());
    }

    @Test
    public void updateCloudShouldUpdateSessionList() {
        Session testSession = new Session();

        // populate the session with values
        testSession.setTitle("Test Session");
        testSession.addTopic(new Topic(
                "Topic 2",
                "User",
                1,
                "Test",
                Topic.Category.BAD
        ));

        Save saveCloud = new SaveCloud(new WeakReference<>(new SprintActivity()), DUMMY_UID);
        List<Session> sessions = new ArrayList<>();
        sessions.add(testSession);
        saveCloud.update(sessions);

        // now load the session back and check
        Load loadCloud = new LoadCloud(new WeakReference<>(new LoadActivity()), DUMMY_UID);

        SessionList testSessionList = loadCloud.load(new LoadActivity().getApplicationContext());
        testSession = new Session();
        testSession = testSessionList.getList().get(1);

        assertEquals("Test Session", testSession.getTitle());
        assertEquals("Topic 2", testSession.getTopics().get(0).getTitle());
        assertEquals("User", testSession.getTopics().get(0).getUsername());
        assertEquals(1, testSession.getTopics().get(0).getVotes());
        assertEquals("Test", testSession.getTopics().get(0).getActions());
        assertEquals(Topic.Category.BAD, testSession.getTopics().get(0).getCategory());
    }
    // ------------------------------------------------//

*/

}
