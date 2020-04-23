package com.example.facharztkatalog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.facharztkatalog.db.CriterionWithProcedures;
import com.example.facharztkatalog.gui.ProcedureExpandableListAdapter;

import java.util.List;


public class ProcedureListFragment extends Fragment {

    private List<CriterionWithProcedures> listData;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_procedure_selection, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setHasOptionsMenu(true);
        ActionBar actionBar = ((AppCompatActivity) requireActivity()).getSupportActionBar();
        if(actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(true);
            actionBar.setTitle(R.string.selectCriterion);
        }
        MainViewModel model = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
        model.getCriteriaWithProcedures().observe(getViewLifecycleOwner(), items -> {
            listData = items;
            setupExpandableListView(view);
        });
    }

    private void setupExpandableListView(View view) {
        ExpandableListView expandableListView = view.findViewById(R.id.expandableListView);

        ProcedureExpandableListAdapter expandableListAdapter = new ProcedureExpandableListAdapter(getContext(), listData);
        expandableListView.setAdapter(expandableListAdapter);

        expandableListView.setOnChildClickListener((parent, v, groupPosition, childPosition, id) -> {
            returnWithId(id);
            return false;
        });

        expandableListView.setOnGroupClickListener((parent, v, groupPosition, id) -> {
            if(expandableListAdapter.getChildrenCount(groupPosition) == 0) {
                long childId = (long) v.getTag();
                returnWithId(childId);
            }
            return false;
        });
    }

    private void returnWithId(long id) {
        long[] proceduresToAdd = {id};
        ProcedureListFragmentDirections.ActionProcedureListFragmentToCaseFragment action = ProcedureListFragmentDirections.actionProcedureListFragmentToCaseFragment();
        action.setProceduresToAdd(proceduresToAdd);
        MainActivity.goToDestination(this, R.id.ProcedureListFragment, action);
    }
}
