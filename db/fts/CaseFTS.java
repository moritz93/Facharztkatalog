package com.example.facharztkatalog.db.fts;

import androidx.lifecycle.LiveData;
import androidx.room.ColumnInfo;
import androidx.room.Dao;
import androidx.room.Entity;
import androidx.room.Fts4;
import androidx.room.PrimaryKey;
import androidx.room.Query;
import androidx.room.Transaction;

import com.example.facharztkatalog.db.tables.Case;

import java.util.List;
import java.util.Set;

@Fts4(contentEntity = Case.class)
@Entity(tableName = "cases_fts")
public class CaseFTS {
    @PrimaryKey
    @ColumnInfo(name = "rowid")
    private final long rowId;
    private final String title;
    private final String note;
    private final String hospital;

    public long getRowId() {
        return rowId;
    }
    public String getTitle() {
        return title;
    }
    public String getNote() {
        return note;
    }
    public String getHospital() {
        return hospital;
    }

    public CaseFTS(long rowId, String title, String note, String hospital) {
        this.rowId = rowId;
        this.title = title;
        this.note = note;
        this.hospital = hospital;
    }

    @Dao
    public interface CaseFTSDao {
        @Transaction
        @Query(
                "SELECT cases.id, cases.title, cases.dateOfSurgery, cases.dateOfBirth, cases.initials, cases.asaScore, cases.note, cases.hospital " +
                        "FROM cases " +
                        "JOIN cases_fts ON (cases.id = cases_fts.rowid) " +
                        "WHERE cases_fts MATCH :term")
        List<Case> searchForCase(String term);
    }
}
