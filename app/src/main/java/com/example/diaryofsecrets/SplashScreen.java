package com.example.diaryofsecrets;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
public class SplashScreen extends Activity {

    private DiaryPreference diaryPreference;
    Handler handler;
    Runnable runnable;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        diaryPreference = new DiaryPreference(MyApplication.getContext());

        int SPLASH_TIME_OUT = 2000;
        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                // This method will be executed once the timer is over
                // Start your app login activity
                if(!TextUtils.isEmpty(diaryPreference.getPassword())){
                    Intent applockIntent = new Intent(SplashScreen.this, AppLockScreenActivity.class);
                    startActivity(applockIntent);
                }else {
                    Intent loginIntent = new Intent(SplashScreen.this, CatalogActivity.class);
                    startActivity(loginIntent);
                }
                // close this activity
                finish();
            }
        };
        handler.postDelayed(runnable, SPLASH_TIME_OUT);

//       handler.postDelayed(new Runnable() {
//
//            @Override
//            public void run() {
//                // This method will be executed once the timer is over
//                // Start your app login activity
//                if(!TextUtils.isEmpty(diaryPreference.getPassword())){
//                    Intent applockIntent = new Intent(SplashScreen.this, AppLockScreenActivity.class);
//                    startActivity(applockIntent);
//                }else {
//                    Intent loginIntent = new Intent(SplashScreen.this, CatalogActivity.class);
//                    startActivity(loginIntent);
//                }
//                // close this activity
//                finish();
//            }
//        }, SPLASH_TIME_OUT);


    }

    @Override
    protected void onUserLeaveHint() {
        if(handler != null)
            handler.removeCallbacks(runnable);
        finish();
        super.onUserLeaveHint();
    }

}
