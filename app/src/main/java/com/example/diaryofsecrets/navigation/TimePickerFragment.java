package com.example.diaryofsecrets.navigation;

import android.app.Dialog;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.format.DateFormat;
import android.widget.TimePicker;

import java.util.Calendar;

/**
 * Created by Nandita Rai on 2/8/2018.
 */

public class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {
    static AlarmTimeSetListener alarmTimeSetListener;

    interface AlarmTimeSetListener{
        public void onAlarmTimeSet(int hour, int min);
    }

    public static void setListener(SetReminderFragment context){
        alarmTimeSetListener = (AlarmTimeSetListener) context;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){

        //Use the current time as the default values for the time picker
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        //Create and return a new instance of TimePickerDialog
        return new TimePickerDialog(getActivity(),this, hour, minute,
                DateFormat.is24HourFormat(getActivity()));
    }

    @Override
    public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
        alarmTimeSetListener.onAlarmTimeSet(hourOfDay, minute);
//        TextView tv = (TextView) getActivity().findViewById(R.id.tv);
//        //Set a message for user
//        tv.setText("Your chosen time is...\n\n");
//        //Display the user changed time on TextView
//        tv.setText(tv.getText()+ "Hour : " + String.valueOf(hourOfDay)
//                + "\nMinute : " + String.valueOf(minute) + "\n");
    }
}
