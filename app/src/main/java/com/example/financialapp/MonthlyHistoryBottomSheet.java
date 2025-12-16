package com.example.financialapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.Calendar;

public class MonthlyHistoryBottomSheet extends BottomSheetDialogFragment {

    private RecyclerView rv;
    private ArrayList<Object> finalList = new ArrayList<>();

    private FirebaseAuth mAuth;
    private DatabaseReference cashRef, userRef;

    private String currency = "IDR";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.history_list_bottomsheet, container, false);

        rv = v.findViewById(R.id.rvHistory);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));

        mAuth = FirebaseAuth.getInstance();
        String uid = mAuth.getCurrentUser().getUid();

        cashRef = FirebaseDatabase.getInstance()
                .getReference("Cashdata")
                .child(uid);

        userRef = FirebaseDatabase.getInstance()
                .getReference("Users")
                .child(uid);

        loadCurrencyThenMonthly();

        return v;
    }

    private void loadCurrencyThenMonthly() {
        userRef.child("currency").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override public void onDataChange(@NonNull DataSnapshot ds) {
                if (ds.exists()) currency = ds.getValue(String.class);
                loadMonthly();
            }
            @Override public void onCancelled(@NonNull DatabaseError e) {}
        });
    }

    private void loadMonthly() {

        int currentMonth = Calendar.getInstance().get(Calendar.MONTH) + 1;

        cashRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snap) {

                finalList.clear();
                finalList.add(new HistoryHeader("This Month"));

                for (DataSnapshot s : snap.getChildren()) {
                    Datacash d = s.getValue(Datacash.class);

                    if (d != null && d.getMonth() == currentMonth) {
                        finalList.add(d);
                    }
                }

                rv.setAdapter(new HistoryUnifiedAdapter(getContext(), finalList, currency));
            }

            @Override public void onCancelled(@NonNull DatabaseError error) {}
        });
    }
}
