package com.chandra.piyush.project3c.android.federalmoneynewclient;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    private static int year;
    private static int month;
    private static int day;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        //Use the current date as the default date in the date picker
        final Calendar c = Calendar.getInstance();
        if (year == 0) {
            year = c.get(Calendar.YEAR);
            month = c.get(Calendar.MONTH);
            day = c.get(Calendar.DAY_OF_MONTH);
        }
        //Create a new DatePickerDialog instance and return it
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    public static interface OnCompleteListener {
        public abstract void onComplete(List<Integer> time);
    }

    private OnCompleteListener mListener;

    // make sure the Activity implemented it
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            this.mListener = (OnCompleteListener) context;
        } catch (final ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement OnCompleteListener");
        }
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {
        //Do something with the date chosen by the user
        TextView tv = (TextView) getActivity().findViewById(R.id.dateText);
        month++;
        this.year = year;
        this.month = month;
        this.day = day;
        String mon = String.format("%02d", month);
        String dayOfMonth = String.format("%02d", day);
        tv.setText("Date changed...");
        tv.setText(tv.getText() + "\nYear: " + year);
        tv.setText(tv.getText() + "\nMonth: " + mon);
        tv.setText(tv.getText() + "\nDay of Month: " + dayOfMonth);

        String stringOfDate = year + "-" + mon + "-" + dayOfMonth;
        tv.setText(tv.getText() + "\n\nFormatted date: " + stringOfDate);
        List<Integer> datesList = new ArrayList<>();
        datesList.add(day);
        datesList.add(month);
        datesList.add(year);
        this.mListener.onComplete(datesList);
    }
}
