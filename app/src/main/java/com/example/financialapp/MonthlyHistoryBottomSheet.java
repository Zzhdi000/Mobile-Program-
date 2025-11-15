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
    private ArrayList<Datacash> list = new ArrayList<>();

    private FirebaseAuth mAuth;
    private DatabaseReference cashRef;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.history_list_bottomsheet, container, false);

        rv = v.findViewById(R.id.rvHistory);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));

        mAuth = FirebaseAuth.getInstance();
        cashRef = FirebaseDatabase.getInstance()
                .getReference("Cashdata")
                .child(mAuth.getCurrentUser().getUid());

        loadMonthly();

        return v;
    }

    private void loadMonthly() {

        int currentMonth = Calendar.getInstance().get(Calendar.MONTH) + 1;

        cashRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snap) {

                list.clear();

                for (DataSnapshot s : snap.getChildren()) {
                    Datacash d = s.getValue(Datacash.class);
                    if (d == null) continue;

                    if (d.getMonth() == currentMonth) {
                        list.add(d);
                    }
                }

                rv.setAdapter(new TransactionAdapter(getContext(), list, "IDR"));
            }

            @Override public void onCancelled(@NonNull DatabaseError error) {}
        });
    }
}
