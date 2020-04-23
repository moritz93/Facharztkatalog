package com.example.facharztkatalog.gui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.facharztkatalog.R;
import com.example.facharztkatalog.db.tables.Case;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class OverviewAdapter extends RecyclerView.Adapter<OverviewAdapter.MyViewHolder> {

    private List<Case> dataset;
    private View.OnClickListener onClickListener;

    private Set<Integer> firstOfMonth = new HashSet<>();

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public LinearLayout overviewEntry;
        public boolean activated = false;
        public MyViewHolder(LinearLayout entry, View.OnClickListener clickListener) {
            super(entry);
            overviewEntry = entry;
            entry.setOnClickListener(clickListener);
        }
    }

    public OverviewAdapter(List<Case> dataset, View.OnClickListener onClickListener) {
        this.dataset = dataset;
        this.onClickListener = onClickListener;

        findFirstOfMonth();
    }

    private void findFirstOfMonth() {
        Set<String> dates = new HashSet<>();
        for (int i = 0; i < dataset.size(); i++) {
            Case c = dataset.get(i);
            String date = c.getSurgeryMonthAndYear();
            if(!dates.contains(date)) {
                dates.add(date);
                firstOfMonth.add(i);
            }
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LinearLayout v = (LinearLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_overview_entry, parent, false);

        MyViewHolder vh = new MyViewHolder(v, onClickListener);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Case c = dataset.get(position);
        addCaseEntry(holder, c);
    }

    private void addCaseEntry(MyViewHolder vh, Case c) {
        View view = vh.overviewEntry;
        if(vh.getItemViewType() == 1) {
            TextView tv = (TextView) view.findViewById(R.id.dateDivider);
            tv.setText(c.getSurgetyMonth() + " " + c.getSurgetyYear());
            tv.setVisibility(View.VISIBLE);
        }
        view.setTag(c.getId());
        ((TextView) view.findViewById(R.id.caseEntry_initials)).setText(c.getInitials());
        ((TextView) view.findViewById(R.id.caseEntry_title)).setText(c.getTitle());
        ((TextView) view.findViewById(R.id.caseEntry_age)).setText(Case.getAge(c.getDateOfBirth(), c.getDateOfSurgery()) + "");
        ((TextView) view.findViewById(R.id.caseEntry_day)).setText(c.getSurgetyDay());
        ((TextView) view.findViewById(R.id.caseEntry_month)).setText(c.getSurgetyMonth());
        ((TextView) view.findViewById(R.id.caseEntry_year)).setText(c.getSurgetyYear());
    }

    @Override
    public int getItemCount() {
        return dataset.size();
    }

    @Override
    public int getItemViewType(int position) {
        return firstOfMonth.contains(position) ? 1 : 0;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}