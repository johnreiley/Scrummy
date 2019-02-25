package com.threeguys.scrummy;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

public class TopicActivity extends AppCompatActivity {

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


        LinearLayout linearLayout = (LinearLayout)findViewById(R.id._snackbarLayout);
        linearLayout.setVisibility(View.INVISIBLE);
    }
}
