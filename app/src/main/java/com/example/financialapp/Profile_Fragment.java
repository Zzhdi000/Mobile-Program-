package com.example.financialapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

import java.util.Arrays;

public class Profile_Fragment extends Fragment {

    private Spinner spCurrency;
    private TextView  tvLogout, txtName, txtEmail;
    private LinearLayout btnEdit, btnSettings, btnHistory, btnSavings, btnfeedback;
    private FirebaseAuth mAuth;
    private DatabaseReference userRef;

    String[] currencyList = {"LKR", "IDR", "USD", "MYR"};
    boolean isFirstLoad = true;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_profile_, container, false);

        // INIT UI
        txtName = v.findViewById(R.id.txtName);
        txtEmail = v.findViewById(R.id.txtEmail);

        spCurrency = v.findViewById(R.id.spCurrency);
        btnfeedback = v.findViewById(R.id.btnFeedback);
        tvLogout = v.findViewById(R.id.btn_Logout);

        btnEdit = v.findViewById(R.id.btnEdit);
        btnSettings = v.findViewById(R.id.btnSettings);
        btnHistory = v.findViewById(R.id.btnHistory);
        btnSavings = v.findViewById(R.id.btnSavings);

        // FIREBASE
        mAuth = FirebaseAuth.getInstance();
        userRef = FirebaseDatabase.getInstance()
                .getReference("Users")
                .child(mAuth.getCurrentUser().getUid());

        // LOAD USER INFO
        loadUserInfo();

        // SETUP SPINNER
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                getContext(),
                android.R.layout.simple_spinner_item,
                currencyList
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCurrency.setAdapter(adapter);

        loadCurrency();   // load from database
        saveCurrency();   // save on change

        // BUTTON FUNCTIONS
        btnEdit.setOnClickListener(v1 -> {
            Intent intent = new Intent(getActivity(), EditProfileActivity.class);
            startActivity(intent);
        });


        btnSettings.setOnClickListener(v12 -> {
            Intent intent = new Intent(getActivity(), SettingsActivity.class);
            startActivity(intent);
        });


        btnHistory.setOnClickListener(v13 ->
                new HistoryMenuBottomSheet().show(getParentFragmentManager(), "history")
        );

        btnSavings.setOnClickListener(v14 ->
                startActivity(new Intent(getActivity(), SavingsActivity.class))
        );

        btnfeedback.setOnClickListener(v15 ->
                new FeedbackBottomSheet().show(getParentFragmentManager(), "feedback")
        );

        // LOGOUT
        tvLogout.setOnClickListener(v16 -> {
            mAuth.signOut();
            startActivity(new Intent(getActivity(), LoginScreen.class));
            getActivity().finish();
        });

        return v;
    }

    // ========================= LOAD USER INFO =============================
    private void loadUserInfo() {
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                String name = snapshot.child("name").getValue(String.class);
                String email = snapshot.child("email").getValue(String.class);

                // CEK NULL AGAR TIDAK ERROR
                if (name != null) txtName.setText(name);
                if (email != null) txtEmail.setText(email);
            }

            @Override public void onCancelled(@NonNull DatabaseError error) { }
        });
    }

    // ========================= LOAD CURRENCY =============================
    private void loadCurrency() {
        userRef.child("currency").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String curr = snapshot.getValue(String.class);

                if (curr == null) curr = "LKR";

                int pos = Arrays.asList(currencyList).indexOf(curr);

                if (pos >= 0) {
                    spCurrency.setSelection(pos); // SET SPINNER KE VALUE DB
                }

                isFirstLoad = true;  // agar tidak langsung trigger save
            }

            @Override public void onCancelled(@NonNull DatabaseError error) { }
        });
    }

    // ========================= SAVE CURRENCY =============================
    private void saveCurrency() {
        spCurrency.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (isFirstLoad) {
                    isFirstLoad = false;
                    return;
                }

                String selected = currencyList[position];

                userRef.child("currency").setValue(selected)
                        .addOnSuccessListener(unused ->
                                Toast.makeText(getContext(), "Currency updated!", Toast.LENGTH_SHORT).show()
                        )
                        .addOnFailureListener(e ->
                                Toast.makeText(getContext(), "Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                        );
            }

            @Override public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }
}
