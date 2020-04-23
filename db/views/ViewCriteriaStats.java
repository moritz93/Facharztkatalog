package com.example.facharztkatalog.db.views;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.DatabaseView;
import androidx.room.Query;

import java.util.List;

@DatabaseView(
        "SELECT criteria.id AS criterionId, criteria.name AS criterion, criteria.target As target, COUNT(*) As value, criteria.groupId As groupId " +
                "FROM criteria " +
                "INNER JOIN procedures ON procedures.criterionId = criteria.id" +
                " INNER JOIN case_procedure_crossref ON procedures.id = case_procedure_crossref.procedureId" +
                " GROUP BY criteria.id")
public class ViewCriteriaStats {
    public long criterionId;
    public String criterion;

    public int target;
    public int value;

    public long groupId;

    @Dao
    public interface ViewCriteriaStatsDao {
        @Query("SELECT * FROM ViewCriteriaStats")
        public List<ViewCriteriaStats> getAll();
    }
}