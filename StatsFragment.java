package com.example.facharztkatalog;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.facharztkatalog.gui.StatisticsExpandableListAdapter;

public class StatsFragment extends Fragment {

    private ViewGroup loading;
    private ExpandableListView expandableListView;
    private MainViewModel model;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_stats, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setHasOptionsMenu(true);
        ActionBar actionBar = ((AppCompatActivity) requireActivity()).getSupportActionBar();
        if(actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(true);
            actionBar.setTitle(R.string.statistics);
        }
        loading = view.findViewById(R.id.loading);
        expandableListView = view.findViewById(R.id.expandableListView);

        model = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
        final Handler h = makeLoadingDoneHandler();

        new Thread(() -> model.loadStats(h)).start();
    }
    private void setupExpandableListView() {
        ExpandableListAdapter expandableListAdapter = new StatisticsExpandableListAdapter(getContext(), model.getStats());
        expandableListView.setAdapter(expandableListAdapter);

    }

    private Handler makeLoadingDoneHandler() {
        return new Handler(msg -> {
            if (msg.what == MainActivity.LOADING_DONE_CODE) {
                setupExpandableListView();
                loading.setVisibility(View.GONE);
                expandableListView.setVisibility(View.VISIBLE);
            }
            return true;
        });
    }
}
