package com.example.financialapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;

public class Profile_Fragment extends Fragment {

    private Spinner spCurrency;
    private FirebaseAuth mAuth;
    private DatabaseReference userRef;

    String[] currencyList = {"LKR", "IDR", "USD", "MYR"};

    // prevent spinner auto-update at first load
    private boolean isFirstLoad = true;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_profile_, container, false);

        spCurrency = v.findViewById(R.id.spCurrency);

        // ====================== HISTORY MENU =========================
        View tvDaily = v.findViewById(R.id.tvDaily);
        View tvWeekly = v.findViewById(R.id.tvWeekly);
        View tvMonthly = v.findViewById(R.id.tvMonthly);

        // === HISTORY CLICKS ===
        v.findViewById(R.id.tvDaily).setOnClickListener(view -> {
            new DailyHistoryBottomSheet().show(getParentFragmentManager(), "daily");
        });

        v.findViewById(R.id.tvWeekly).setOnClickListener(view -> {
            new WeeklyHistoryBottomSheet().show(getParentFragmentManager(), "weekly");
        });

        v.findViewById(R.id.tvMonthly).setOnClickListener(view -> {
            new MonthlyHistoryBottomSheet().show(getParentFragmentManager(), "monthly");
        });

        // =============================================================

        // FIREBASE USER REF
        mAuth = FirebaseAuth.getInstance();
        userRef = FirebaseDatabase.getInstance().getReference("Users")
                .child(mAuth.getCurrentUser().getUid());

        // --- SETUP SPINNER ---
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                getContext(),
                android.R.layout.simple_spinner_item,
                currencyList
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCurrency.setAdapter(adapter);

        // Load currency from Firebase
        loadCurrency();

        // Save currency when changed
        saveCurrency();

        return v;
    }

    private void loadCurrency() {
        userRef.child("currency").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot s) {

                String curr = s.getValue(String.class);
                if (curr == null) curr = "LKR";

                int pos = Arrays.asList(currencyList).indexOf(curr);
                if (pos >= 0) spCurrency.setSelection(pos);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }

    private void saveCurrency() {
        spCurrency.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (isFirstLoad) {
                    isFirstLoad = false;
                    return;
                }

                String selected = currencyList[position];

                userRef.child("currency").setValue(selected);
                Toast.makeText(getContext(), "Currency set to " + selected, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });
    }
}
