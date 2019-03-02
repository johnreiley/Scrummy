package com.threeguys.scrummy;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

public class ViewSessionActivity extends AppCompatActivity {

    private Session session;
    private RecyclerView recyclerView;
    private LoadTopicItemAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_session);

        adapter = new LoadTopicItemAdapter(session.getTopics());

        recyclerView = findViewById(R.id._viewSessionRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    public void display() {

    }
}
