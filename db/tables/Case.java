package com.example.facharztkatalog.db.tables;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Entity;
import androidx.room.Fts4;
import androidx.room.Ignore;
import androidx.room.Insert;
import androidx.room.PrimaryKey;
import androidx.room.Query;
import androidx.room.Update;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.time.Year;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

@Entity(tableName = "cases")
public class Case {

    @PrimaryKey(autoGenerate = true)
    private long id;

    private String title;
    private Date dateOfSurgery;
    private Date dateOfBirth;
    private String initials;
    private int asaScore;
    private String note;
    private String hospital;

    @Ignore
    private boolean ambulant;

    @Ignore
    public Case(String title, Date dateOfSurgery, Date dateOfBirth, String initials, int asaScore, String note, String hospital) {
        this.title = title;
        this.dateOfSurgery = dateOfSurgery;
        this.dateOfBirth = dateOfBirth;
        this.initials = initials;
        this.asaScore = asaScore;
        this.note = note;
        this.hospital = hospital;
    }

    public Case() {
        this("", new Date(), new Date(0), "", -1, "", "BKT");
    }


    // Utils
    public static int getAge(Date dateOfBirth, Date dateOfSurgery) {
        if(dateOfBirth != null && dateOfSurgery != null) {
            LocalDate dob = dateOfBirth.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            LocalDate dos = dateOfSurgery.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            return Period.between(dob, dos).getYears();
        }
        return 0;
    }
    public String getSurgetyYear() {
        return getSurgeryDate("y",dateOfSurgery);
    }
    public String getSurgeryMonthAndYear() {
        return getSurgetyMonth() + " " + getSurgetyYear();
    }
    public String getSurgetyMonth() {
        return getSurgeryDate("m", dateOfSurgery);
    }
    public String getSurgetyDay() {
        return getSurgeryDate("d", dateOfSurgery);
    }
    public String getSurgeryDate() {
        return getSurgeryDate("", dateOfSurgery);
    }
    public String getDOB() {
        return getSurgeryDate("", dateOfBirth);
    }

    private String getSurgeryDate(String mode, Date date) {
        Calendar c = Calendar.getInstance();
        c.setTimeZone(TimeZone.getDefault());
        c.setTime(date);
        switch (mode) {
            case "y":
                return "" + c.get(Calendar.YEAR);
            case "m":
                return new SimpleDateFormat("MMM").format(c.getTime()).replaceAll("\\.", "");
            case "d":
                return "" + c.get(Calendar.DAY_OF_MONTH);

            default:
                return new SimpleDateFormat("dd.MM.YYYY").format(c.getTime());
        }
    }

    public static Date convertStringToDate(String date) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        sdf.setTimeZone(TimeZone.getDefault());
        try {
            Date d = sdf.parse(date);
            return d;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }


    @Override
    public boolean equals(@Nullable Object obj) {
        if(!(obj instanceof Case)) {
            return false;
        }
        Case c = (Case) obj;
        return c.getAsaScore() == asaScore &&
                c.getDateOfBirth().equals(dateOfBirth) &&
                c.getDateOfSurgery().equals(dateOfSurgery) &&
                c.getInitials().equals(initials) &&
                c.getTitle().equals(title) &&
                c.getNote().equals(note) &&
                c.getHospital().equals(hospital) &&
                c.isAmbulant() == ambulant;
    }

    public static Comparator<Case> getSurgeryDateComparator() {
        Comparator c = new Comparator() {
            @Override
            public int compare(Object o1, Object o2) {
                if(o1 instanceof Case && o2 instanceof Case) {
                    Case c1 = (Case) o1;
                    Case c2 = (Case) o2;
                    Date d1 = c1.getDateOfSurgery();
                    Date d2 = c2.getDateOfSurgery();
                    return d1.before(d2) ? 1 : -1;
                }
                throw new IllegalArgumentException();
            }
        };
        return c;
    }

    public Date getDateOfSurgery() {
        return dateOfSurgery;
    }
    public Date getDateOfBirth() {
        return dateOfBirth;
    }
    public String getInitials() {
        return initials;
    }
    public int getAsaScore() {
        return asaScore;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public void setDateOfSurgery(Date dateOfSurgery) {
        this.dateOfSurgery = dateOfSurgery;
    }
    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }
    public void setInitials(String initials) {
        this.initials = initials;
    }
    public void setAsaScore(int asaScore) {
        this.asaScore = asaScore;
    }
    public String getHospital() {
        return hospital;
    }
    public void setHospital(String hospital) {
        this.hospital = hospital;
    }
    public String getNote() {
        return note;
    }
    public void setNote(String note) {
        this.note = note;
    }
    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }
    public boolean isFreshCase() {
        return id == 0;
    }
    public boolean isAmbulant() {
        return ambulant;
    }
    public void setAmbulant(boolean ambulant){
        this.ambulant = ambulant;
    }


    @Dao
    public interface CaseDao {
        @Update
        void updateCases(Case... cases);

        @Insert
        void insertAll(Case... cases);

        @Insert
        long insert(Case c);

        @Delete
        void delete(Case c);

        @Query("SELECT * FROM cases ORDER BY dateOfSurgery DESC")
        LiveData<List<Case>> getAll();

        @Query("SELECT * FROM cases ORDER BY dateOfSurgery DESC LIMIT 1000")
        LiveData<List<Case>> getFirstOneThousand();

        @Query("SELECT * FROM cases WHERE :caseId=id")
        Case findCaseForId(long caseId);

        @Query("SELECT cases.id, cases.title, cases.dateOfSurgery, cases.dateOfBirth, cases.initials, cases.asaScore, cases.note, cases.hospital FROM cases " +
                "JOIN case_procedure_crossref ON case_procedure_crossref.caseId = cases.id " +
                "JOIN procedures ON case_procedure_crossref.procedureId = procedures.id " +
                "WHERE procedures.name LIKE :p " +
                "GROUP BY cases.id " +
                "ORDER BY cases.dateOfSurgery DESC")
        List<Case> searchCasesForProcedure(String p);
    }
}