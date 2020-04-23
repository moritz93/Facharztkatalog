package com.example.facharztkatalog.db.tables;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Entity;
import androidx.room.Insert;
import androidx.room.PrimaryKey;
import androidx.room.Query;

import java.util.List;

@Entity(tableName = "criteria")
public class Criterion {

    @PrimaryKey(autoGenerate = true)
    private long id;
    private final String name;
    private int target;
    private long groupId;
    private boolean listed = true;

    public Criterion(String name) {
        this.name = name;
    }

    public void setId(long id) {
        this.id = id;
    }
    public long getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public int getTarget() {
        return target;
    }
    public void setTarget(int target) {
        this.target = target;
    }
    public long getGroupId() {
        return groupId;
    }
    public void setGroupId(long groupId) {
        this.groupId = groupId;
    }
    public boolean isListed() { return listed; }
    public void setListed(boolean listed) {
        this.listed = listed;
    }

    @NonNull
    @Override
    public String toString() {
        return getName();
    }

    @Dao
    public interface CriterionDao {
        @Insert
        void insertAll(Criterion... criteria);

        @Delete
        void delete(Criterion criterion);

        @Query("SELECT * FROM criteria")
        List<Criterion> getAll();

        @Query("SELECT * FROM criteria WHERE id=:id")
        Criterion findCriterionForId(final long id);

        @Query("SELECT * FROM criteria WHERE id=:id")
        LiveData<Criterion> getCriterionForId(final long id);
    }
}

