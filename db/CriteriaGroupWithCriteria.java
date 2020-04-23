package com.example.facharztkatalog.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Embedded;
import androidx.room.Query;
import androidx.room.Relation;
import androidx.room.Transaction;

import com.example.facharztkatalog.db.tables.CriteriaGroup;
import com.example.facharztkatalog.db.tables.Criterion;

import java.util.List;

public class CriteriaGroupWithCriteria {
    @Embedded
    public CriteriaGroup group;
    @Relation(
            parentColumn = "id",
            entityColumn = "groupId"
    )
    public List<Criterion> criteria;

    @Dao
    public interface CriteriaGroupWithCriteriaDao {

        @Transaction
        @Query("SELECT * FROM criteria_groups")
        public LiveData<List<CriteriaGroupWithCriteria>> getAll();

        @Transaction
        @Query("SELECT * FROM criteria_groups")
        public List<CriteriaGroupWithCriteria> getAllAsync();
    }
}
