package com.example.pakistanirestaurant.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * SharedPreferences Manager for app settings
 * Handles login state, onboarding, theme preferences, and other app settings
 */
public class MySharedPrefManager {

    private static final String PREF_NAME = "pak_restaurant_pref";

    // Keys
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_ONBOARDING_DONE = "onboardingDone";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_THEME = "theme";

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private Context context;

    public MySharedPrefManager(Context context) {
        this.context = context;
        pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = pref.edit();
    }

    // ==================== LOGIN METHODS ====================

    /**
     * Set user login status
     */
    public void setLoggedIn(boolean loggedIn) {
        editor.putBoolean(KEY_IS_LOGGED_IN, loggedIn).apply();
    }

    /**
     * Check if user is logged in
     */
    public boolean isLoggedIn() {
        return pref.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    /**
     * Save username
     */
    public void setUsername(String username) {
        editor.putString(KEY_USERNAME, username).apply();
    }

    /**
     * Get saved username
     */
    public String getUsername() {
        return pref.getString(KEY_USERNAME, "Guest");
    }

    // ==================== ONBOARDING METHODS ====================

    /**
     * Mark onboarding as completed
     */
    public void setOnboardingCompleted(boolean done) {
        editor.putBoolean(KEY_ONBOARDING_DONE, done).apply();
    }

    /**
     * Check if onboarding is completed
     */
    public boolean isOnboardingCompleted() {
        return pref.getBoolean(KEY_ONBOARDING_DONE, false);
    }

    // ==================== THEME METHODS ====================

    /**
     * Set app theme
     * @param theme Use ThemeHelper.LIGHT (0) or ThemeHelper.DARK (1)
     */
    public void setTheme(int theme) {
        // Only allow LIGHT (0) or DARK (1)
        if (theme == ThemeHelper.LIGHT || theme == ThemeHelper.DARK) {
            editor.putInt(KEY_THEME, theme).apply();
        } else {
            // Default to LIGHT if invalid theme
            editor.putInt(KEY_THEME, ThemeHelper.LIGHT).apply();
        }
    }

    /**
     * Get current theme setting
     * @return ThemeHelper.LIGHT (0) or ThemeHelper.DARK (1)
     */
    public int getTheme() {
        int theme = pref.getInt(KEY_THEME, ThemeHelper.LIGHT); // Default to Light theme

        // If old Pakistani theme (2) is saved, convert to Light
        if (theme > 1) {
            setTheme(ThemeHelper.LIGHT);
            return ThemeHelper.LIGHT;
        }

        return theme;
    }

    // ==================== UTILITY METHODS ====================

    /**
     * Clear all preferences (useful for complete logout)
     */
    public void clearAll() {
        editor.clear().apply();
    }

    /**
     * Clear login data only (preserve theme and onboarding settings)
     */
    public void clearLoginData() {
        int currentTheme = getTheme(); // Save theme
        boolean onboardingDone = isOnboardingCompleted(); // Save onboarding status

        editor.remove(KEY_IS_LOGGED_IN);
        editor.remove(KEY_USERNAME);
        editor.apply();

        // Restore theme and onboarding
        setTheme(currentTheme);
        setOnboardingCompleted(onboardingDone);
    }

    /**
     * Get all saved preferences as a summary string (for debugging)
     */
    public String getPreferencesSummary() {
        return "Login: " + isLoggedIn() +
                ", Username: " + getUsername() +
                ", Theme: " + (getTheme() == ThemeHelper.DARK ? "Dark" : "Light") +
                ", Onboarding: " + isOnboardingCompleted();
    }
}