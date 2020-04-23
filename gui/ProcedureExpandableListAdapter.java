package com.example.facharztkatalog.gui;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.util.Pair;

import com.example.facharztkatalog.R;
import com.example.facharztkatalog.db.CriterionWithProcedures;
import com.example.facharztkatalog.db.tables.Procedure;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

public class ProcedureExpandableListAdapter extends BaseExpandableListAdapter {
    private Context context;
    private List<CriterionWithProcedures> data;

    public ProcedureExpandableListAdapter(Context context, List<CriterionWithProcedures> data) {
        this.context = context;
        this.data = data;
    }

    @Override
    public Object getChild(int listPosition, int expandedListPosition) {
        return data.get(listPosition).procedures.get(expandedListPosition);
    }

    @Override
    public long getChildId(int listPosition, int expandedListPosition) {
        return data.get(listPosition).procedures.get(expandedListPosition).getId();
    }

    @Override
    public View getChildView(int listPosition, final int expandedListPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {


        final Procedure procedure = (Procedure) getChild(listPosition, expandedListPosition);
        final String expandedListText = procedure.getName();
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.layout_list_item, null);
        }
        if(data.get(listPosition).procedures.size() == 1) {
            convertView.setVisibility(View.GONE);
        }

        TextView expandedListTextView = (TextView) convertView
                .findViewById(R.id.listItem);
        expandedListTextView.setText(expandedListText);
        return convertView;
    }

    @Override
    public int getChildrenCount(int listPosition) {
        int count = data.get(listPosition).procedures.size();
        return count == 1 ? 0 : count;
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
        return data.get(listPosition).criterion.getId();
    }

    @Override
    public View getGroupView(int listPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        CriterionWithProcedures group = (CriterionWithProcedures) getGroup(listPosition);
        LayoutInflater layoutInflater = (LayoutInflater) this.context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = layoutInflater.inflate(R.layout.layout_list_group, null);
        TextView listTitleTextView = convertView.findViewById(R.id.groupTitle);
        if(getChildrenCount(listPosition) > 0) {
            listTitleTextView.setCompoundDrawablesWithIntrinsicBounds(isExpanded ? R.drawable.ic_expand_less_24px : R.drawable.ic_expand_more_24px, 0, 0, 0);
        } else {
            long childId = group.procedures.get(0).getId();
            convertView.setTag(childId);
        }
        listTitleTextView.setText(group.criterion.getName());
        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int listPosition, int expandedListPosition) {
        return true;
    }
}
