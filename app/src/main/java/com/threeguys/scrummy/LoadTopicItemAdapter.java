package com.threeguys.scrummy;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class LoadTopicItemAdapter extends RecyclerView.Adapter<LoadTopicItemAdapter.LoadTopicItemsViewHolder> {

    private List<Topic> topics;

    public LoadTopicItemAdapter(List<Topic> topics) {
        this.topics = topics;
    }

    @Override
    public LoadTopicItemAdapter.LoadTopicItemsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.vote_item, parent, false);
        return new LoadTopicItemAdapter.LoadTopicItemsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(LoadTopicItemAdapter.LoadTopicItemsViewHolder holder, int position) {
        holder.topicTitle.setText(topics.get(position).getTitle());
        holder.topicActions.setText(topics.get(position).getActions());

        String bad = "Bad";
        String good = "Good";
        String neutral = "Neutral";

        switch (topics.get(position).getCategory()) {
            case BAD:
                holder.topicCategory.setText(bad);
                break;
            case GOOD:
                holder.topicCategory.setText(good);
                break;
            default:
                holder.topicCategory.setText(neutral);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return topics.size();
    }

    class LoadTopicItemsViewHolder extends RecyclerView.ViewHolder {

        TextView topicTitle;
        TextView topicCategory;
        TextView topicActions;

        public LoadTopicItemsViewHolder(View itemView) {
            super(itemView);
            topicTitle = itemView.findViewById(R.id._topicTitleTextView);
            topicCategory = itemView.findViewById(R.id._topicCategoryTextView);
            topicActions = itemView.findViewById(R.id._topicActionsTextView);
        }
    }
}