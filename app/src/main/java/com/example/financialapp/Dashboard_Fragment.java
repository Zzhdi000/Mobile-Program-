package com.example.financialapp;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

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

    // Anti-spam popup
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

        // SharedPreferences
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

    @Override
    public void onResume() {
        super.onResume();
        checkLimitWarning();
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

                int limit = 0;

                for (DataSnapshot s : snap.getChildren()) {
                    BudgetLimit bl = s.getValue(BudgetLimit.class);
                    if (bl != null && category.equalsIgnoreCase(bl.getCategory()))
                        limit = bl.getAmount();
                }

                tvLimit.setText("Limit: " + currency + " " + limit);

                int finalLimit = limit;

                cashRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override public void onDataChange(@NonNull DataSnapshot s2) {

                        int used = 0;

                        for (DataSnapshot s : s2.getChildren()) {
                            Datacash d = s.getValue(Datacash.class);

                            if (d != null &&
                                    "expense".equals(d.getType()) &&
                                    category.equalsIgnoreCase(d.getCategory())) {

                                used += d.getAmount();
                            }
                        }

                        tvUsed.setText("Used: " + currency + " " + used);
                        updateProgressBar(finalLimit, used, pb);
                    }

                    @Override public void onCancelled(@NonNull DatabaseError error) {}
                });
            }

            @Override public void onCancelled(@NonNull DatabaseError error) {}
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

    // ==========================================================================
    //                         LIMIT WARNING POPUP (ANTI SPAM)
    // ==========================================================================
    private void checkLimitWarning() {

        int month = Calendar.getInstance().get(Calendar.MONTH) + 1;

        limitRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override public void onDataChange(@NonNull DataSnapshot limitSnap) {

                for (DataSnapshot L : limitSnap.getChildren()) {

                    BudgetLimit bl = L.getValue(BudgetLimit.class);
                    if (bl == null) continue;

                    String category = bl.getCategory();
                    int setLimit = bl.getAmount();

                    cashRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override public void onDataChange(@NonNull DataSnapshot cashSnap) {

                            int used = 0;

                            for (DataSnapshot c : cashSnap.getChildren()) {
                                Datacash d = c.getValue(Datacash.class);

                                if (d != null &&
                                        d.getMonth() == month &&
                                        "expense".equals(d.getType()) &&
                                        category.equalsIgnoreCase(d.getCategory())) {

                                    used += d.getAmount();
                                }
                            }

                            if (used >= setLimit && setLimit > 0) {
                                if (shouldShowPopup(category)) {
                                    showPopup(category, setLimit, used);
                                    disablePopupForToday(category);
                                }
                            }
                        }

                        @Override public void onCancelled(@NonNull DatabaseError error) {}
                    });
                }
            }

            @Override public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void showPopup(String category, int limit, int used) {
        new AlertDialog.Builder(requireContext())
                .setTitle("⚠ Limit Reached")
                .setMessage(
                        "Your " + category + " spending has reached the limit.\n\n" +
                                "Limit : " + currency + " " + limit + "\n" +
                                "Current: " + currency + " " + used
                )
                .setPositiveButton("OK", null)
                .show();
    }

    private boolean shouldShowPopup(String category) {
        String key = category + "_" + getToday();
        return prefs.getBoolean(key, true);
    }

    private void disablePopupForToday(String category) {
        String key = category + "_" + getToday();
        prefs.edit().putBoolean(key, false).apply();
    }

    private String getToday() {
        return new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
                .format(new Date());
    }
}
