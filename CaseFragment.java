package com.example.facharztkatalog;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;

import com.example.facharztkatalog.db.tables.Case;
import com.example.facharztkatalog.db.tables.Procedure;
import com.example.facharztkatalog.gui.DateEditText;
import com.example.facharztkatalog.gui.ListAdapter;

import java.util.Date;
import java.util.Objects;

public class CaseFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemLongClickListener, CompoundButton.OnCheckedChangeListener {

    private MainViewModel model;
    private ListAdapter<Procedure> lva;

    private ActionBar actionBar;
    private LinearLayout footer;

    //UI
    private TextView asaScore;
    private DateEditText dateOfBirth;
    private DateEditText dateOfSurgery;
    private TextView title;
    private TextView initials;
    private TextView note;
    private CheckBox ambulant;
    private TextView hospital;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        requireActivity().getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                onBackPressed();
            }
        });
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_case, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
        model = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
        model.getSelectedCase().observe(getViewLifecycleOwner(), this::updateView);
        model.getProcedures().observe(getViewLifecycleOwner(), procedures -> {
            lva.setItems(procedures);
            lva.notifyDataSetChanged();
        });

        checkForNewProcedures();
    }

    private void checkForNewProcedures() {
        if (getArguments() == null) {
            return;
        }
        long[] proceduresToAdd = CaseFragmentArgs.fromBundle(getArguments()).getProceduresToAdd();
        if (proceduresToAdd == null) return;
        for (long id : proceduresToAdd) {
            model.getProcedure(id).observe(getViewLifecycleOwner(), p -> {
                model.addProcedure(p);
                lva.notifyDataSetChanged();
            });
        }

    }

    private void initView() {
        actionBar = ((AppCompatActivity) requireActivity()).getSupportActionBar();

        View v = getView();
        if (v == null) return;
        asaScore = getView().findViewById(R.id.editCase_asaScore);
        dateOfBirth = getView().findViewById(R.id.editCase_dateOfBirth);
        dateOfSurgery = getView().findViewById(R.id.editCase_dateOfSurgery);
        title = getView().findViewById(R.id.editCase_caseTitle);
        initials = getView().findViewById(R.id.editCase_initials);
        note = getView().findViewById(R.id.editCase_note);
        ambulant = getView().findViewById(R.id.cb_ambulant);
        hospital = getView().findViewById(R.id.editCase_hospital);

        ListView listView = getView().findViewById(R.id.editCase_procedureListView);
        listView.setOnItemLongClickListener(this);
        lva = new ListAdapter<>(getContext());
        listView.setAdapter(lva);

        footer = getView().findViewById(R.id.btnFooter);
        getView().findViewById(R.id.bDiscard).setOnClickListener(this);
        getView().findViewById(R.id.bSave).setOnClickListener(this);
        getView().findViewById(R.id.fabAddProcedure).setOnClickListener(this);
        ambulant.setOnCheckedChangeListener(this);


        TextWatcher dateWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                int age = getAge();
                model.getProcedure(32).observe(getViewLifecycleOwner(), p -> {
                    if(age < 6) {
                        model.addProcedure(p);
                    } else {
                        model.removeProcedure(p);
                    }
                    lva.notifyDataSetChanged();
                });
            }
        };
        dateOfBirth.addTextChangedListener(dateWatcher);
        dateOfSurgery.addTextChangedListener(dateWatcher);


    }

    private int getAge() {
        Date dob = Case.convertStringToDate(Objects.requireNonNull(dateOfBirth.getText()).toString());
        Date dos = Case.convertStringToDate(Objects.requireNonNull(dateOfSurgery.getText()).toString());
        return  Case.getAge(dob, dos);
    }

    private void updateView(Case c) {
        if (c.isFreshCase()) {
            actionBar.setTitle(R.string.newEntryTitle);
            footer.setVisibility(View.VISIBLE);
        } else {
            actionBar.setTitle(R.string.editEntry);
            footer.setVisibility(View.GONE);
        }
        int asa = c.getAsaScore();
        asaScore.setText(asa != -1 ? c.getAsaScore() + "" : "");
        dateOfBirth.setDate(c.getDateOfBirth());
        dateOfSurgery.setDate(c.getDateOfSurgery());
        title.setText(c.getTitle());
        initials.setText(c.getInitials());
        note.setText(c.getNote());
        ambulant.setChecked(c.isAmbulant());
        hospital.setText(c.getHospital());
    }


    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_case_fragment, menu);
    }

    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        //MenuItem item = menu.findItem(R.id.action_export);
        //if(item != null)
        //    item.setVisible(false);
        MenuItem delete = menu.findItem(R.id.action_delete);
        model.getSelectedCase().observe(getViewLifecycleOwner(), c -> delete.setVisible(!c.isFreshCase()));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete:
                showDeleteDialog();
                return true;
            case android.R.id.home:
                returnToOverviewFragment();
                return true;
        }
        return false;
    }

    private Case makeCaseFromUIData() {
        int asa;
        try {
            asa = Integer.parseInt(asaScore.getText().toString());
        } catch (NumberFormatException e) {
            asa = -1;
        }
        Date dob = Case.convertStringToDate(Objects.requireNonNull(dateOfBirth.getText()).toString());
        Date dos = Case.convertStringToDate(Objects.requireNonNull(dateOfSurgery.getText()).toString());

        String t = title.getText().toString();
        String i = initials.getText().toString();
        String n = note.getText().toString();
        boolean b = ambulant.isChecked();
        String h = hospital.getText().toString();
        Case c = new Case(t, dos, dob, i, asa, n, h);
        c.setAmbulant(b);
        return c;
    }

    private void save() {
        Case c = makeCaseFromUIData();
        model.saveCase(c);
        model.dbUpdateCase();
        returnToOverviewFragment();
    }

    private void discard() {
        returnToOverviewFragment();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bDiscard:
                showDiscardDialog();
                return;
            case R.id.bSave:
                showSaveDialog();
                return;
            case R.id.fabAddProcedure:
                model.saveCase(makeCaseFromUIData());
                goToProcedureListFragment();
        }
    }

    private void goToProcedureListFragment() {
        MainActivity.goToDestination(CaseFragment.this, R.id.CaseFragment, R.id.action_CaseFragment_to_ProcedureListFragment);
    }

    private void returnToOverviewFragment() {
        MainActivity.goToDestination(CaseFragment.this, R.id.CaseFragment, R.id.action_CaseFragment_to_OverviewFragment);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        view.setSelected(true);
        long procedureId = lva.getItem(position).getId();
        model.getProcedure(procedureId).observe(getViewLifecycleOwner(), p -> {
            if(procedureId == 33) {
                ambulant.setChecked(false);
            } else {
                model.removeProcedure(p);
                lva.notifyDataSetChanged();
            }
        });

        return false;
    }

    private void showDiscardDialog() {
        new AlertDialog.Builder(requireContext())
                .setTitle(R.string.discardEntry)
                .setMessage(R.string.sureToDiscard)
                .setPositiveButton(R.string.yes, (dialog, which) -> discard())
                .setNegativeButton(R.string.no, null)
                .setIcon(android.R.drawable.ic_menu_delete)
                .show();
    }

    private void showSaveDialog() {
        new AlertDialog.Builder(requireContext())
                .setTitle(R.string.saveEntry)
                .setMessage(R.string.sureToSave)
                .setPositiveButton(R.string.yes, (dialog, which) -> save())
                .setNegativeButton(R.string.no, null)
                .setIcon(android.R.drawable.ic_menu_save)
                .show();
    }

    private void showBackPressedDialog() {
        int title, msg;
        LiveData<Case> c = model.getSelectedCase();
        if (c.getValue() != null && c.getValue().isFreshCase()) {
            title = R.string.newEntry;
            msg = R.string.sureToSaveNewEntry;
        } else {
            title = R.string.saveChanges;
            msg = R.string.overwrite;
        }
        new AlertDialog.Builder(requireContext())
                .setTitle(title)
                .setMessage(msg)
                .setPositiveButton(R.string.yes, (dialog, which) -> {
                    model.dbUpdateCase();
                    returnToOverviewFragment();
                })
                .setNegativeButton(R.string.no, (dialog, which) -> returnToOverviewFragment())
                .setNeutralButton(R.string.cancel, null)
                .setIcon(android.R.drawable.ic_menu_save)
                .show();
    }

    private void showDeleteDialog() {
        new AlertDialog.Builder(requireContext())
                .setTitle(R.string.deleteEntry)
                .setMessage(R.string.sureToDelete)
                .setPositiveButton(R.string.yes, (dialog, which) -> {
                    model.deleteSelectedCase();
                    returnToOverviewFragment();
                })
                .setNegativeButton(R.string.no, null)
                .setIcon(R.drawable.ic_error_24px)
                .show();
    }

    private void onBackPressed() {
        final Case c = makeCaseFromUIData();
        model.saveCase(c);
        if (model.hasCaseChanged()) {
            showBackPressedDialog();
        } else {
            returnToOverviewFragment();
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        model.getProcedure(33).observe(getViewLifecycleOwner(), p -> {
            if(isChecked) {
                model.addProcedure(p);
            } else {
                model.removeProcedure(p);
            }
            lva.notifyDataSetChanged();
        });
    }
}
