package com.threeguys.scrummy;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

public class LoadActivity extends AppCompatActivity {

    List<Session> sessions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load);

        sessions = new ArrayList<>();
    }

    public void onClickSession() {

    }
}
