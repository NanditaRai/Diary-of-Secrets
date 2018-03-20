package com.example.diaryofsecrets;

import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.support.v4.app.DialogFragment;
import android.os.Bundle;
import android.widget.DatePicker;
import android.widget.TextView;

import java.util.Calendar;

/**
 * Created by Nandita Rai on 1/14/2018.
 */

public class DatePickerFragment extends DialogFragment implements OnDateSetListener{

    @Override
    public Dialog onCreateDialog(Bundle savedInstancceState){
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day  = calendar.get(Calendar.DAY_OF_MONTH);

        return new DatePickerDialog(getActivity(), this, year, month,day);
    }


    @Override
    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
        TextView calendarText = (TextView) getActivity().findViewById(R.id.calendar);
        String months[] = getResources().getStringArray(R.array.months);
        calendarText.setText(datePicker.getDayOfMonth() + " " + months[datePicker.getMonth()] + ", " +  datePicker.getYear());
    }
}
