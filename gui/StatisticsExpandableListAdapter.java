package com.example.facharztkatalog.gui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.facharztkatalog.R;
import com.example.facharztkatalog.model.StatsCriterion;
import com.example.facharztkatalog.model.StatsEntry;

import java.util.List;

public class StatisticsExpandableListAdapter extends BaseExpandableListAdapter {
    private Context context;
    private List<StatsEntry> data;

    public StatisticsExpandableListAdapter(Context context, List<StatsEntry> data) {
        this.context = context;
        this.data = data;
    }

    @Override
    public Object getChild(int listPosition, int expandedListPosition) {
        return data.get(listPosition).criteria.get(expandedListPosition);
    }

    @Override
    public long getChildId(int listPosition, int expandedListPosition) {
        return expandedListPosition;
    }

    @Override
    public View getChildView(int listPosition, final int expandedListPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {

        final StatsCriterion c = (StatsCriterion) getChild(listPosition, expandedListPosition);
        if (convertView == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            convertView = layoutInflater.inflate(R.layout.layout_stats_item, parent, false);
        }

        ((TextView) convertView.findViewById(R.id.tv_name)).setText(c.name);
        String s = c.value + "/" + c.target;
        ((TextView) convertView.findViewById(R.id.tv_valueTarget)).setText(s);
        return convertView;
    }

    @Override
    public int getChildrenCount(int listPosition) {
        int count = data.get(listPosition).criteria.size();
        return count == 1 ? 0 : count; // dont show single child
    }

    @Override
    public Object getGroup(int listPosition) {
        return data.get(listPosition);
    }

    @Override
    public int getGroupCount() {
        return data.size();
    }

    @Override
    public long getGroupId(int listPosition) {
        return listPosition;
    }

    @Override
    public View getGroupView(int listPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        StatsEntry group = (StatsEntry) getGroup(listPosition);
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        convertView = layoutInflater.inflate(R.layout.layout_stats_group, parent, false);

        TextView listTitleTextView = convertView.findViewById(R.id.groupTitle);
        if(getChildrenCount(listPosition) > 0) {
            listTitleTextView.setCompoundDrawablesWithIntrinsicBounds(isExpanded ? R.drawable.ic_expand_less_24px : R.drawable.ic_expand_more_24px, 0, 0, 0);
        }
        listTitleTextView.setText(group.group);

        int groupValue = group.getGroupValue();
        int groupTarget = group.getGroupTarget();
        String s = groupValue + "/" + groupTarget;
        ((TextView) convertView.findViewById(R.id.tv_valueTarget)).setText(s);
        ProgressBar bar = convertView.findViewById(R.id.progressbar);
        bar.setMax(groupTarget);
        bar.setProgress(groupValue);
        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int listPosition, int expandedListPosition) {
        return false;
    }
}
