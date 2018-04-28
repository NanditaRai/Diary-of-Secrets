package com.example.diaryofsecrets.navigation;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.example.diaryofsecrets.DiaryPreference;
import com.example.diaryofsecrets.MyApplication;
import com.example.diaryofsecrets.R;

/**
 * Created by Nandita Rai on 2/3/2018.
 */

public class SetReminderFragment extends Fragment implements TimePickerFragment.AlarmTimeSetListener{

    public static final String TAG = SetReminderFragment.class.getSimpleName();
    private TextView timeTextView;
    private TextView amPmTextView;
    private Switch alarmToggleButton;
    private String mHours ;
    private String mMins ;
    private DiaryPreference diaryPreference;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.set_reminder_fragement, container, false);

        alarmToggleButton = view.findViewById(R.id.alarm_toggle);
        alarmToggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                {
                    Log.d("alarmCheck","ALARM SET TO TRUE");
                    if(mHours != null && mMins != null){
                        setAlarm();
                    }
                }
                else
                {
                    Log.d("alarmCheck","ALARM SET TO FALSE");
                    NotificationHelper.cancelAlarmRTC();
                    NotificationHelper.disableBootReceiver(getActivity());
                }
            }
        });

        amPmTextView = view.findViewById(R.id.am_pm);
        timeTextView = view.findViewById(R.id.time);
        timeTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               showTimePickerDialog();
            }
        });

        //set the initial alarm if set by the user earlier
        diaryPreference = new DiaryPreference(MyApplication.getContext());
        if(diaryPreference.getAlarm() != null){
            String[] time = diaryPreference.getAlarm().split(",");
            setAlarmDisplayValue(Integer.valueOf(time[0]), Integer.valueOf(time[1]));
//            timeTextView.setText(String.format("%s:%s", String.valueOf(time[0]), String.valueOf(time[1])));
//            amPmTextView.setText(time[2]);
            //just check the button, alarm already set
            alarmToggleButton.setChecked(true);
        }
        return view;
    }

    private void setAlarm() {
        if( timeTextView.getText() != null){
            NotificationHelper.scheduleRepeatingRTCNotification(getActivity(), mHours, mMins);
            NotificationHelper.enableBootReceiver(getActivity());
            String alarmWithSuffix = mHours.concat(",").concat(mMins);
            diaryPreference.setAlarm(alarmWithSuffix);
        }
    }

    private void showTimePickerDialog(){
        DialogFragment timePickerFragment = new TimePickerFragment();
        TimePickerFragment.setListener(this);
        timePickerFragment.show(getActivity().getSupportFragmentManager(),"TimePicker");
    }

    @Override
    public void onAlarmTimeSet(int hour, int min) {
        mHours = String.valueOf(hour);
        mMins = String.valueOf(min);
//        timeTextView.setText(String.format("%s:%s", mHours, mMins));
//        amPmTextView.setText(am_pm);
        setAlarmDisplayValue(hour, min);

        if(! alarmToggleButton.isChecked() )
            alarmToggleButton.setChecked(true);
        else
            setAlarm();
    }

    private void setAlarmDisplayValue(int hour, int min){
        String am_pm;
        if(hour > 12) {
            hour = hour - 12;
            am_pm = "PM";
        }
        else{
            am_pm = "AM";
        }
        String formattedMin = String.format("%02d", min);
        timeTextView.setText(String.format("%s:%s", String.valueOf(hour), formattedMin));
        amPmTextView.setText(am_pm);
    }

}

