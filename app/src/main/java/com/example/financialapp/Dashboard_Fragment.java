package com.example.financialapp;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class Dashboard_Fragment extends Fragment {

    private ProgressBar progressBarFood, progressBarUtility, progressBarHealthCare, progressBarOthers, progressBarExpense;
    private TextView valueFood, valueUtility, valuesHealthCare, valueOthers, valueTotalexpense, valueTotalIncome;
    private Button btn_setLimit;

    private FirebaseAuth mAuth;
    private DatabaseReference dbRef, budgetdbRef;

    int foodbev = 0;
    int utilities = 0;
    int healthCare = 0;
    int others = 0;

    int foodCatAmount;
    int otherCatAmount;
    int utilityCatAmount;
    int healthCatAmount;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mAuth = FirebaseAuth.getInstance();
        dbRef = FirebaseDatabase.getInstance().getReference().child("Cashdata").child(mAuth.getCurrentUser().getUid());
        budgetdbRef = FirebaseDatabase.getInstance().getReference().child("BudgetLimit").child(mAuth.getCurrentUser().getUid());

        View view = inflater.inflate(R.layout.fragment_dashboard_, container, false);

        btn_setLimit = view.findViewById(R.id.btn_setLimit);

        progressBarFood = view.findViewById(R.id.pro_foofBrev);
        progressBarHealthCare = view.findViewById(R.id.pro_healthCare);
        progressBarUtility = view.findViewById(R.id.pro_utility);
        progressBarOthers = view.findViewById(R.id.pro_other);
        progressBarExpense = view.findViewById(R.id.pro_totalExpense);

        valueOthers = view.findViewById(R.id.otherVale);
        valueFood = view.findViewById(R.id.foodbevValue);
        valuesHealthCare = view.findViewById(R.id.healthCareValue);
        valueUtility = view.findViewById(R.id.utilityValue);
        valueTotalexpense = view.findViewById(R.id.totalExpenseValue);
        valueTotalIncome = view.findViewById(R.id.TF_totalIncome);

        btn_setLimit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), SetlimitActivity.class);
                startActivity(i);
            }
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int totalIncome = 0;
                int totalExpense = 0;

                String type = null;
                String categoryType = null;

                for (DataSnapshot snap : snapshot.getChildren()) {
                    Datacash datacash = snap.getValue(Datacash.class);

                    type = datacash.getType();
                    categoryType = datacash.getCategoryData();

                    if (type.equals("income")) {
                        totalIncome += datacash.getAmount();
                    }
                    if (type.equals("expense")) {
                        totalExpense += datacash.getAmount();
                    }

                    if (categoryType.equals("Food & Beverages")) {
                        foodbev += datacash.getAmount();
                    }
                    if (categoryType.equals("Utilities")) {
                        utilities += datacash.getAmount();
                    }
                    if (categoryType.equals("Health Care")) {
                        healthCare += datacash.getAmount();
                    }
                    if (categoryType.equals("Others")) {
                        others += datacash.getAmount();
                    }
                }

                int foodPres = foodbev * 100 / totalExpense;
                int utilityPres = utilities * 100 / totalExpense;
                int healthPres = healthCare * 100 / totalExpense;
                int otherPres = others * 100 / totalExpense;
                int expensePres = totalExpense * 100 / totalIncome;

                budgetdbRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        String categoryType;

                        int amountCategory;

                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            BudgetLimit budgetLimit = dataSnapshot.getValue(BudgetLimit.class);

                            categoryType = budgetLimit.getCategory();

                            if (categoryType.equals("food")) {
                                foodCatAmount = budgetLimit.getAmount();
                            }
                            if (categoryType.equals("other")) {
                                otherCatAmount = budgetLimit.getAmount();
                            }
                            if (categoryType.equals("health")) {
                                healthCatAmount = budgetLimit.getAmount();
                            }
                            if (categoryType.equals("utility")) {
                                utilityCatAmount = budgetLimit.getAmount();
                            }
                        }

                        valueFood.setText(String.valueOf(foodPres) + "% | " + "LKR " + String.valueOf(foodbev) + ".00 | Limit - " + String.valueOf(foodCatAmount));
                        valueOthers.setText(String.valueOf(otherPres) + "% | " + "LKR " + String.valueOf(others) + ".00 | Limit - " + String.valueOf(otherCatAmount));
                        valuesHealthCare.setText(String.valueOf(healthPres) + "% | " + "LKR" + String.valueOf(healthCare) + ".00 | Limit - " + String.valueOf(healthCatAmount));
                        valueUtility.setText(String.valueOf(utilityPres) + "% | " + "LKR " + String.valueOf(utilities) + ".00 | Limit - " + String.valueOf(utilityCatAmount));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


                valueTotalexpense.setText(String.valueOf(expensePres) + "% | " + "LKR " + String.valueOf(totalExpense) + ".00");
                valueTotalIncome.setText("LKR " + String.valueOf(totalIncome) + ".00");

                progressBarFood.setProgress(foodPres);
                progressBarHealthCare.setProgress(healthPres);
                progressBarOthers.setProgress(otherPres);
                progressBarUtility.setProgress(utilityPres);
                progressBarExpense.setProgress(expensePres);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}