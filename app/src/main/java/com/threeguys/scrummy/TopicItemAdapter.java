package com.threeguys.scrummy;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;

public class TopicItemAdapter extends BaseExpandableListAdapter{

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

    public TopicItemAdapter(Context context, HashMap<String, List<Topic>> topics, List<String> categories) {
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
    public Topic getChild(int groupPosition, int childPosition) {
        return topics.get(categories.get(groupPosition)).get(childPosition);
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
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        Topic topic = getChild(groupPosition, childPosition);
        if (topic == null)
            Log.i("getChildView", "Uh oh, this is a NULL OBJECT!! WHAT DO I DO????????????????");
        else
            Log.i("getChildView","Found Object: " + topic.getTitle());

        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.topic_item, null);
        }

        TextView titleTextView = convertView.findViewById(R.id._topicItemTitleTextView);
        TextView usernameTextView = convertView.findViewById(R.id._topicItemUsernameTextView);

        titleTextView.setText(topic.getTitle());
        usernameTextView.setText(topic.getUsername());

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
