package com.threeguys.scrummy;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

public class VoteItemAdapter extends RecyclerView.Adapter<VoteItemAdapter.VoteItemsViewHolder> {

    private List<Topic> topics;

    public VoteItemAdapter(List<Topic> topics) {
        this.topics = topics;
    }

    @Override
    public VoteItemsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.vote_item, parent, false);
        return new VoteItemsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(VoteItemsViewHolder holder, int position) {
        holder.topicTitle.setText(topics.get(position).getTitle());
        holder.topicUsername.setText(topics.get(position).getUsername());
        holder.topicVotes.setText(topics.get(position).getVotes());
        holder.addVote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Call AddVote in VoteActivity.java
                //Send it "position" data
            }
        });
        holder.subVote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Call SubVote in VoteActivity.java
                //Send it "position" data
            }
        });
    }

    @Override
    public int getItemCount() {
        return topics.size();
    }

    class VoteItemsViewHolder extends RecyclerView.ViewHolder {

        TextView topicTitle;
        TextView topicUsername;
        TextView topicVotes;
        Button addVote;
        Button subVote;

        public VoteItemsViewHolder(View itemView) {
            super(itemView);
            topicTitle = itemView.findViewById(R.id._topicTitleTextView);
            topicUsername = itemView.findViewById(R.id._topicUsernameTextView);
            topicVotes = itemView.findViewById(R.id._topicVotesTextView);
            addVote = itemView.findViewById(R.id._addVoteButton);
            subVote = itemView.findViewById(R.id._subVoteButton);
        }
    }
}