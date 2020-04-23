package com.example.facharztkatalog.db;

import android.content.Context;
import android.util.Log;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.facharztkatalog.db.fts.CaseFTS;
import com.example.facharztkatalog.db.tables.Case;
import com.example.facharztkatalog.db.tables.CaseProcedureCrossRef;
import com.example.facharztkatalog.db.tables.CriteriaGroup;
import com.example.facharztkatalog.db.tables.Criterion;
import com.example.facharztkatalog.db.tables.Procedure;
import com.example.facharztkatalog.db.views.ViewCriteriaStats;
import com.example.facharztkatalog.db.views.ViewProcedureCriterion;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

@Database(entities = {Case.class, Criterion.class, Procedure.class, CaseProcedureCrossRef.class, CriteriaGroup.class, CaseFTS.class},
        views = {ViewProcedureCriterion.class, ViewCriteriaStats.class}, version = 1)
@TypeConverters(Converters.class)
public abstract class AppDatabase extends RoomDatabase {

    private static final String TAG = AppDatabase.class.getSimpleName();
    private static final String DB_NAME = "catalogue.db";
    private static final String populationFile = "db/populate.sql";
    private static volatile AppDatabase db;

    public static synchronized AppDatabase getInstance(Context context) {
        if(db == null) {
            db = create(context);
        }
        return db;
    }
    private static AppDatabase create(final Context context) {
        RoomDatabase.Callback rdc = new RoomDatabase.Callback() {
            public void onCreate(SupportSQLiteDatabase db) {
                populateDB(db, context);
            }
        };

        return Room.databaseBuilder(context, AppDatabase.class, DB_NAME)
                .addCallback(rdc)
                .allowMainThreadQueries()
                .build();
    }

    private static void populateDB(SupportSQLiteDatabase db, Context context) {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(
                    new InputStreamReader(context.getAssets().open(populationFile)));

            // do reading, usually loop until end of file reading
            String mLine;
            while ((mLine = reader.readLine()) != null) {
                if (!mLine.isEmpty()) db.execSQL(mLine);
            }
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
            // log
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    Log.e(TAG, e.getMessage());
                }
            }
        }
    }

    public abstract Case.CaseDao caseDao();
    public abstract Procedure.ProcedureDao procedureDao();
    public abstract Criterion.CriterionDao criterionDao();
    public abstract CaseProcedureCrossRef.CaseProcedureCrossRefDao caseProcedureCrossRefDao();
    public abstract ViewProcedureCriterion.ViewProcedureCriterionDao viewProcedureDisciplineDao();
    public abstract CriteriaGroupWithCriteria.CriteriaGroupWithCriteriaDao criteriaGroupWithCriteriaDao();
    public abstract CriterionWithProcedures.CriterionWithProceduresDao criterionWithProceduresDao();
    public abstract ViewCriteriaStats.ViewCriteriaStatsDao viewCriteriaStatsDao();
    public abstract CriteriaGroup.CriteriaGroupDao criteriaGroupDao();
    public abstract CaseFTS.CaseFTSDao caseFTSDao();
}
