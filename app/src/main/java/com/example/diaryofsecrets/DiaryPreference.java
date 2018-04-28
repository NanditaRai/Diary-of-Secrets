package com.example.diaryofsecrets;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;

import java.net.PortUnreachableException;

import static android.media.MediaFormat.KEY_PROFILE;

/**
 * Created by Nandita Rai on 2/3/2018.
 */

public class DiaryPreference {
    private static final String PREF_NAME = "DiaryOfSecretsPreference";
    private static final String KEY_THEME = "theme";
    private static final String KEY_SECURITY_QUESTION = "security question";
    private static final String KEY_SECURITY_QUESTION_ANSWER = "security answer";
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_ALARM = "alarm";
    private static final String KEY_PROFILE_IMAGE = "profile image";

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

    public void setSecurityQuestion(String question){
        editor.putString(KEY_SECURITY_QUESTION, question);
        editor.commit();
    }

    public String getSecurityQuestion(){
        return mPref.getString(KEY_SECURITY_QUESTION, null);
    }

    public void setSecurityAnswer(String answer){
        editor.putString(KEY_SECURITY_QUESTION_ANSWER, answer);
        editor.commit();
    }

    public String getSecurityAnswer(){
        return mPref.getString(KEY_SECURITY_QUESTION_ANSWER, null);
    }

    public void setPassword(String password){
        editor.putString(KEY_PASSWORD, password);
        editor.commit();
    }

    public void clearPassword(){
        editor.putString(KEY_PASSWORD,  null);
        editor.putString(KEY_SECURITY_QUESTION, null);
        editor.putString(KEY_SECURITY_QUESTION_ANSWER, null);
        editor.commit();
    }

    public String getPassword(){
        return mPref.getString(KEY_PASSWORD, null);
    }

    public void setAlarm(String alarm){
        editor.putString(KEY_ALARM, alarm);
        editor.commit();
    }

    public String getAlarm(){
        return mPref.getString(KEY_ALARM, null);
    }

    public void setProfileImageUri(Uri uri){
        editor.putString(KEY_PROFILE_IMAGE, uri.toString());
        editor.commit();
    }

    public String getProfileImageUri(){
        return mPref.getString(KEY_PROFILE_IMAGE, null);
    }
}

