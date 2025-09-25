package com.example.financialapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.Switch;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;

    Home_Fragment home_fragment= new Home_Fragment();
    Profile_Fragment profile_fragment = new Profile_Fragment();
    Dashboard_Fragment dashboard_fragment = new Dashboard_Fragment();
    Transaction_Fragment transaction_fragment = new Transaction_Fragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottom_navigation);

        getSupportFragmentManager().beginTransaction().replace(R.id.container, home_fragment).commit();

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.home_Fragment:
                        getSupportFragmentManager().beginTransaction().replace(R.id.container, home_fragment).commit();
                        return true;
                    case R.id.dashboard_Fragment:
                        getSupportFragmentManager().beginTransaction().replace(R.id.container, dashboard_fragment).commit();
                        return true;
                    case R.id.transaction_Fragment:
                        getSupportFragmentManager().beginTransaction().replace(R.id.container, transaction_fragment).commit();
                        return true;
                    case R.id.profile_Fragment:
                        getSupportFragmentManager().beginTransaction().replace(R.id.container, profile_fragment).commit();
                        return true;

                }
                return false;
            }
        });



    }
}