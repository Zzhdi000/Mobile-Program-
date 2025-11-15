package com.example.financialapp;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

public class Transaction_Fragment extends Fragment {

    private RecyclerView rvTransaction;
    private FirebaseAuth mAuth;
    private DatabaseReference cashRef, userRef;

    private ArrayList<Datacash> list = new ArrayList<>();
    private String currency = "LKR";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_transaction, container, false);

        rvTransaction = v.findViewById(R.id.rvTransaction);
        rvTransaction.setLayoutManager(new LinearLayoutManager(getContext()));

        mAuth = FirebaseAuth.getInstance();

        cashRef = FirebaseDatabase.getInstance()
                .getReference("Cashdata")
                .child(mAuth.getCurrentUser().getUid());

        userRef = FirebaseDatabase.getInstance()
                .getReference("Users")
                .child(mAuth.getCurrentUser().getUid());

        loadCurrency();

        return v;
    }

    private void loadCurrency() {
        userRef.child("currency").get().addOnSuccessListener(s -> {
            if (s.exists()) currency = s.getValue(String.class);
            loadTransactions();
        });
    }

    private void loadTransactions() {
        cashRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                list.clear();

                for (DataSnapshot S : snapshot.getChildren()) {
                    Datacash d = S.getValue(Datacash.class);
                    if (d != null) list.add(d);
                }

                Collections.reverse(list);

                rvTransaction.setAdapter(
                        new TransactionAdapter(getContext(), list, currency)
                );
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }
}
