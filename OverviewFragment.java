package com.example.facharztkatalog;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.facharztkatalog.db.tables.Case;
import com.example.facharztkatalog.gui.OverviewAdapter;
import com.example.facharztkatalog.util.Excel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class OverviewFragment extends Fragment implements View.OnClickListener, SearchView.OnQueryTextListener, SearchView.OnCloseListener {

    private MainViewModel model;

    private RecyclerView recyclerViewAll;
    private RecyclerView recyclerViewSearch;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }


    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_overview, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ActionBar actionBar = ((AppCompatActivity) requireActivity()).getSupportActionBar();
        if(actionBar != null) actionBar.setTitle(R.string.app_name);

        model = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
        recyclerViewAll = view.findViewById(R.id.recycler_view);
        recyclerViewSearch = view.findViewById(R.id.recycler_view_search);
        if(model.getLastSearch() != null) {
            recyclerViewSearch.setVisibility(View.VISIBLE);
            recyclerViewAll.setVisibility(View.GONE);

        }
        setupRecyclerView(recyclerViewAll, model.getCases());
        setupRecyclerView(recyclerViewSearch, model.getSearchedCases());

        //recyclerView.addItemDecoration(new DateDivider(requireContext()));

        FloatingActionButton fab = view.findViewById(R.id.fab);
        fab.setOnClickListener(view1 -> {
            model.selectNewCase();
            NavHostFragment.findNavController(OverviewFragment.this).navigate(R.id.action_OverviewFragment_to_CaseFragment);
        });
    }

    private void setupRecyclerView(RecyclerView view, LiveData<List<Case>> data) {
        LinearLayoutManager mgr = new LinearLayoutManager(requireContext());
        view.setLayoutManager(mgr);
        data.observe(getViewLifecycleOwner(), cases -> {
            //Collections.sort(cases, Case.getSurgeryDateComparator()); handled by db
            OverviewAdapter adapter = new OverviewAdapter(cases, this);
            view.setAdapter(adapter);
        });
    }



    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_overview_fragment, menu);
        Activity activity = requireActivity();
        SearchManager manager = (SearchManager) activity.getSystemService(Context.SEARCH_SERVICE);
        if(manager == null) return;
        SearchView search = (SearchView) menu.findItem(R.id.search_bar).getActionView();
        search.setSearchableInfo(manager.getSearchableInfo(activity.getComponentName()));
        search.setOnQueryTextListener(this);
        search.setOnCloseListener(this);
        if(model.getLastSearch() != null) {
            search.setIconified(false);
            search.setQuery(model.getLastSearch(), false);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_export:
                new Thread(() -> new Excel(requireActivity(), model, makeToastHandler()).export()).start();
                return true;

            case R.id.action_stats:
                goToStatsFragment();
                return true;
        }
        return false;
    }

    private void goToCaseFragment() {
        MainActivity.goToDestination(OverviewFragment.this, R.id.OverviewFragment, R.id.action_OverviewFragment_to_CaseFragment);
    }

    private void goToStatsFragment() {
        MainActivity.goToDestination(OverviewFragment.this, R.id.OverviewFragment, R.id.action_OverviewFragment_to_StatsFragment);
    }



    @Override
    public void onClick(View v) {
        long id = Long.parseLong(v.getTag().toString());
        model.selectCase(id);
        goToCaseFragment();
    }

    private Handler makeToastHandler() {
        return new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(@NonNull Message inputMessage) {
                String msg = getResources().getString(inputMessage.arg1);
                msg = inputMessage.what == MainActivity.EXCEPTION_CODE ? "Fehler: " + msg : msg;
                Toast.makeText(requireActivity(), msg, Toast.LENGTH_SHORT).show();
            }
        };
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        model.asyncSearchForCases(query, makeSearchDoneHandler());
        recyclerViewAll.setVisibility(View.GONE);
        recyclerViewSearch.setVisibility(View.VISIBLE);
        model.setLastSearch(query);
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }


    @Override
    public boolean onClose() {
        recyclerViewAll.setVisibility(View.VISIBLE);
        recyclerViewSearch.setVisibility(View.GONE);
        model.setLastSearch(null);
        return false;
    }

    private Handler makeSearchDoneHandler() {
        return new Handler(msg -> {
            if (msg.what == MainActivity.SEARCH_DONE_CODE) {
                recyclerViewSearch.getAdapter().notifyDataSetChanged();
            }
            return true;
        });
    }
}