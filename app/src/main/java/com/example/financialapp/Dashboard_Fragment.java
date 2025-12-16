package com.example.financialapp;

import android.content.SharedPreferences;
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
    private TextView tvBillsLimit, tvBillsUsed;
    private ProgressBar pbBills;

    private TextView tvShoppingLimit, tvShoppingUsed;
    private ProgressBar pbShopping;

    private TextView tvTransportLimit, tvTransportUsed;
    private ProgressBar pbTransport;

    private SharedPreferences prefs;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_dashboard_, container, false);

        mAuth = FirebaseAuth.getInstance();

        cashRef = FirebaseDatabase.getInstance()
                .getReference("Cashdata")
                .child(mAuth.getCurrentUser().getUid());

        limitRef = FirebaseDatabase.getInstance()
                .getReference("BudgetLimit")
                .child(mAuth.getCurrentUser().getUid());

        prefs = requireActivity().getSharedPreferences("LimitPopup", requireContext().MODE_PRIVATE);

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

        tvBillsLimit = v.findViewById(R.id.tvBillsLimit);
        tvBillsUsed = v.findViewById(R.id.tvBillsUsed);
        pbBills = v.findViewById(R.id.pbBills);

        tvShoppingLimit = v.findViewById(R.id.tvShoppingLimit);
        tvShoppingUsed = v.findViewById(R.id.tvShoppingUsed);
        pbShopping = v.findViewById(R.id.pbShopping);

        tvTransportLimit = v.findViewById(R.id.tvTransportLimit);
        tvTransportUsed = v.findViewById(R.id.tvTransportUsed);
        pbTransport = v.findViewById(R.id.pbTransport);

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

                long totInc = 0;
                long totExp = 0;

                for (DataSnapshot s : snapshot.getChildren()) {
                    Datacash d = s.getValue(Datacash.class);
                    if (d == null) continue;

                    if (d.getType().equals("income"))
                        totInc += d.getAmount();
                    else if (d.getType().equals("expense"))
                        totExp += d.getAmount();
                }

                String symbol = NumberFormatHelper.getCurrencySymbol(currency);
                String incF = NumberFormatHelper.formatCurrency(currency, totInc);
                String expF = NumberFormatHelper.formatCurrency(currency, totExp);

                tvTotalIncome.setText(symbol + " " + incF);
                tvTotalExpense.setText(symbol + " " + expF);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void loadAllLimits() {
        loadLimit("food", tvFoodLimit, tvFoodUsed, pbFood);
        loadLimit("utility", tvUtilityLimit, tvUtilityUsed, pbUtility);
        loadLimit("health", tvHealthLimit, tvHealthUsed, pbHealth);
        loadLimit("bills", tvBillsLimit, tvBillsUsed, pbBills);
        loadLimit("shopping", tvShoppingLimit, tvShoppingUsed, pbShopping);
        loadLimit("transport", tvTransportLimit, tvTransportUsed, pbTransport);
        loadLimit("other", tvOtherLimit, tvOtherUsed, pbOther);
    }

    private void loadLimit(String category, TextView tvLimit, TextView tvUsed, ProgressBar pb) {

        limitRef.addValueEventListener(new ValueEventListener() {
            @Override public void onDataChange(@NonNull DataSnapshot snap) {

                long limit = 0;

                for (DataSnapshot s : snap.getChildren()) {
                    BudgetLimit bl = s.getValue(BudgetLimit.class);
                    if (bl != null && bl.getCategory().equalsIgnoreCase(category))
                        limit = bl.getAmount();
                }

                String symbol = NumberFormatHelper.getCurrencySymbol(currency);
                String limitF = NumberFormatHelper.formatCurrency(currency, limit);

                tvLimit.setText("Limit: " + symbol + " " + limitF);

                long finalLimit = limit;

                cashRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override public void onDataChange(@NonNull DataSnapshot s2) {

                        long used = 0;

                        for (DataSnapshot s : s2.getChildren()) {
                            Datacash d = s.getValue(Datacash.class);

                            if (d != null &&
                                    d.getType().equals("expense") &&
                                    d.getCategory().equalsIgnoreCase(category)) {

                                used += d.getAmount();
                            }
                        }

                        String usedF = NumberFormatHelper.formatCurrency(currency, used);
                        tvUsed.setText("Used: " + symbol + " " + usedF);

                        updateProgressBar((int) finalLimit, (int) used, pb);
                    }

                    @Override public void onCancelled(@NonNull DatabaseError error) {}
                });
            }

            @Override public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void updateProgressBar(int limit, int used, ProgressBar pb) {

        int percent = (limit > 0) ? (used * 100 / limit) : 0;

        pb.setProgress(percent);

        if (percent < 75)
            pb.getProgressDrawable().setTint(Color.parseColor("#6C5CE7"));
        else if (percent < 100)
            pb.getProgressDrawable().setTint(Color.parseColor("#E67E22"));
        else
            pb.getProgressDrawable().setTint(Color.parseColor("#E74C3C"));
    }
}