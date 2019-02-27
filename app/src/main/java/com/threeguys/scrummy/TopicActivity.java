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
        linearLayout.setVisibility(View.INVISIBLE);
    }

    public void onClickVote(View view) {
    }

    public void onClickAddTopic(View view) {
        LinearLayout linearLayout = (LinearLayout)findViewById(R.id._snackbarLayout);
        if(linearLayout.getVisibility() == View.VISIBLE)
        {
            linearLayout.setVisibility(View.INVISIBLE);
        }
        if(linearLayout.getVisibility() == View.INVISIBLE)
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
        categoryS.getSelectedItemPosition();
        //TODO get the selected category
        Topic.Category c = Topic.Category.NEUTRAL;
        topic.setCategory(c);

        session.addTopic(topic);

        LinearLayout linearLayout = (LinearLayout)findViewById(R.id._snackbarLayout);
        linearLayout.setVisibility(View.INVISIBLE);
    }

    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }
}
