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

import java.text.SimpleDateFormat;
import java.util.*;

public class WeeklyHistoryBottomSheet extends BottomSheetDialogFragment {

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

        loadCurrencyThenWeekly();

        return v;
    }

    private void loadCurrencyThenWeekly() {
        userRef.child("currency").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override public void onDataChange(@NonNull DataSnapshot ds) {
                if (ds.exists()) currency = ds.getValue(String.class);
                loadWeekly();
            }
            @Override public void onCancelled(@NonNull DatabaseError e) {}
        });
    }

    private void loadWeekly() {

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, -7);
        Date last7 = cal.getTime();

        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());

        cashRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snap) {

                finalList.clear();
                finalList.add(new HistoryHeader("Last 7 Days"));

                for (DataSnapshot s : snap.getChildren()) {

                    Datacash d = s.getValue(Datacash.class);
                    if (d == null) continue;

                    try {
                        Date date = df.parse(d.getDate());
                        if (date != null && !date.before(last7)) {
                            finalList.add(d);
                        }
                    } catch (Exception ignore) {}
                }

                rv.setAdapter(new HistoryUnifiedAdapter(getContext(), finalList, currency));
            }

            @Override public void onCancelled(@NonNull DatabaseError e) {}
        });
    }
}
