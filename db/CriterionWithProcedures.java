package com.example.facharztkatalog.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Embedded;
import androidx.room.Query;
import androidx.room.Relation;
import androidx.room.Transaction;

import com.example.facharztkatalog.db.tables.Criterion;
import com.example.facharztkatalog.db.tables.Procedure;

import java.util.List;

public class CriterionWithProcedures {
    @Embedded
    public Criterion criterion;
    @Relation(
            parentColumn = "id",
            entityColumn = "criterionId"
    )
    public List<Procedure> procedures;

    @Dao
    public interface CriterionWithProceduresDao {
        @Transaction
        @Query("SELECT * FROM criteria WHERE criteria.id = :id")
        public CriterionWithProcedures getCriterionWithProcedure(long id);

        @Transaction
        @Query("SELECT * FROM criteria")
        public LiveData<List<CriterionWithProcedures>> getAll();

        @Transaction
        @Query("SELECT * FROM criteria WHERE listed=1")
        public LiveData<List<CriterionWithProcedures>> getListed();
    }
}
