package com.example.financialapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

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

        // USER BELUM LOGIN
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            startActivity(new Intent(this, LoginScreen.class));
            finish();
            return;
        }

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // ===================================================
        // ⬅⬅ Cek apakah user sudah onboarding
        // ===================================================
        FirebaseDatabase.getInstance().getReference("Users")
                .child(uid)
                .get()
                .addOnSuccessListener(snapshot -> {

                    Boolean onboarded = snapshot.child("isOnboarded").getValue(Boolean.class);

                    if (onboarded == null || !onboarded) {
                        // User BELUM onboarding → buka OnboardingActivity
                        startActivity(new Intent(MainActivity.this, OnboardingActivity.class));
                        finish();
                        return;
                    }

                    // ===================================================
                    // ⬅⬅ Setelah onboarding → cek apakah sudah tutorial
                    // ===================================================
                    SharedPreferences prefs = getSharedPreferences("app_prefs", MODE_PRIVATE);
                    boolean tutorialDone = prefs.getBoolean("tutorialCompleted", false);

                    if (!tutorialDone) {
                        // User BELUM tutorial → buka TutorialActivity
                        startActivity(new Intent(MainActivity.this, TutorialActivity.class));
                        finish();
                        return;
                    }

                    // Kalau sudah onboarding + tutorial → lanjut normal
                    if (!isFinishing() && !isDestroyed()) {
                        setupBottomNav();
                    }
                });
    }

    private void setupBottomNav() {

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        loadFragment(home_fragment);

        bottomNavigationView.setOnItemSelectedListener(item -> {
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
        });
    }

    public void loadFragment(Fragment fragment) {
        if (!isFinishing() && !isDestroyed()) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.container, fragment)
                    .commitAllowingStateLoss();
        }
    }
}
