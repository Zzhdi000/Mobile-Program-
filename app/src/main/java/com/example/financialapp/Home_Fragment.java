package com.example.financialapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class Home_Fragment extends Fragment implements View.OnClickListener {

    private CardView incomeCardView, expensessCardView;
    private TextView totalIncome, totalExpense;

    private FirebaseAuth mAuth;
    private DatabaseReference incomeRef;
    private ProgressDialog progressDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_, container, false);

        incomeCardView = (CardView) view.findViewById(R.id.CD_Income);
        expensessCardView = (CardView) view.findViewById(R.id.CD_Expenses);
        totalIncome = (TextView) view.findViewById(R.id.TF_incomeValue);
        totalExpense = (TextView) view.findViewById(R.id.TF_expenses);

        mAuth = FirebaseAuth.getInstance();
        incomeRef = FirebaseDatabase.getInstance().getReference().child("Cashdata").child(mAuth.getCurrentUser().getUid());

        incomeCardView.setOnClickListener(this);
        expensessCardView.setOnClickListener(this);

        incomeCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), IncomeActivity.class);
                startActivity(i);
            }
        });

        expensessCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), ExpenseActivity.class);
                startActivity(i);
            }
        });

        return view;
    }


    @Override
    public void onClick(View v) {

    }

    @Override
    public void onStart() {
        super.onStart();

        incomeRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int totalIncomeint = 0;
                int totalExpenseint = 0;
                String stotalIncome= null;
                String stotalExpense= null;
                String type = null;

                for (DataSnapshot snap : snapshot.getChildren()) {
                    Datacash data = snap.getValue(Datacash.class);

                    type = data.getType();

                    if (type.equals("income")) {
                        totalIncomeint += data.getAmount();
                    }
                    if(type.equals("expense")){
                        totalExpenseint += data.getAmount();
                    }
                }
                stotalIncome = String.valueOf(totalIncomeint);
                stotalExpense = String.valueOf(totalExpenseint);
                totalIncome.setText("LKR "+stotalIncome + ".00");
                totalExpense.setText("LKR "+stotalExpense + ".00");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}