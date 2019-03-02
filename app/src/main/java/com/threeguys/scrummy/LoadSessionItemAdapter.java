package com.threeguys.scrummy;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

public class LoadSessionItemAdapter extends RecyclerView.Adapter<LoadSessionItemAdapter.LoadSessionItemsViewHolder> {

    private List<Session> sessions;

    public LoadSessionItemAdapter(List<Session> sessions) {
        this.sessions = sessions;
    }

    @Override
    public LoadSessionItemAdapter.LoadSessionItemsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.vote_item, parent, false);
        return new LoadSessionItemAdapter.LoadSessionItemsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(LoadSessionItemAdapter.LoadSessionItemsViewHolder holder, int position) {
        holder.sessionDate.setText(sessions.get(position).getDate());
        holder.sessionView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Call onClickSession in LoadActivity.java
                //Send it "position" data
            }
        });
    }

    @Override
    public int getItemCount() {
        return sessions.size();
    }

    class LoadSessionItemsViewHolder extends RecyclerView.ViewHolder {

        TextView sessionDate;
        Button sessionView;

        public LoadSessionItemsViewHolder(View itemView) {
            super(itemView);
            sessionDate = itemView.findViewById(R.id._sessionDateTextView);
            sessionView = itemView.findViewById(R.id._viewSessionButton);
        }
    }
}
