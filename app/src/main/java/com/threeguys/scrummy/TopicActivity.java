package com.threeguys.scrummy;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;

public class TopicActivity extends AppCompatActivity {

    private Session session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topic);

        LinearLayout linearLayout = (LinearLayout)findViewById(R.id._snackbarLayout);
        linearLayout.setVisibility(View.GONE);

        session = new Session();
    }

    public void onClickVote(View view) {
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
    }

    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }
}
