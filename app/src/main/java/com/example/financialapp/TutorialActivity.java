package com.example.financialapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class TutorialActivity extends AppCompatActivity {

    ViewPager2 viewPager;
    Button btnFinish;

    int[] pages = {
            R.layout.item_tutorial_1,
            R.layout.item_tutorial_2,
            R.layout.item_tutorial_3
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);

        viewPager = findViewById(R.id.tutorialViewPager);
        btnFinish = findViewById(R.id.btnFinish);

        TutorialAdapter adapter = new TutorialAdapter(this, pages);
        viewPager.setAdapter(adapter);

        // MULAI AUTO SLIDE
        autoSlide();

        // TOMBOL FINISH
        btnFinish.setOnClickListener(v -> finishTutorial());

        // SETTING TOMBOL FINISH TAMPIL DI HALAMAN TERAKHIR
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {

                if (position == pages.length - 1) {
                    btnFinish.setVisibility(View.VISIBLE);
                } else {
                    btnFinish.setVisibility(View.GONE);
                }
            }
        });
    }

    // ========================
    //     AUTO SLIDE
    // ========================
    private void autoSlide() {
        new Thread(() -> {
            try {
                for (int i = 0; i < pages.length; i++) {
                    Thread.sleep(2000);
                    int finalI = i;
                    runOnUiThread(() -> viewPager.setCurrentItem(finalI, true));
                }

                Thread.sleep(2000);
                runOnUiThread(this::finishTutorial);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    // ========================
    //     FINISH TUTORIAL
    // ========================
    private void finishTutorial() {

        getSharedPreferences("app_prefs", MODE_PRIVATE)
                .edit()
                .putBoolean("tutorialCompleted", true)
                .apply();

        FirebaseDatabase.getInstance()
                .getReference("Users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("isOnboarded")
                .setValue(true);

        startActivity(new Intent(TutorialActivity.this, MainActivity.class));
        finish();
    }
}
