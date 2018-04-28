package com.example.diaryofsecrets.utils;

import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.example.diaryofsecrets.MyApplication;

/**
 * Created by Nandita Rai on 4/11/2018.
 */

public class AppUtils {
    /**
     * Utility to hide soft keypad
     */
    public static void hideSoftKeypad(View iView) {
        try {
            if (iView != null) {
                InputMethodManager imm = (InputMethodManager) MyApplication.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) imm.hideSoftInputFromWindow(iView.getWindowToken(), 0);
            }
        } catch (Exception ignored) {

        }
    }
}
