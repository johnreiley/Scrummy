package com.threeguys.scrummy;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;

public class VoteItemAdapter extends BaseExpandableListAdapter {

    private Context context;

    public void setCategories(List<String> categories) {
        this.categories = categories;
    }

    public void setTopics(HashMap<String, List<Topic>> topics) {
        this.topics = topics;
    }

    // group titles
    private List<String> categories;

    // child data
    private HashMap<String, List<Topic>> topics;

    public VoteItemAdapter(Context context, HashMap<String, List<Topic>> topics, List<String> categories) {
        this.context = context;
        this.topics = topics;
        this.categories = categories;
    }

    @Override
    public int getGroupCount() {
        return categories.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return topics.get(categories.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return categories.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return topics.get(categories.get(groupPosition))
                .get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        String headerTitle = (String) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.category_item, null);
        }

        TextView textViewGroup = convertView
                .findViewById(R.id._categoryTextView);
        textViewGroup.setText(headerTitle);

        return convertView;
    }

    @Override
    public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        Topic topic = (Topic) getChild(groupPosition, childPosition);

        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.vote_item, null);
        }

        TextView titleTextView = convertView.findViewById(R.id._topicTitleTextView);
        TextView usernameTextView = convertView.findViewById(R.id._topicUsernameTextView);
        TextView votesTextView = convertView.findViewById(R.id._topicVotesTextView);
        Button addVoteButton = convertView.findViewById(R.id._addVoteButton);
        Button subVoteButton = convertView.findViewById(R.id._subVoteButton);

        titleTextView.setText(topic.getTitle());
        usernameTextView.setText(topic.getUsername());
        votesTextView.setText(String.valueOf(topic.getVotes()));
        addVoteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Call addVote method in VoteActivity.java
                if(context instanceof VoteActivity)
                    ((VoteActivity)context).onClickAddVote(groupPosition, childPosition);
            }
        });
        subVoteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Call subVote method in VoteActivity.java
                if(context instanceof VoteActivity)
                    ((VoteActivity)context).onClickSubVote(groupPosition, childPosition);
            }
        });

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}