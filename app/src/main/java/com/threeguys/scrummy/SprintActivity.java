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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sprint);

        session = (Session)getIntent().getExtras().get(MainActivity.SESSION_KEY);
    }

    public void onClickNextTopic(View view) {
        TextView currentTopic = findViewById(R.id._currentTopicTextView);
        Button nextTopic = findViewById(R.id._nextTopicButton);
        MultiAutoCompleteTextView actionsView = findViewById(R.id._actionsMultiAutoCompleteTextView);

    }
}
