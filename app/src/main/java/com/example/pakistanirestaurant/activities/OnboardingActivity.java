package com.example.pakistanirestaurant.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.example.pakistanirestaurant.R;
import com.example.pakistanirestaurant.adapters.OnboardingAdapter;
import com.example.pakistanirestaurant.utils.MySharedPrefManager;
import com.example.pakistanirestaurant.utils.ThemeHelper;
import com.google.mlkit.common.sdkinternal.SharedPrefManager;

import java.util.Arrays;
import java.util.List;

public class OnboardingActivity extends AppCompatActivity {
    ViewPager2 viewPager;
    Button btnNext;
    SharedPrefManager pref;
    int currentPage = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeHelper.applyTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);

        pref = new SharedPrefManager(this);
        viewPager = findViewById(R.id.viewPager);
        btnNext = findViewById(R.id.btnNext);

        List<Integer> pages = Arrays.asList(
                R.drawable.logo,
                R.drawable.food,
                R.drawable.delivery
        );

        viewPager.setAdapter(new OnboardingAdapter(pages));
        btnNext.setOnClickListener(v -> {
            if(currentPage < pages.size()-1){
                currentPage++;
                viewPager.setCurrentItem(currentPage);
            } else {
                new MySharedPrefManager(this).setOnboardingCompleted(true);
                startActivity(new Intent(this, LoginActivity.class));
                finish();
            }
        });

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                currentPage = position;
            }
        });
    }
}
