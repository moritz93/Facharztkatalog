package com.example.facharztkatalog.db;

import androidx.room.Embedded;
import androidx.room.Junction;
import androidx.room.Relation;

import com.example.facharztkatalog.db.tables.Case;
import com.example.facharztkatalog.db.tables.CaseProcedureCrossRef;
import com.example.facharztkatalog.db.tables.Criterion;

import java.util.List;

public class CriterionWithCases {
    @Embedded
    Criterion criterion;
    @Relation(
            parentColumn = "criterionId",
            entityColumn = "caseId",
            associateBy = @Junction(CaseProcedureCrossRef.class)
    )
    public List<Case> cases;
}
