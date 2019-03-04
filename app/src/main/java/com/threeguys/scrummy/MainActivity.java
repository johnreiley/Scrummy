package com.threeguys.scrummy;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    static final String SESSION_KEY =  "SESSION_KEY";
    public static final String MAIN_TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    public void onClickNew(View view) {
        // start the topic activity
        Intent newIntent = new Intent(this, TopicActivity.class);
        startActivity(newIntent);
    }

    public void onClickContinue(View view) {
        // continue the last saved session
    }

    public void onClickLoad(View view) {
        // start the load activity
        Intent loadIntent = new Intent(this, LoadActivity.class);
        startActivity(loadIntent);
    }
}
