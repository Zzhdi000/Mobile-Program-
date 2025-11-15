package com.example.financialapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class HistoryMenuBottomSheet extends BottomSheetDialogFragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.history_bottomsheet, container, false);

        Button btnDaily = v.findViewById(R.id.btnDaily);
        Button btnWeekly = v.findViewById(R.id.btnWeekly);
        Button btnMonthly = v.findViewById(R.id.btnMonthly);

        // ========================
        // OPEN DAILY HISTORY
        // ========================
        btnDaily.setOnClickListener(view -> {
            dismiss();
            new DailyHistoryBottomSheet().show(getParentFragmentManager(), "daily");
        });

        // ========================
        // OPEN WEEKLY HISTORY
        // ========================
        btnWeekly.setOnClickListener(view -> {
            dismiss();
            new WeeklyHistoryBottomSheet().show(getParentFragmentManager(), "weekly");
        });

        // ========================
        // OPEN MONTHLY HISTORY
        // ========================
        btnMonthly.setOnClickListener(view -> {
            dismiss();
            new MonthlyHistoryBottomSheet().show(getParentFragmentManager(), "monthly");
        });

        return v;
    }
}
