package com.threeguys.scrummy;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.threeguys.scrummy.MainActivity.ACTIVITY_KEY;
import static com.threeguys.scrummy.MainActivity.CONTINUE_KEY;
import static com.threeguys.scrummy.MainActivity.TEMP_SAVE_PREF;

public class TopicActivity extends AppCompatActivity {

    public static final String TOPIC_TAG = TopicActivity.class.getSimpleName();
    private Session session;

    private ExpandableListView expandableListView;
    private TopicItemAdapter adapter;
    private HashMap<String, List<Topic>> childData;
    private List<String> groupData;

    private EditText topicET;
    private EditText nameET;
    private Spinner categoryS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topic);
        Log.i(TOPIC_TAG, "TopicActivity Started");

        LinearLayout linearLayout = (LinearLayout)findViewById(R.id._snackbarLayout);
        linearLayout.setVisibility(View.GONE);

        topicET = findViewById(R.id._topicInputEditText);
        nameET = findViewById(R.id._nameInputEditText);
        categoryS = findViewById(R.id._categoryInputSpinner);

        session = new Session();

        groupData = new ArrayList<>(); // new change
        groupData.add("Good");
        groupData.add("Neutral");
        groupData.add("Bad");

        // ------------ TEST CODE ------------ //
        Topic t1 = new Topic();
        t1.setTitle("Good Test");
        t1.setUsername("Bretton");
        t1.setCategory(Topic.Category.GOOD);

        Topic t2 = new Topic();
        t2.setTitle("Neutral Test");
        t2.setUsername("Bretton");
        t2.setCategory(Topic.Category.NEUTRAL);

        Topic t3 = new Topic();
        t3.setTitle("Bad Test");
        t3.setUsername("Bretton");
        t3.setCategory(Topic.Category.BAD);

        session.addTopic(t1);
        session.addTopic(t2);
        session.addTopic(t3);

        Log.i("Topic Count","Size: " + session.getTopics().size());

        // ------------------------------------ //

        expandableListView = findViewById(R.id._topicCategoryExpandableListView);

        refreshAdapter();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (session.getTopics().size() > 0) {
            SharedPreferences sp = this.getSharedPreferences(TEMP_SAVE_PREF, MODE_PRIVATE);
            Gson gson = new Gson();

            String sessionJson = gson.toJson(session, Session.class);
            String activityJson = "TopicActivity";

            SharedPreferences.Editor editor = sp.edit();
            editor.putString(CONTINUE_KEY, sessionJson);
            editor.putString(ACTIVITY_KEY, activityJson);
            editor.apply();
        }
    }

    public void onClickVote(View view) {

        Log.d(TOPIC_TAG, "session.getTopics().size() == " + session.getTopics().size());

        if (session.getTopics() != null && session.getTopics().size() > 0) {
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
        LinearLayout linearLayout = findViewById(R.id._snackbarLayout);
        if (linearLayout.getVisibility() == View.VISIBLE) {
            linearLayout.setVisibility(View.GONE);
            // reset the text input values to default
            topicET.setText("");
            nameET.setText("");
            categoryS.setSelection(0);
        } else {
            linearLayout.setVisibility(View.VISIBLE);
        }
    }

    public void onClickFinishInput(View view) {

        // first make sure the text inputs aren't empty
        if (topicET.getText().toString().equals("") || nameET.getText().toString().equals("")) {
            Toast.makeText(this,
                    "Enter both a title and name before adding a new topic",
                    Toast.LENGTH_SHORT).show();
        } else {
            // populate the topic with the text input values
            Topic topic = new Topic();
            topic.setTitle(topicET.getText().toString());
            topic.setUsername(nameET.getText().toString());

            Topic.Category c = Topic.Category.NEUTRAL;
            if (categoryS.getSelectedItemPosition() == 0) {
                c = Topic.Category.GOOD;
            } else if (categoryS.getSelectedItemPosition() == 1) {
                c = Topic.Category.NEUTRAL;
            } else if (categoryS.getSelectedItemPosition() == 2) {
                c = Topic.Category.BAD;
            }
            topic.setCategory(c);

            session.addTopic(topic);

            LinearLayout linearLayout = findViewById(R.id._snackbarLayout);
            linearLayout.setVisibility(View.GONE);

            // reset the text input values to default
            topicET.setText("");
            nameET.setText("");
            categoryS.setSelection(0);

            refreshAdapter();
        }
    }

    private void refreshAdapter() {
        childData = new HashMap<>();

        childData.put(groupData.get(0),session.getGoodTopics());
        childData.put(groupData.get(1),session.getNeutralTopics());
        childData.put(groupData.get(2),session.getBadTopics());

        if(adapter == null) {
            adapter = new TopicItemAdapter(this, childData, groupData);
            expandableListView.setAdapter(adapter);
            expandableListView.expandGroup(0);
            expandableListView.expandGroup(1);
            expandableListView.expandGroup(2);
        }
        else
        {
            Parcelable recyclerViewState = expandableListView.onSaveInstanceState();

            adapter.setCategories(groupData);
            adapter.setTopics(childData);
            adapter.notifyDataSetChanged();

            expandableListView.onRestoreInstanceState(recyclerViewState);
        }
    }

    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }
}
