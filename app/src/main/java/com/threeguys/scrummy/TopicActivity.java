package com.threeguys.scrummy;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.gson.Gson;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.threeguys.scrummy.MainActivity.ACTIVITY_KEY;
import static com.threeguys.scrummy.MainActivity.CONTINUE_KEY;
import static com.threeguys.scrummy.MainActivity.TEMP_SAVE_PREF;

public class TopicActivity extends AppCompatActivity {

    public static final String TOPIC_TAG = "TopicActivity";//TopicActivity.class.getSimpleName();
    private Session session;

    private ExpandableListView expandableListView;
    private TopicItemAdapter adapter;
    private HashMap<String, List<Topic>> childData;
    private List<String> groupData;
    private String username;
    private String userID;

    private DatabaseReference sessionDataRef;
    private DatabaseReference activityDataRef;

    AlertDialog addTopicDialogue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topic);
        Log.i(TOPIC_TAG, "TopicActivity Started");

        //String sessionTitle = (String)getIntent().getExtras().get(MainActivity.SESSION_TITLE_KEY);
        //Log.d(TOPIC_TAG, "Obtained new session title: " + sessionTitle);

        TextView sessionTitleHolder = findViewById(R.id._topicEntryTextView);

        String sessionJson = (String) getIntent().getExtras().get(MainActivity.SESSION_KEY);
        Gson gson = new Gson();
        session = gson.fromJson(sessionJson, Session.class);
        //session.setTitle(sessionTitle);

        groupData = new ArrayList<>(); // new change
        groupData.add("Good");
        groupData.add("Neutral");
        groupData.add("Bad");

        Log.i("Topic Count","Size: " + session.getTopics().size());

        expandableListView = findViewById(R.id._topicCategoryExpandableListView);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        username = preferences.getString("username","");
        Log.i(TOPIC_TAG, "username is: " + username);

        userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Setup Firebase database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        // Setup activity Firebase
        activityDataRef = database.getReference().child("users").child(userID).child("activity");
        activityDataRef.setValue("TopicActivity");
        activityDataRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String activity = dataSnapshot.getValue(String.class);

                switch (activity){
                    case "TopicActivity":
                        break;
                    case "VoteActivity":
                        onClickVote(getCurrentFocus());
                        break;
                    case "SprintActivity":
                        onClickVote(getCurrentFocus());
                        break;
                    case "MainActivity":
                        onClickVote(getCurrentFocus());
                        break;
                    default:
                        Log.wtf(TOPIC_TAG, "The activity in database is: " + activity);
                        break;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Failed to read value
                Log.w(TOPIC_TAG, "Failed to read value.", error.toException());
            }
        });
        // Setup session Firebase
        sessionDataRef = database.getReference().child("users").child(userID).child("session");
        sessionDataRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String value = dataSnapshot.getValue(String.class);

                if (value != null && value != ""){
                    Gson gson = new Gson();
                    session = gson.fromJson(value, Session.class);

                    TextView title = findViewById(R.id._topicEntryTextView);
                    title.setText(session.getTitle());

                    refreshAdapter();
                }

                Log.d(TOPIC_TAG, "FirebaseDatabase value is: " + value);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TOPIC_TAG, "Failed to read value.", error.toException());
            }
        });

        sessionTitleHolder.setText(session.getTitle());

        updateFirebaseSession();
        refreshAdapter();
    }

    @Override
    protected void onStop() {
        super.onStop();
        SharedPreferences sp = this.getSharedPreferences(TEMP_SAVE_PREF, MODE_PRIVATE);
        Gson gson = new Gson();

        String sessionJson = gson.toJson(session, Session.class);

        Log.d(TOPIC_TAG, "SESSION TITLE == " + session.getTitle());

        String activityJson = "TopicActivity";

        SharedPreferences.Editor editor = sp.edit();
        editor.putString(CONTINUE_KEY, sessionJson);
        editor.putString(ACTIVITY_KEY, activityJson);
        editor.apply();
    }

    /**
     * Deletes the selected topic from the session
     * @param category the category of the topic
     * @param index of the topic in its respective category
     */
    public void onClickDelete(int category, int index) {
        List<Topic> tl;
        switch(category) {
            case 0:
                tl = session.getGoodTopics();
                break;
            case 1:
                tl = session.getNeutralTopics();
                break;
            case 2:
                tl = session.getBadTopics();
                break;
            default:
                tl = new ArrayList<>();
                Toast.makeText(this,
                        "Can't find the Category this topic is in.", Toast.LENGTH_SHORT).show();
                break;
        }

        if (tl.size() <= index) {
            Toast.makeText(this,
                    "Can't find the Topic to add a vote to.", Toast.LENGTH_SHORT).show();
        }
        else {
            Log.i("Add Vote", "Found correct topic: " + tl.get(index).getTitle());

            int position = index;
            switch (category) {
                case 0:
                    position += session.getNeutralTopics().size();
                case 1:
                    position += session.getBadTopics().size();
                    break;
                case 2:
                default:
                    break;
            }

            List<Topic> topics = session.getTopics();
            topics.remove(position);
            session.setTopics(topics);

            refreshAdapter();
        }
    }

    /**
     * Moves to the next activity
     * @param view The "vote" button clicked
     */
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

    /**
     * Called when the plus button is clicked
     * @param view the plus button
     */
    public void onClickAddTopic(View view) {
        Log.d(TOPIC_TAG, "entered onclick add topic");
        View v = (LayoutInflater.from(this)).inflate(R.layout.add_topic, null);

        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
        alertBuilder.setView(v);

        final EditText topic = v.findViewById(R.id._topicInputEditText);
        final EditText name = v.findViewById(R.id._nameInputEditText);
        name.setText(username);
        final Spinner category = v.findViewById(R.id._categoryInputSpinner);

        alertBuilder.setCancelable(true).setPositiveButton(
                "Add Topic",
                new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        addTopicDialogue = alertBuilder.create();
        addTopicDialogue.show();
        addTopicDialogue.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(
                new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickFinishInput(topic, name, category);
            }
        });
    }

    /**
     * Allows for the editing of a topic in the session
     * @param category good, bad, or neutral
     * @param index the index number of the topic being edited
     */
    public void onClickEditTopic(int category, final int index) {
        Log.d(TOPIC_TAG, "entered onclick edit topic");
        List<Topic> tl;
        switch(category) {
            case 0:
                tl = session.getGoodTopics();
                break;
            case 1:
                tl = session.getNeutralTopics();
                break;
            case 2:
                tl = session.getBadTopics();
                break;
            default:
                tl = new ArrayList<>();
                Toast.makeText(this,
                        "Can't find the Category this topic is in.", Toast.LENGTH_SHORT).show();
                break;
        }

        if (tl.size() <= index) {
            Toast.makeText(this,
                    "Can't find the Topic to add a vote to.", Toast.LENGTH_SHORT).show();
        }
        else {
            Log.i("Add Vote", "Found correct topic: " + tl.get(index).getTitle());

            int position = index;
            switch (category) {
                case 0:
                    position += session.getNeutralTopics().size();
                case 1:
                    position += session.getBadTopics().size();
                    break;
                case 2:
                default:
                    break;
            }


            View v = (LayoutInflater.from(this)).inflate(R.layout.add_topic, null);

            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
            alertBuilder.setView(v);

            final EditText topic = v.findViewById(R.id._topicInputEditText);
            final EditText name = v.findViewById(R.id._nameInputEditText);
            final Spinner categoryS = v.findViewById(R.id._categoryInputSpinner);

            Topic t = session.getTopics().get(position);

            topic.setText(t.getTitle());
            name.setText(t.getUsername());
            switch (t.getCategory()) {
                case GOOD:
                    categoryS.setSelection(0);
                    break;
                case NEUTRAL:
                    categoryS.setSelection(1);
                    break;
                case BAD:
                    categoryS.setSelection(2);
                    break;
                default:
                    categoryS.setSelection(0);
                    break;
            }

            alertBuilder.setCancelable(true).setPositiveButton(
                    "Edit Topic",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });

            addTopicDialogue = alertBuilder.create();
            addTopicDialogue.show();
            final int finalPosition = position;
            addTopicDialogue.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            onClickEditFinish(topic, name, categoryS, finalPosition);
                        }
                    });
        }
    }

    /**
     * Called when finishing edits
     * @param topicET The topic title view
     * @param nameET The user's name view
     * @param categoryS the category spinner
     * @param position the position of the index of session
     */
    public void onClickEditFinish(EditText topicET, EditText nameET, Spinner categoryS, int position) {
        Topic t = session.getTopics().get(position);
        t.setTitle(topicET.getText().toString());
        t.setUsername(nameET.getText().toString());

        Topic.Category c = Topic.Category.NEUTRAL;
        if (categoryS.getSelectedItemPosition() == 0) {
            c = Topic.Category.GOOD;
        } else if (categoryS.getSelectedItemPosition() == 1) {
            c = Topic.Category.NEUTRAL;
        } else if (categoryS.getSelectedItemPosition() == 2) {
            c = Topic.Category.BAD;
        }

        t.setCategory(c);

        session.updateTopic(position, t);
        session.sortByCategory();

        addTopicDialogue.dismiss();

        updateFirebaseSession();
        refreshAdapter();
    }

    /**
     * Called when the topic is being added to the session
     * @param topicET the edit text holding the topic title
     * @param nameET the edit text holding the name of the user
     * @param categoryS the spinner holding the category
     */
    public void onClickFinishInput(EditText topicET, EditText nameET, Spinner categoryS) {
        Log.d(TOPIC_TAG, "entered onclick");
        // first make sure the text inputs aren't empty
        if (TextUtils.isEmpty(topicET.getText().toString()) ||
                TextUtils.isEmpty(nameET.getText().toString())) {
            Toast.makeText(this,
                    "Enter both a title and name before adding a new topic",
                    Toast.LENGTH_SHORT).show();
        } else {
            Log.d(TOPIC_TAG, "populating topic");
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
            Log.d(TOPIC_TAG, "topic added");

            addTopicDialogue.dismiss();

            updateFirebaseSession();
            refreshAdapter();
            Log.d(TOPIC_TAG, "adapter refreshed");
        }
    }

    /**
     * Resets the adapter to display the latest data.
     */
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

    /**
     * Pushes the user's session to the FireBase and updates continue key.
     */
    private void updateFirebaseSession() {
        Gson gson = new Gson();
        String sessionJson = gson.toJson(session, Session.class);
        sessionDataRef.setValue(sessionJson);
        Log.i(TOPIC_TAG, "Firebase Updated");

        String activityJson = "TopicActivity";

        SharedPreferences sp = this.getSharedPreferences(TEMP_SAVE_PREF, MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(CONTINUE_KEY, sessionJson);
        editor.putString(ACTIVITY_KEY, activityJson);
        editor.apply();
        Log.i(TOPIC_TAG, "Firebase Updated The Continue Button");

    }

    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }
}
