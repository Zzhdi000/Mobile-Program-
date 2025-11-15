package com.example.financialapp;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Dashboard_Fragment extends Fragment {

    private FirebaseAuth mAuth;
    private DatabaseReference cashRef, limitRef;

    private String currency = "LKR";

    private TextView tvTotalIncome, tvTotalExpense;

    private TextView tvFoodLimit, tvFoodUsed;
    private ProgressBar pbFood;

    private TextView tvUtilityLimit, tvUtilityUsed;
    private ProgressBar pbUtility;

    private TextView tvHealthLimit, tvHealthUsed;
    private ProgressBar pbHealth;

    private TextView tvOtherLimit, tvOtherUsed;
    private ProgressBar pbOther;

    private CardView cardSetLimit;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_dashboard_, container, false);

        mAuth = FirebaseAuth.getInstance();

        if (mAuth.getCurrentUser() == null) {
            Toast.makeText(getContext(), "Please login again.", Toast.LENGTH_SHORT).show();
            return v;
        }

        cashRef = FirebaseDatabase.getInstance()
                .getReference("Cashdata")
                .child(mAuth.getCurrentUser().getUid());

        limitRef = FirebaseDatabase.getInstance()
                .getReference("BudgetLimit")
                .child(mAuth.getCurrentUser().getUid());

        tvTotalIncome = v.findViewById(R.id.TV_totalIncome);
        tvTotalExpense = v.findViewById(R.id.TV_totalExpense);

        tvFoodLimit = v.findViewById(R.id.tvFoodLimit);
        tvFoodUsed = v.findViewById(R.id.tvFoodUsed);
        pbFood = v.findViewById(R.id.pbFood);

        tvUtilityLimit = v.findViewById(R.id.tvUtilityLimit);
        tvUtilityUsed = v.findViewById(R.id.tvUtilityUsed);
        pbUtility = v.findViewById(R.id.pbUtility);

        tvHealthLimit = v.findViewById(R.id.tvHealthLimit);
        tvHealthUsed = v.findViewById(R.id.tvHealthUsed);
        pbHealth = v.findViewById(R.id.pbHealth);

        tvOtherLimit = v.findViewById(R.id.tvOtherLimit);
        tvOtherUsed = v.findViewById(R.id.tvOtherUsed);
        pbOther = v.findViewById(R.id.pbOther);

        cardSetLimit = v.findViewById(R.id.cardSetLimit);
        cardSetLimit.setOnClickListener(view ->
                startActivity(new android.content.Intent(getActivity(), SetlimitActivity.class))
        );

        loadCurrency();

        return v;
    }

    private void loadCurrency() {
        CurrencyHelper.getCurrency(mAuth, c -> {
            currency = c;
            loadSummary();
            loadAllLimits();
        });
    }

    private void loadSummary() {
        cashRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                int totInc = 0;
                int totExp = 0;

                for (DataSnapshot s : snapshot.getChildren()) {
                    Datacash d = s.getValue(Datacash.class);
                    if (d == null) continue;

                    if ("income".equals(d.getType()))
                        totInc += d.getAmount();
                    else if ("expense".equals(d.getType()))
                        totExp += d.getAmount();
                }

                tvTotalIncome.setText(currency + " " + totInc);
                tvTotalExpense.setText(currency + " " + totExp);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Firebase error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadAllLimits() {
        loadLimit("food", tvFoodLimit, tvFoodUsed, pbFood);
        loadLimit("utility", tvUtilityLimit, tvUtilityUsed, pbUtility);
        loadLimit("health", tvHealthLimit, tvHealthUsed, pbHealth);
        loadLimit("other", tvOtherLimit, tvOtherUsed, pbOther);
    }

    private void loadLimit(String category, TextView tvLimit, TextView tvUsed, ProgressBar pb) {

        limitRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snap) {

                int limit = 0;

                for (DataSnapshot s : snap.getChildren()) {
                    BudgetLimit bl = s.getValue(BudgetLimit.class);
                    if (bl == null || bl.getCategory() == null) continue;

                    if (category.equalsIgnoreCase(bl.getCategory()))
                        limit = bl.getAmount();
                }

                tvLimit.setText("Limit: " + currency + " " + limit);
                int finalLimit = limit;

                cashRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot s2) {

                        int used = 0;

                        for (DataSnapshot s : s2.getChildren()) {
                            Datacash d = s.getValue(Datacash.class);
                            if (d == null || d.getCategory() == null) continue;

                            if ("expense".equals(d.getType()) &&
                                    category.equalsIgnoreCase(d.getCategory()))
                                used += d.getAmount();
                        }

                        tvUsed.setText("Used: " + currency + " " + used);

                        // =====================================================
                        //     WARNING + DISABLE SET LIMIT (AMANKAN)
                        // =====================================================

                        if (finalLimit > 0 && used >= finalLimit) {

                            // Disable tombol Set Limit
                            cardSetLimit.setEnabled(false);
                            cardSetLimit.setAlpha(0.5f);

                            // Tampilkan 1x/hari
                            if (getContext() != null &&
                                    LimitWarningHelper.shouldShow(getContext(), category)) {

                                Toast.makeText(
                                        getContext(),
                                        "Warning: limit for " + category + " reached!",
                                        Toast.LENGTH_SHORT
                                ).show();

                                if (getActivity() != null && isAdded()) {
                                    new androidx.appcompat.app.AlertDialog.Builder(getContext())
                                            .setTitle("Limit Reached")
                                            .setMessage("You have reached the limit for " + category + ".")
                                            .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                                            .show();
                                }

                                LimitWarningHelper.setShown(getContext(), category);
                            }

                        } else {
                            // Limit BELUM tercapai → tombol aktif normal
                            cardSetLimit.setEnabled(true);
                            cardSetLimit.setAlpha(1f);
                        }

                        updateProgressBar(finalLimit, used, pb);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void updateProgressBar(int limit, int used, ProgressBar pb) {

        if (pb == null) return;

        int percent = 0;
        if (limit > 0)
            percent = (used * 100) / limit;

        pb.setProgress(percent);

        try {
            if (percent < 75)
                pb.getProgressDrawable().setTint(Color.parseColor("#6C5CE7"));
            else if (percent < 100)
                pb.getProgressDrawable().setTint(Color.parseColor("#E67E22"));
            else
                pb.getProgressDrawable().setTint(Color.parseColor("#E74C3C"));

        } catch (Exception ignored) {}
    }
}
