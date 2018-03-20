package com.example.diaryofsecrets.navigation;

import android.app.Activity;
import android.content.Intent;

import com.example.diaryofsecrets.R;

/**
 * Created by Nandita Rai on 2/3/2018.
 */

public class ChangeTheme {
    final static int THEME_BLUE = 0;
    final static int THEME_GOLDEN = 1;
    final static int THEME_GREEN = 2;
    final static int THEME_PURPLE = 3;
    final static int THEME_RED  = 4;

    /** Set the theme of the activity, according to the configuration. */
    public static void onActivityCreateSetTheme(Activity activity, int theme)
    {
        switch (theme)
        {
            default:
            case THEME_BLUE:
                activity.setTheme(R.style.DefaultTheme);
                break;
            case THEME_GOLDEN:
                activity.setTheme(R.style.GoldenTheme);
                break;
            case THEME_GREEN:
                activity.setTheme(R.style.GreenTheme);
                break;
            case THEME_PURPLE:
                activity.setTheme(R.style.PurpleTheme);
                break;
            case THEME_RED:
                activity.setTheme(R.style.RedTheme);
                break;
        }
    }
}
