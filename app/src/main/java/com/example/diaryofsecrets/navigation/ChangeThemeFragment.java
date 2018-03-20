package com.example.diaryofsecrets.navigation;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.diaryofsecrets.DiaryPreference;
import com.example.diaryofsecrets.MyApplication;
import com.example.diaryofsecrets.R;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Nandita Rai on 2/3/2018.
 */

public class ChangeThemeFragment extends Fragment implements View.OnClickListener {

    public static final String TAG = ChangeThemeFragment.class.getSimpleName();
    private DiaryPreference diaryPreference;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.change_theme_fragment, container, false);

        view.findViewById(R.id.default_theme).setOnClickListener(this);
        view.findViewById(R.id.first_theme).setOnClickListener(this);
        view.findViewById(R.id.second_theme).setOnClickListener(this);
        view.findViewById(R.id.third_theme).setOnClickListener(this);
        view.findViewById(R.id.forth_theme).setOnClickListener(this);

        diaryPreference = new DiaryPreference(MyApplication.getContext());
        return view;
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.default_theme:
                diaryPreference.setTheme(ChangeTheme.THEME_BLUE);
                changeToTheme(getActivity(),ChangeTheme.THEME_BLUE);
                break;
            case R.id.first_theme:
                diaryPreference.setTheme(ChangeTheme.THEME_GOLDEN);
                changeToTheme(getActivity(), ChangeTheme.THEME_GOLDEN);
                break;
            case R.id.second_theme:
                diaryPreference.setTheme(ChangeTheme.THEME_GREEN);
                changeToTheme(getActivity(), ChangeTheme.THEME_GREEN);
                break;
            case R.id.third_theme:
                diaryPreference.setTheme(ChangeTheme.THEME_PURPLE);
                changeToTheme(getActivity(), ChangeTheme.THEME_PURPLE);
                break;
            case R.id.forth_theme:
                diaryPreference.setTheme(ChangeTheme.THEME_RED);
                changeToTheme(getActivity(), ChangeTheme.THEME_RED);
                break;
            default: break;
        }
    }

    /**
     * Set the theme of the Activity, and restart it by creating a new Activity of the same type.
     */
    public static void changeToTheme(Activity activity, int theme)
    {
        activity.finish();
        activity.startActivity(new Intent(activity, activity.getClass()));
    }
}
