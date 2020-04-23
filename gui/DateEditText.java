package com.example.facharztkatalog.gui;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;

import com.example.facharztkatalog.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateEditText extends androidx.appcompat.widget.AppCompatEditText implements View.OnClickListener {

    private final Calendar myCalendar;
    private final SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.YYYY", Locale.GERMANY);
    private final Context context;
    private DatePickerDialog.OnDateSetListener date;
    private boolean spinnerMode;


    public DateEditText(Context context, AttributeSet attr) {
        super(context, attr);
        this.context = context;
        setFocusable(false);
        setFocusableInTouchMode(false);
        setClickable(true);
        setOnClickListener(this);

        TypedArray a = context.getTheme().obtainStyledAttributes(attr, R.styleable.DateEditText, 0, 0);
        try {
            spinnerMode = a.getBoolean(R.styleable.DateEditText_spinnerMode, false);
        } finally {
            a.recycle();
        }
        myCalendar = Calendar.getInstance();

        date = (view, year, monthOfYear, dayOfMonth) -> {
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateLabel();
        };
    }

    public void setDate(Date date) {
        myCalendar.setTime(date);
        setText(sdf.format(myCalendar.getTime()));
    }

    private void updateLabel() {
        setText(sdf.format(myCalendar.getTime()));
    }

    @Override
    public void onClick(View v) {
        DatePickerDialog dialog;
        if (spinnerMode) {
            dialog = new DatePickerDialog(context, R.style.MySpinnerDatePickerStyle, date, myCalendar
                    .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                    myCalendar.get(Calendar.DAY_OF_MONTH));
        } else {
            dialog = new DatePickerDialog(context, date, myCalendar
                    .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                    myCalendar.get(Calendar.DAY_OF_MONTH));
        }
        dialog.show();

    }
}
