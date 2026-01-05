package com.example.pakistanirestaurant.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.example.pakistanirestaurant.R;
import com.example.pakistanirestaurant.utils.MySharedPrefManager;
import com.example.pakistanirestaurant.utils.ThemeHelper;
import com.google.mlkit.common.sdkinternal.SharedPrefManager;

public class SplashActivity extends AppCompatActivity {
    SharedPrefManager pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeHelper.applyTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);




        new Handler().postDelayed(() -> {
            if(!new MySharedPrefManager(this).isOnboardingCompleted()) {
                startActivity(new Intent(this, OnboardingActivity.class));
            } else if(new MySharedPrefManager(this).isLoggedIn()) {
                startActivity(new Intent(this, MainActivity.class));
            } else {
                startActivity(new Intent(this, LoginActivity.class));
            }
            finish();
        }, 2000); // 2 sec splash
    }
}
