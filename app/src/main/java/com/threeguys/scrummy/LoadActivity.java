package com.threeguys.scrummy;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class LoadActivity extends AppCompatActivity {

    List<Session> sessions;
    private RecyclerView recyclerView;
    private LoadSessionItemAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load);

        sessions = new ArrayList<>();

        //TEST Dummy code
        Topic t1 = new Topic();
        t1.setTitle("Good Test1");
        t1.setUsername("Bretton");
        t1.setCategory(Topic.Category.GOOD);

        Topic t2 = new Topic();
        t2.setTitle("Neutral Test1");
        t2.setUsername("Bretton");
        t2.setCategory(Topic.Category.NEUTRAL);

        Topic t3 = new Topic();
        t3.setTitle("Bad Test1");
        t3.setUsername("Bretton");
        t3.setCategory(Topic.Category.BAD);

        Session session1 = new Session();
        session1.setDate("First");
        session1.addTopic(t1);
        session1.addTopic(t2);
        session1.addTopic(t3);

        Topic t4 = new Topic();
        t4.setTitle("Good Test2");
        t4.setUsername("Bretton");
        t4.setCategory(Topic.Category.GOOD);

        Topic t5 = new Topic();
        t5.setTitle("Neutral Test2");
        t5.setUsername("Bretton");
        t5.setCategory(Topic.Category.NEUTRAL);

        Topic t6 = new Topic();
        t6.setTitle("Bad Test2");
        t6.setUsername("Bretton");
        t6.setCategory(Topic.Category.BAD);

        Session session2 = new Session();
        session2.setDate("Second");
        session2.addTopic(t4);
        session2.addTopic(t5);
        session2.addTopic(t6);

        sessions.add(session1);
        sessions.add(session2);
        //End dummy code

        adapter = new LoadSessionItemAdapter(this, sessions);

        recyclerView = findViewById(R.id._loadSessionRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    public void onClickSession(int index) {
        Log.i("onClickSession", "Session clicked: " + sessions.get(index).getDate());
    }
}
