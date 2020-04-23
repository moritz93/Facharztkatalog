package com.example.facharztkatalog.db.views;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.DatabaseView;
import androidx.room.Query;

import java.util.List;

@DatabaseView("SELECT criteria.id AS criterionId, criteria.name AS criterion, procedures.id AS procedureId, procedures.name AS procedure FROM procedures INNER JOIN criteria ON procedures.criterionId = criteria.id")
public class ViewProcedureCriterion {
    public long procedureId;
    public String procedure;
    public long criterionId;
    public String criterion;

    @Dao
    public interface ViewProcedureCriterionDao {
        @Query("SELECT * FROM ViewProcedureCriterion")
        public LiveData<List<ViewProcedureCriterion>> getAll();
    }
}