package com.example.facharztkatalog;

import android.app.Application;
import android.os.Handler;
import android.os.Message;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.facharztkatalog.db.AppDatabase;
import com.example.facharztkatalog.db.CriteriaGroupWithCriteria;
import com.example.facharztkatalog.db.CriterionWithProcedures;
import com.example.facharztkatalog.db.tables.Case;
import com.example.facharztkatalog.db.tables.CaseProcedureCrossRef;
import com.example.facharztkatalog.db.tables.Procedure;
import com.example.facharztkatalog.db.views.ViewCriteriaStats;
import com.example.facharztkatalog.model.StatsEntry;

import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class MainViewModel extends AndroidViewModel {

    private LiveData<List<Case>> cases;
    private MutableLiveData<List<Case>> searchedCases;

    /**
     * The case instance where changes are applied to.
     */
    private MutableLiveData<Case> selectedCase;

    /**
     * The case instance that was loaded from database.
     */
    private Case oldCase;

    /**
     * The procedures where changes are applied to.
     */
    private MutableLiveData<List<Procedure>> procedures;

    /**
     * The procedures that were loaded from database.
     */
    private List<Procedure> oldProcedures;

    private LiveData<List<CriterionWithProcedures>> criteriaWithProcedures;
    private final AppDatabase db;
    private LiveData<List<CriteriaGroupWithCriteria>> groups;
    private List<StatsEntry> stats;

    String getLastSearch() {
        return lastSearch;
    }
    void setLastSearch(String lastSearch) {
        this.lastSearch = lastSearch;
    }
    private String lastSearch;

    public MainViewModel(@NonNull Application application) {
        super(application);
        db = AppDatabase.getInstance(application);
    }

    void selectNewCase() {
        reset();
        Case newCase = new Case();
        oldCase = newCase;
        if(selectedCase == null) {
            selectedCase = new MutableLiveData<>();
        }
        selectedCase.setValue(newCase);
    }

    void selectCase(long caseId) {
        reset();
        if(selectedCase == null) {
            selectedCase = new MutableLiveData<>();
            asyncLoadCase(caseId);
        }
    }

    LiveData<Case> getSelectedCase() {
        return selectedCase;
    }

    LiveData<List<Procedure>> getProcedures() {
        if(procedures == null) {
            procedures = new MutableLiveData<>();
            asyncLoadProcedures();
        }
        return procedures;
    }

    private void asyncLoadCase(long id) {
        new Thread(() -> {
            Case c = db.caseDao().findCaseForId(id);
            selectedCase.postValue(c);
            oldCase = c; // Keep reference to check for changes.
        }).start();
    }

    private void asyncLoadProcedures() {
        new Thread(() -> {
            try {
                while(selectedCase.getValue() == null) { Thread.sleep(10);}
            } catch (InterruptedException e) {
                return;
            }
            Case c = selectedCase.getValue();
            List<Procedure> proceduresTmp = db.caseProcedureCrossRefDao().getProceduresForCase(c.getId());
            oldProcedures = new LinkedList<>(proceduresTmp); // Make a copy to check changes.
            procedures.postValue(proceduresTmp);
            for (Procedure p : proceduresTmp) {
                if(p.getId() == 33) {
                    //ambulant
                    c.setAmbulant(true);
                }
            }
        }).start();
    }

    private void reset() {
        selectedCase = null;
        procedures = null;
        oldCase = null;
        oldProcedures = null;
    }

    public LiveData<List<Case>> getCases() {
        if(cases == null) {
           cases = new MutableLiveData<>();
           loadCases();
        }
        return cases;
    }
    private void loadCases() {
        cases = db.caseDao().getFirstOneThousand();
    }
    private void loadProceduresWithDisciplines() {
        criteriaWithProcedures = db.criterionWithProceduresDao().getListed();
    }


    void deleteSelectedCase() {
        new Thread(() -> db.caseDao().delete(selectedCase.getValue())).start();
    }

    private void removeProceduresFromCase(List<Procedure> procedures) {
        Case c = selectedCase.getValue();
        if(c == null) return;
        CaseProcedureCrossRef[] toBeDeleted = new CaseProcedureCrossRef[procedures.size()];
        int i = 0;
        final long caseId = c.getId();
        for (Procedure p : procedures) {
            toBeDeleted[i++] = new CaseProcedureCrossRef(caseId, p.getId());
        }
        db.caseProcedureCrossRefDao().delete(toBeDeleted);
    }

    void saveCase(Case c) {
        c.setId(oldCase.getId());
        selectedCase.setValue(c);
    }

    void dbUpdateCase() {
        Case newCase = selectedCase.getValue();
        if(newCase == null) return;
        new Thread(() -> {
            if(newCase.isFreshCase()) {
                long id = db.caseDao().insert(newCase);
                newCase.setId(id);
            } else {
                db.caseDao().updateCases(newCase);
            }
            dbUpdateProcedures();
        }).start();
    }

    private void dbUpdateProcedures() {
        removeProceduresFromCase(oldProcedures);
        if(procedures.getValue() == null) return;
        addProceduresToCase(procedures.getValue());
    }


    void addProcedure(Procedure p) {
        if(procedures.getValue() == null) return;
        if(procedures.getValue().contains(p)) return;
        procedures.getValue().add(p);
    }

    void removeProcedure(Procedure procedure) {
        if(procedures.getValue() == null) return;
        procedures.getValue().remove(procedure);
    }


    LiveData<List<CriterionWithProcedures>> getCriteriaWithProcedures() {
        if(criteriaWithProcedures == null){
            criteriaWithProcedures = new MutableLiveData<>();
            loadProceduresWithDisciplines();
        }
        return criteriaWithProcedures;
    }

    LiveData<List<CriteriaGroupWithCriteria>> getCriteriaGroupsWithCriteria() {
        if(groups == null){
            groups = new MutableLiveData<>();
            asyncloadGroups();
        }
        return groups;
    }

    private void asyncloadGroups() {
        groups = db.criteriaGroupWithCriteriaDao().getAll();
    }

    private void addProceduresToCase(List<Procedure> procedures) {
        Case c = getSelectedCase().getValue();
        if(c == null) return;
        long caseId = c.getId();
        CaseProcedureCrossRef[] toBeAdded = new CaseProcedureCrossRef[procedures.size()];
        int i = 0;
        for (Procedure p : procedures) {
            CaseProcedureCrossRef cpj = new CaseProcedureCrossRef(caseId, p.getId());
            toBeAdded[i++] = cpj;
        }
        db.caseProcedureCrossRefDao().insertOrIgnore(toBeAdded);
    }

    LiveData<Procedure> getProcedure(long id) {
        return db.procedureDao().getProcedureForId(id);
    }

    public List<Procedure> getProceduresForCase(Case c) {
        return db.caseProcedureCrossRefDao().getProceduresForCase(c.getId());
    }

    boolean hasCaseChanged() {
        Case newCase = selectedCase.getValue();
        if(newCase == null) return false;
        boolean isC = newCase.equals(oldCase);
        if(procedures.getValue() == null) return false;
        boolean isP = procedures.getValue().containsAll(oldProcedures) && oldProcedures.containsAll(procedures.getValue());
        return !(isC && isP);
    }


    void loadStats(Handler h) {
        List<ViewCriteriaStats> entries = db.viewCriteriaStatsDao().getAll();
        List<CriteriaGroupWithCriteria> groups = db.criteriaGroupWithCriteriaDao().getAllAsync();
        stats = StatsEntry.getStatsFromCriteriaStats(entries, groups);
        sendMsgToHandler(h, MainActivity.LOADING_DONE_CODE);
    }
    private static void sendMsgToHandler(Handler h, int message) {
        Message msg = h.obtainMessage();
        msg.what = message;
        msg.setTarget(h);
        msg.sendToTarget();
    }

    List<StatsEntry> getStats() { return stats;}


    void asyncSearchForCases(String query, Handler h) {
        new Thread(() -> {
            LinkedHashSet<Case> result = new LinkedHashSet<>();
            List<Case> caseMatches = db.caseFTSDao().searchForCase(String.format("*%s*", query));
            List<Case> procedureMatches = db.caseDao().searchCasesForProcedure("%" + query + "%"); //SQLite wildcards
            result.addAll(caseMatches);
            result.addAll(procedureMatches);
            List<Case> tmp = new LinkedList<>(result);
            searchedCases.postValue(tmp);
            sendMsgToHandler(h, MainActivity.SEARCH_DONE_CODE);
        }).start();

    }

    LiveData<List<Case>> getSearchedCases() {
        if(searchedCases == null) {
            searchedCases = new MutableLiveData<>(new LinkedList<>());
        }
        return searchedCases;
    }

}
