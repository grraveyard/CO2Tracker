package com.example.co2tracker;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import androidx.appcompat.app.AppCompatDelegate;
import java.util.Locale;

public class CO2TrackerApp extends Application {
    private static final String PREFS_NAME = "app_settings";

    @Override
    protected void attachBaseContext(Context base) {
        SharedPreferences prefs = base.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String language = prefs.getString("language", "English");
        String languageCode = language.equals("English") ? "en" : "sv";
        
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);
        
        Configuration config = new Configuration(base.getResources().getConfiguration());
        config.setLocale(locale);
        Context context = base.createConfigurationContext(config);
        
        super.attachBaseContext(context);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        
        // Apply theme
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        boolean isDarkMode = prefs.getBoolean("dark_mode", false);
        AppCompatDelegate.setDefaultNightMode(
            isDarkMode ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO
        );
    }

    public static void updateLanguage(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String language = prefs.getString("language", "English");
        String languageCode = language.equals("English") ? "en" : "sv";
        
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);
        
        Configuration config = new Configuration(context.getResources().getConfiguration());
        config.setLocale(locale);
        context.getResources().updateConfiguration(config, context.getResources().getDisplayMetrics());
    }
}
