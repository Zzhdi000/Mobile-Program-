package com.example.financialapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
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

    private TextView tvDaily, tvWeekly, tvMonthly;
    private TextView tvSavings, tvFeedback, tvLogout;

    String[] currencyList = {"LKR", "IDR", "USD", "MYR"};

    private boolean isFirstLoad = true;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_profile_, container, false);

        // BIND ALL VIEWS
        spCurrency = v.findViewById(R.id.spCurrency);

        tvDaily = v.findViewById(R.id.tvDaily);
        tvWeekly = v.findViewById(R.id.tvWeekly);
        tvMonthly = v.findViewById(R.id.tvMonthly);

        tvSavings = v.findViewById(R.id.tvSavings);

        tvFeedback = v.findViewById(R.id.tvFeedback);

        tvLogout = v.findViewById(R.id.tvLogout);

        // ========== HISTORY ==========
        tvDaily.setOnClickListener(view ->
                new DailyHistoryBottomSheet().show(getParentFragmentManager(), "daily"));

        tvWeekly.setOnClickListener(view ->
                new WeeklyHistoryBottomSheet().show(getParentFragmentManager(), "weekly"));

        tvMonthly.setOnClickListener(view ->
                new MonthlyHistoryBottomSheet().show(getParentFragmentManager(), "monthly"));

        // ========== SAVINGS ==========
        tvSavings.setOnClickListener(view ->
                startActivity(new Intent(getActivity(), SavingsActivity.class)));

        // ========== FEEDBACK ==========
        tvFeedback.setOnClickListener(view ->
                new FeedbackBottomSheet().show(getParentFragmentManager(), "feedback"));

        // ========== LOGOUT ==========
        tvLogout.setOnClickListener(view -> {
            FirebaseAuth.getInstance().signOut();

            Intent i = new Intent(getActivity(), LoginScreen.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);

            requireActivity().finish();
        });

        // FIREBASE
        mAuth = FirebaseAuth.getInstance();
        userRef = FirebaseDatabase.getInstance()
                .getReference("Users")
                .child(mAuth.getCurrentUser().getUid());

        // ========== CURRENCY SPINNER ==========
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                getContext(),
                android.R.layout.simple_spinner_item,
                currencyList
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCurrency.setAdapter(adapter);

        loadCurrency();
        saveCurrency();

        return v;
    }

    private void loadCurrency() {
        userRef.child("currency").addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot s) {

                        String curr = s.getValue(String.class);
                        if (curr == null) curr = "LKR";

                        int pos = Arrays.asList(currencyList).indexOf(curr);
                        if (pos >= 0)
                            spCurrency.setSelection(pos);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) { }
                });
    }

    private void saveCurrency() {
        spCurrency.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent,
                                       View view,
                                       int position,
                                       long id) {

                if (isFirstLoad) {
                    isFirstLoad = false;
                    return;
                }

                String selected = currencyList[position];

                userRef.child("currency").setValue(selected);
                Toast.makeText(getContext(),
                        "Currency set to " + selected,
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });
    }
}
