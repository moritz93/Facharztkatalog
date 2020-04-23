package com.example.facharztkatalog.db.tables;

import androidx.room.Dao;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.Query;

import java.util.List;

@Entity(tableName = "criteria_groups")
public class CriteriaGroup {

    @PrimaryKey(autoGenerate = true)
    private long id;
    private String name;

    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    @Dao
    public interface CriteriaGroupDao {
        @Query("SELECT * FROM criteria_groups")
        List<CriteriaGroup> getAll();
    }
}

