package com.example.facharztkatalog.db;

import androidx.room.Embedded;
import androidx.room.Junction;
import androidx.room.Relation;

import com.example.facharztkatalog.db.tables.Case;
import com.example.facharztkatalog.db.tables.CaseProcedureCrossRef;
import com.example.facharztkatalog.db.tables.Criterion;

import java.util.List;

public class CaseWithCriteria {
    @Embedded
    Case c;
    @Relation(
            parentColumn = "caseId",
            entityColumn = "criterionId",
            associateBy = @Junction(CaseProcedureCrossRef.class)
    )
    public List<Criterion> criteria;
}
