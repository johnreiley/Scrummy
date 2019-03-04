package com.threeguys.scrummy;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.MultiAutoCompleteTextView;
import android.widget.TextView;

public class SprintActivity extends AppCompatActivity {

    Session session;
    private int topicNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sprint);

        // Initialize Member Data
        session = (Session)getIntent().getExtras().get(MainActivity.SESSION_KEY);
        topicNumber = 0;

        TextView currentTopic = findViewById(R.id._currentTopicTextView);
        Button nextTopic = findViewById(R.id._nextTopicButton);

        currentTopic.setText(session.getTopics().get(0).getTitle());
        // Is there more than one topic?
        if (session.getTopics().size() > 1) {
            nextTopic.setText(session.getTopics().get(1).getTitle());
        } else {
            nextTopic.setText("Save and Quit");
        }
    }

    public void onClickNextTopic(View view) {
        TextView currentTopic = findViewById(R.id._currentTopicTextView);
        Button nextTopic = findViewById(R.id._nextTopicButton);
        MultiAutoCompleteTextView actionsView = findViewById(R.id._actionsMultiAutoCompleteTextView);

        session.getTopics().get(topicNumber).setActions(actionsView.getText().toString());
        actionsView.setText("");

        topicNumber++;

        // No more topics, save and quit
        if (topicNumber > session.getTopics().size()) {
            //TODO save
            return;
        }

        // Go to next Topic
        currentTopic.setText(session.getTopics().get(topicNumber).getTitle());

        // Check if there is a nex topic
        if (topicNumber + 1 > session.getTopics().size()) {
            nextTopic.setText(session.getTopics().get(topicNumber + 1).getTitle());
        } else {
            nextTopic.setText("Save and Quit");
        }
    }
}
