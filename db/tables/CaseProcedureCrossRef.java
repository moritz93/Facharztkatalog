package com.example.facharztkatalog.db.tables;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import static androidx.room.ForeignKey.CASCADE;

@Entity(tableName = "case_procedure_crossref",
        primaryKeys = {"caseId", "procedureId"},
        indices = {@Index("caseId"), @Index("procedureId")},
        foreignKeys = {
                @ForeignKey(entity = Case.class,
                        parentColumns = "id",
                        childColumns = "caseId",
                        onDelete = CASCADE),
                @ForeignKey(entity = Procedure.class,
                        parentColumns = "id",
                        childColumns = "procedureId",
                        onDelete = CASCADE)
        })
public class CaseProcedureCrossRef {
    public final long caseId;
    public final long procedureId;

    public CaseProcedureCrossRef(long caseId, long procedureId) {
        this.caseId = caseId;
        this.procedureId = procedureId;
    }

    @Dao
    public interface CaseProcedureCrossRefDao {
        @Insert
        void insert(CaseProcedureCrossRef caseProcedureCrossRef);

        @Insert(onConflict = OnConflictStrategy.IGNORE)
        void insertOrIgnore(CaseProcedureCrossRef... caseProcedureCrossRefs);

        @Update
        void update(CaseProcedureCrossRef caseProcedureCrossRef);

        @Delete
        void delete(CaseProcedureCrossRef... caseProcedureCrossRefs);

        @Query("SELECT cases.id, cases.dateOfSurgery, cases.note, cases.asaScore, cases.initials, cases.dateOfBirth, cases.hospital, cases.title FROM cases INNER JOIN case_procedure_crossref ON cases.id=case_procedure_crossref.caseId WHERE case_procedure_crossref.procedureId=:procedureId")
        LiveData<List<Case>> getCasesForProcedure(final long procedureId);

        @Query("SELECT procedures.id, procedures.name, procedures.criterionId FROM procedures INNER JOIN case_procedure_crossref ON procedures.id=case_procedure_crossref.procedureId WHERE case_procedure_crossref.caseId=:caseId")
        List<Procedure> getProceduresForCase(final long caseId);

    }
}
