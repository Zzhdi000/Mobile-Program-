package com.example.financialapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class OnboardingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);

        Button btnStart = findViewById(R.id.btnGetStarted);

        btnStart.setOnClickListener(v -> {

            // SIMPAN STATUS "SUDAH LEWAT ONBOARDING" KE FIREBASE
            String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

            FirebaseDatabase.getInstance()
                    .getReference("Users")
                    .child(uid)
                    .child("isOnboarded")
                    .setValue(true);

            // LANJUT KE TUTORIAL
            startActivity(new Intent(OnboardingActivity.this, TutorialActivity.class));
            finish();
        });
    }
}
