package com.threeguys.scrummy;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import com.threeguys.scrummy.R;
import java.util.ArrayList;
import java.util.List;

public class LoadActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {

    List<Session> sessions;
    private RecyclerView recyclerView;
    private LoadSessionItemAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load);

        sessions = new ArrayList<>();

        adapter = new LoadSessionItemAdapter(sessions);

        recyclerView = findViewById(R.id._loadSessionRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    public void onClickSession(View v) {

    }

    public void onClickPopup(View v) {
        PopupMenu popup = new PopupMenu(this, v);
        popup.setOnMenuItemClickListener(this);

        //popup.inflate(R.menu.load_item_popup);
        popup.show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            //case R.id._deletePopupItem:
                //return true;
        }
        return false;
    }
}
