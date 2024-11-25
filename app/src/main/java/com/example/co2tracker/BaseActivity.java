package com.example.co2tracker;

import android.content.Context;
import android.content.res.Configuration;
import androidx.appcompat.app.AppCompatActivity;

public class BaseActivity extends AppCompatActivity {
    @Override
    protected void attachBaseContext(Context newBase) {
        Context context = LocaleManager.setLocale(newBase);
        super.attachBaseContext(context);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        LocaleManager.setLocale(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (this instanceof MainActivity) {
            ((MainActivity) this).refreshHomeViewModel();
        }
    }
}