package com.example.facharztkatalog.model;

import android.location.Criteria;

import androidx.lifecycle.LiveData;

import com.example.facharztkatalog.db.CriteriaGroupWithCriteria;
import com.example.facharztkatalog.db.tables.CriteriaGroup;
import com.example.facharztkatalog.db.tables.Criterion;
import com.example.facharztkatalog.db.views.ViewCriteriaStats;

import java.util.LinkedList;
import java.util.List;

public class StatsEntry {

    public final String group;
    public final long id;
    public final List<StatsCriterion> criteria;

    private StatsEntry(long id, String group, List<StatsCriterion> criteria) {
        this.id = id;
        this.group = group;
        this.criteria = criteria;
    }

    public int getGroupTarget() {
        int target = 0;
        for (StatsCriterion c : criteria) {
            target += c.target;
        }
        return target;
    }

    public int getGroupValue() {
        int v = 0;
        for (StatsCriterion c : criteria) {
            v += c.value;
        }
        return v;
    }

    public static List<StatsEntry> getStatsFromCriteriaStats(List<ViewCriteriaStats> rows, List<CriteriaGroupWithCriteria> groups) {
        List<StatsEntry> res = new LinkedList<>();
        for (CriteriaGroupWithCriteria group : groups) {
            List<StatsCriterion> criteria = new LinkedList<>();
            for (Criterion c : group.criteria) {
                criteria.add(new StatsCriterion(c.getId(), c.getName(), c.getTarget(), 0));
            }
            StatsEntry entry = new StatsEntry(group.group.getId(), group.group.getName(), criteria);
            res.add(entry);
        }

        for (ViewCriteriaStats row : rows) {
            StatsEntry entry = findEntry(res, row.groupId);
            setCriterionValue(entry, row.criterionId, row.value);
        }
        return res;
    }

    private static StatsEntry findEntry(List<StatsEntry> entries, long id) {
        for (StatsEntry entry : entries) {
            if(entry.id == id) {
                return entry;
            }
        }
        return null;
    }

    private static void setCriterionValue(StatsEntry entry, long criterionId, int value) {
        for (StatsCriterion c : entry.criteria) {
            if(c.id == criterionId) {
                c.setValue(value);
                return;
            }
        }
    }
}
