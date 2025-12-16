package com.example.financialapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.LinearLayout;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

public class SettingsActivity extends AppCompatActivity {

    LinearLayout btnChangePassword, btnTheme, btnAbout;

    SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // ==== LOAD TEMA SAAT ACTIVITY DIBUKA ====
        prefs = getSharedPreferences("app_theme", MODE_PRIVATE);
        int mode = prefs.getInt("theme_mode", AppCompatDelegate.MODE_NIGHT_NO);
        AppCompatDelegate.setDefaultNightMode(mode);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        btnChangePassword = findViewById(R.id.btnChangePassword);
        btnTheme = findViewById(R.id.btnTheme);
        btnAbout = findViewById(R.id.btnAbout);

        // =============== CHANGE PASSWORD ===============
        btnChangePassword.setOnClickListener(v ->
                startActivity(new Intent(SettingsActivity.this, ChangePasswordActivity.class)));

        // =============== THEME MODE ===============
        btnTheme.setOnClickListener(v -> {
            String[] themes = {"Light Mode", "Dark Mode"};

            new AlertDialog.Builder(this)
                    .setTitle("Choose Theme")
                    .setItems(themes, (dialog, which) -> {

                        if (which == 0) {
                            setThemeMode(AppCompatDelegate.MODE_NIGHT_NO);
                        } else {
                            setThemeMode(AppCompatDelegate.MODE_NIGHT_YES);
                        }

                    })
                    .show();
        });

        // =============== ABOUT APP ===============
        btnAbout.setOnClickListener(v ->
                startActivity(new Intent(SettingsActivity.this, AboutActivity.class)));
    }

    // ==================================================
    // 🔥 FUNGSI GANTI TEMA
    // ==================================================
    private void setThemeMode(int mode) {

        // Simpan pilihan
        prefs.edit().putInt("theme_mode", mode).apply();

        // Terapkan tema
        AppCompatDelegate.setDefaultNightMode(mode);

        // Restart supaya langsung berubah tema
        recreate();
    }
}
