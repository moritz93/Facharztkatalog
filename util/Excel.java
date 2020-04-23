package com.example.facharztkatalog.util;

import android.app.Activity;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;

import com.example.facharztkatalog.MainActivity;
import com.example.facharztkatalog.MainViewModel;
import com.example.facharztkatalog.R;
import com.example.facharztkatalog.db.tables.Case;
import com.example.facharztkatalog.db.tables.Procedure;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.List;


public class Excel {

    //////////////////////////////////// Config ////////////////////////////////////////////////////////
    private static final String filename = "KatalogExport.xls";
    private final String destinationPath;
    private static final String templatePath = "excel/excel_template.xls";
    private static final String IS_CHECKED = "X";

    private static final int row_headline = 1;
    private static final int row_start = 5;

    private static final int col_dateOfSurgery = 1;
    private static final int col_initials = 2;
    private static final int col_dateOfBirth = 3;
    private static final int col_ambulant = 5;

    private static final long ID_AMBULANT = 33;
    private static final long ID_YOUNGER_THAN_SIX = 32;
    //////////////////////////////////////////////////////////////////////////////////////////////////


    //private XSSFWorkbook wb;
    //private XSSFSheet sh_1;
    private HSSFWorkbook wb;
    private HSSFSheet sh;
    private MainViewModel model;
    private Handler handler; // Notifies activity when exceptions occur.

    private int currentRow = 0;

    public Excel(Activity activity, MainViewModel model, Handler handler) {
        this.handler = handler;

        destinationPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();
        this.model = model;
        try {
            InputStream stream = activity.getAssets().open(templatePath);
            wb = new HSSFWorkbook(stream);
            sh = wb.getSheetAt(0);
        } catch (IOException e) {
            makeToast(MainActivity.EXCEPTION_CODE, R.string.exception_excelTemplateFileCorrupted);
        }
    }

    public void export() {
        currentRow = row_start;
        List<Case> cases = model.getCases().getValue();
        if (cases == null || cases.size() == 0) {
            makeToast(MainActivity.NOTIFICATION_CODE, R.string.exception_procedureNotFoundInExcelTemplate);
            return;
        }
        for (Case c : cases) {
            try {
                writeCase(c);
                currentRow++;
            } catch (IOException e) {
                makeToast(MainActivity.EXCEPTION_CODE, R.string.exception_procedureNotFoundInExcelTemplate);
                return;
            }
        }

        save();

    }

    private void writeCase(Case c) throws IOException {
        Row r = sh.getRow(currentRow);
        getOrCreateCell(r, col_dateOfSurgery).setCellValue(c.getSurgeryDate());
        getOrCreateCell(r, col_initials).setCellValue(c.getInitials());
        getOrCreateCell(r, col_dateOfBirth).setCellValue(c.getDOB());
        writeProceduresToCaseRow(c);
    }

    private void writeProceduresToCaseRow(Case c) throws IOException {
        for (Procedure p : model.getProceduresForCase(c)) {
            Row r = sh.getRow(currentRow);
            if (p.getId() == ID_AMBULANT) {
                getOrCreateCell(r, col_ambulant).setCellValue(c.isAmbulant() ? IS_CHECKED : "");
            } else if (p.getId() == ID_YOUNGER_THAN_SIX) {
            } else {
                writeProcedure(p);
            }
        }
    }

    private void writeProcedure(Procedure p) throws IOException {
        int column = findColumnByHealine(p.getName());
        if (column == -1) {
            String msg = R.string.exception_procedureNotFoundInExcelTemplate + " " + p.getName();
            throw new IOException(msg);
        }
        Row r = sh.getRow(currentRow);
        getOrCreateCell(r, column).setCellValue(IS_CHECKED);
    }


    private Cell getOrCreateCell(Row r, int column) {
        Cell c = r.getCell(column);
        if (c == null) {
            c = r.createCell(column);
        }
        return c;
    }

    private int findColumnByHealine(String headline) {
        Row r = sh.getRow(row_headline);
        Iterator<Cell> iter = r.cellIterator();
        while (iter.hasNext()) {
            Cell c = iter.next();
            String val = c.getStringCellValue();
            if (val.toLowerCase().replace(" ", "").equals(headline.toLowerCase().replace(" ", ""))) {
                return c.getColumnIndex();
            }
        }
        return -1;
    }

    private void save() {
        long tsLong = System.currentTimeMillis() / 1000;
        String ts = Long.toString(tsLong);
        try (OutputStream stream = new FileOutputStream(destinationPath + "/" + ts + "_" + filename)) {
            wb.write(stream);
            makeToast(MainActivity.NOTIFICATION_CODE, R.string.excelExportSaved);
        } catch (IOException e) {
            makeToast(MainActivity.EXCEPTION_CODE, R.string.exception_couldNotSaveFile);
        }
    }

    private void makeToast(int code, int text) {
        Message msg = Message.obtain();
        msg.arg1 = text;
        msg.what = code;
        msg.setTarget(handler);
        msg.sendToTarget();
    }

}
