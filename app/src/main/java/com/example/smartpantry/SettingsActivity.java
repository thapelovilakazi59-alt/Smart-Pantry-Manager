package com.example.smartpantry;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

/**
 * SettingsActivity
 * Provides user preferences: expiry alerts toggle and dark mode toggle.
 * Uses SharedPreferences for lightweight key-value storage.
 * Satisfies the minimum-screens requirement (5th screen).
 */
public class SettingsActivity extends AppCompatActivity {

    private Switch switchExpiryAlert, switchDarkMode;
    private SharedPreferences prefs;
    private static final String PREFS_NAME = "smart_pantry_prefs";
    private static final String KEY_EXPIRY_ALERT = "expiry_alert";
    private static final String KEY_DARK_MODE = "dark_mode";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        setTitle("Settings");

        prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        switchExpiryAlert = findViewById(R.id.switchExpiryAlert);
        switchDarkMode = findViewById(R.id.switchDarkMode);

        // Load saved preferences
        switchExpiryAlert.setChecked(prefs.getBoolean(KEY_EXPIRY_ALERT, true));
        switchDarkMode.setChecked(prefs.getBoolean(KEY_DARK_MODE, false));

        switchExpiryAlert.setOnCheckedChangeListener((buttonView, isChecked) -> {
            prefs.edit().putBoolean(KEY_EXPIRY_ALERT, isChecked).apply();
            String msg = isChecked ? "Expiry alerts enabled" : "Expiry alerts disabled";
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        });

        switchDarkMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            prefs.edit().putBoolean(KEY_DARK_MODE, isChecked).apply();
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
        });
    }
}
