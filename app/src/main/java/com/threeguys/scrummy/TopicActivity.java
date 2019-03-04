package com.threeguys.scrummy;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.HashMap;
import java.util.List;

public class TopicActivity extends AppCompatActivity {

    public static final String TOPIC_TAG = TopicActivity.class.getSimpleName();
    private Session session;
    private ExpandableListView expandableListView;
    private TopicItemAdapter adapter;
    private HashMap<String, List<Topic>> childData;
    private List<String> groupData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topic);

        LinearLayout linearLayout = (LinearLayout)findViewById(R.id._snackbarLayout);
        linearLayout.setVisibility(View.GONE);

        session = new Session();

        groupData.add("Good");
        groupData.add("Neutral");
        groupData.add("Bad");

        refreshAdapter();
    }

    public void onClickVote(View view) {

        Log.d(TOPIC_TAG, "session.getTopics().size() == " + session.getTopics().size());

        if (session.getTopics() != null && session.getTopics().size() >= 0) {
            Intent voteIntent = new Intent(this, VoteActivity.class);

            // turn the session into a string
            Gson gson = new Gson();
            String sessionJson = gson.toJson(session);

            // add the session string to the intent
            voteIntent.putExtra(MainActivity.SESSION_KEY, sessionJson);
            startActivity(voteIntent);

        } else {
            Toast.makeText(this,
                    "Please enter at least one topic before voting", Toast.LENGTH_SHORT).show();
        }
    }

    public void onClickAddTopic(View view) {
        LinearLayout linearLayout = (LinearLayout)findViewById(R.id._snackbarLayout);
        if(linearLayout.getVisibility() == View.VISIBLE)
        {
            linearLayout.setVisibility(View.GONE);
        }
        else
        {
            linearLayout.setVisibility(View.VISIBLE);
        }
    }

    public void onClickFinishInput(View view) {
        EditText topicET = findViewById(R.id._topicInputEditText);
        EditText nameET = findViewById(R.id._nameInputEditText);
        Spinner categoryS = findViewById(R.id._categoryInputSpinner);

        Topic topic = new Topic();
        topic.setTitle(topicET.getText().toString());
        topic.setUsername(nameET.getText().toString());

        Topic.Category c = Topic.Category.NEUTRAL;
        if (categoryS.getSelectedItemPosition() == 0)
        {
            c = Topic.Category.BAD;
        }
        else if (categoryS.getSelectedItemPosition() == 1)
        {
            c = Topic.Category.NEUTRAL;
        }
        else if (categoryS.getSelectedItemPosition() == 2)
        {
            c = Topic.Category.GOOD;
        }
        topic.setCategory(c);

        session.addTopic(topic);

        LinearLayout linearLayout = (LinearLayout)findViewById(R.id._snackbarLayout);
        linearLayout.setVisibility(View.GONE);

        refreshAdapter();
    }

    private void refreshAdapter() {
        childData = new HashMap<>();

        childData.put(groupData.get(0),session.getGoodTopics());
        childData.put(groupData.get(1),session.getNeutralTopics());
        childData.put(groupData.get(2),session.getBadTopics());

        adapter = new TopicItemAdapter(this, childData, groupData);

        expandableListView = findViewById(R.id._topicCategoryExpandableListView);
        expandableListView.setAdapter(adapter);
    }

    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }
}
