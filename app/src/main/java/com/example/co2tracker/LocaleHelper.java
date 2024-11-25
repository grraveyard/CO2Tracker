package com.example.co2tracker;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.LocaleList;
import java.util.Locale;

public class LocaleHelper {
    private static final String SELECTED_LANGUAGE = "language";

    public static Context setLocale(Context context) {
        SharedPreferences preferences = context.getSharedPreferences("Settings", Context.MODE_PRIVATE);
        String language = preferences.getString(SELECTED_LANGUAGE, "en");
        return updateResources(context, language);
    }

    public static Context setNewLocale(Context context, String language) {
        SharedPreferences preferences = context.getSharedPreferences("Settings", Context.MODE_PRIVATE);
        preferences.edit().putString(SELECTED_LANGUAGE, language).apply();
        return updateResources(context, language);
    }

    private static Context updateResources(Context context, String language) {
        Locale locale = new Locale(language);
        Locale.setDefault(locale);

        Resources res = context.getResources();
        Configuration config = new Configuration(res.getConfiguration());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            LocaleList localeList = new LocaleList(locale);
            LocaleList.setDefault(localeList);
            config.setLocales(localeList);
        } else {
            config.setLocale(locale);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            context = context.createConfigurationContext(config);
        } else {
            res.updateConfiguration(config, res.getDisplayMetrics());
        }

        return context;
    }
}
