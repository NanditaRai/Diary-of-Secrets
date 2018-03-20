package com.example.diaryofsecrets;

import android.content.Context;
import android.content.SharedPreferences;

import java.net.PortUnreachableException;

import static android.media.MediaFormat.KEY_PROFILE;

/**
 * Created by Nandita Rai on 2/3/2018.
 */

public class DiaryPreference {
    private static final String PREF_NAME = "DiaryOfSecretsPreference";
    private static final String KEY_THEME = "theme";
    private static final String KEY_SECURITY_QUESTION = "security question";
    private static final String KEY_SECURITY_QUESTION_POSITION = "question position";
    private static final String KEY_SECURITY_QUESTION_ANSWER = "security answer";
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_ALARM = "alarm";

    // Shared Preferences
    private final SharedPreferences mPref;
    // Editor for Shared preferences
    private final SharedPreferences.Editor editor;
    private final Context mContext;

    // Constructor
    public DiaryPreference(Context context){
        this.mContext = context;
        int PRIVATE_MODE = 0;
        mPref = mContext.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = mPref.edit();
    }

    public void setTheme(int theme){
        editor.putInt(KEY_THEME, theme);
        // commit changes
        editor.commit();
    }

    public int getTheme(){
        return mPref.getInt(KEY_THEME, 0);
    }

    public void setSecurityQuestionPosition(int position){
        editor.putInt(KEY_SECURITY_QUESTION_POSITION, position);
        editor.commit();
    }

    public int getSecurityQuestionPosition(){
        return mPref.getInt(KEY_SECURITY_QUESTION_POSITION, 0);
    }

    public void setSecurityQuestion(String question){
        editor.putString(KEY_SECURITY_QUESTION, question);
        editor.commit();
    }

    String getSecurityQuestion(){
        return mPref.getString(KEY_SECURITY_QUESTION, null);
    }

    public void setSecurityAnswer(String answer){
        editor.putString(KEY_SECURITY_QUESTION_ANSWER, answer);
        editor.commit();
    }

    String getSecurityAnswer(){
        return mPref.getString(KEY_SECURITY_QUESTION_ANSWER, null);
    }

    public void setPassword(String password){
        editor.putString(KEY_PASSWORD, password);
        editor.commit();
    }

    String getPassword(){
        return mPref.getString(KEY_PASSWORD, null);
    }

    public void setAlarm(String alarm){
        editor.putString(KEY_ALARM, alarm);
        editor.commit();
    }

    public String getAlarm(){
        return mPref.getString(KEY_ALARM, null);
    }
}

