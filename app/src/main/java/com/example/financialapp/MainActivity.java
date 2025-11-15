package com.example.financialapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;

    Home_Fragment home_fragment = new Home_Fragment();
    Profile_Fragment profile_fragment = new Profile_Fragment();
    Dashboard_Fragment dashboard_fragment = new Dashboard_Fragment();
    Transaction_Fragment transaction_fragment = new Transaction_Fragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // =====================================================
        // FIX PALING PENTING : CEK LOGIN DULU
        // =====================================================
        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        if (mAuth.getCurrentUser() == null) {
            startActivity(new Intent(MainActivity.this, LoginScreen.class));
            finish();
            return; // Hentikan jalan ke bawah
        }

        // =====================================================

        bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Default fragment
        loadFragment(home_fragment);

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()) {
                    case R.id.home_Fragment:
                        loadFragment(home_fragment);
                        return true;

                    case R.id.dashboard_Fragment:
                        loadFragment(dashboard_fragment);
                        return true;

                    case R.id.transaction_Fragment:
                        loadFragment(transaction_fragment);
                        return true;

                    case R.id.profile_Fragment:
                        loadFragment(profile_fragment);
                        return true;
                }

                return false;
            }
        });
    }

    public void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, fragment)
                .commit();
    }
}
