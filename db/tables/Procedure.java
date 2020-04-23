package com.example.facharztkatalog.db.tables;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Entity;
import androidx.room.Insert;
import androidx.room.PrimaryKey;
import androidx.room.Query;


import java.util.List;

@Entity(tableName = "procedures")
public class Procedure {

    @PrimaryKey(autoGenerate = true)
    private long id;

    String name;
    private final long criterionId;

    public Procedure(String name, long criterionId) {
        this.name = name;
        this.criterionId = criterionId;
    }

    public String getName() {
        return name;
    }
    public void setId(long id) {
        this.id = id;
    }
    public long getId() {
        return id;
    }
    public long getCriterionId() {
        return criterionId;
    }


    @Dao
    public interface ProcedureDao {
        @Insert
        void insertAll(Procedure... Procedures);

        @Delete
        void delete(Procedure Procedure);

        @Query("SELECT * FROM procedures WHERE criterionId=:criterionId")
        List<Procedure> findProceduresForCriterion(final long criterionId);

        @Query("SELECT * FROM procedures WHERE id=:id")
        LiveData<Procedure> getProcedureForId(long id);
    }

    @NonNull
    @Override
    public String toString() {
        return getName();
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if(obj instanceof Procedure) {
            Procedure p = (Procedure) obj;
            return id == p.getId();
        }
        return false;
    }
}
