package com.example.pakistanirestaurant.utils;

import android.app.Activity;

import com.example.pakistanirestaurant.R;

/**
 * Helper class to manage app themes
 * Supports Light and Dark themes
 */
public class ThemeHelper {

    // Theme constants
    public static final int LIGHT = 0;
    public static final int DARK = 1;

    /**
     * Apply the saved theme to the activity
     * Call this in onCreate() BEFORE setContentView()
     */
    public static void applyTheme(Activity activity) {
        int theme = new MySharedPrefManager(activity).getTheme();

        switch(theme) {
            case DARK:
                activity.setTheme(R.style.Theme_App_Dark);
                break;
            case LIGHT:
            default:
                activity.setTheme(R.style.Theme_App_Light);
                break;
        }
    }

    /**
     * Set and save a new theme
     * Call recreate() on the activity after this to apply changes
     */
    public static void setTheme(Activity activity, int theme) {
        new MySharedPrefManager(activity).setTheme(theme);
    }

    /**
     * Get the current theme setting
     */
    public static int getCurrentTheme(Activity activity) {
        return new MySharedPrefManager(activity).getTheme();
    }

    /**
     * Check if dark theme is currently active
     */
    public static boolean isDarkTheme(Activity activity) {
        return getCurrentTheme(activity) == DARK;
    }

    /**
     * Get theme name as string
     */
    public static String getThemeName(Activity activity) {
        return isDarkTheme(activity) ? "Dark Theme" : "Light Theme";
    }
}