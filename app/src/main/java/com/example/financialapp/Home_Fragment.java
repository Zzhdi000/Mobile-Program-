package com.example.financialapp;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

public class Home_Fragment extends Fragment {

    private TextView tvTotalBalance, tvTotalIncome, tvTotalExpense;
    private TextView tvHomeSavings;
    private ImageView ivChartPlaceholder;

    private ImageButton btnIncome, btnExpense, btnTransfer, btnScan;
    private ImageView ivProfile;

    private RecyclerView rvRecent;
    private TransactionAdapter adapter;
    private ArrayList<Datacash> recentList = new ArrayList<>();

    private FirebaseAuth mAuth;
    private DatabaseReference cashRef, userRef, savingsRef;

    private String currency = "LKR";

    public Home_Fragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_home_, container, false);

        // INIT UI
        tvTotalBalance = v.findViewById(R.id.TV_totalBalance);
        tvTotalIncome  = v.findViewById(R.id.TV_totalIncome);
        tvTotalExpense = v.findViewById(R.id.TV_totalExpense);
        tvHomeSavings  = v.findViewById(R.id.tvHomeSavings);

        ivChartPlaceholder = v.findViewById(R.id.iv_chart_placeholder);

        btnIncome   = v.findViewById(R.id.btn_quick_income);
        btnExpense  = v.findViewById(R.id.btn_quick_expense);
        btnTransfer = v.findViewById(R.id.btn_quick_transfer);
        btnScan     = v.findViewById(R.id.btn_quick_scan);

        ivProfile = v.findViewById(R.id.iv_profile);

        rvRecent = v.findViewById(R.id.rv_recent_transactions);
        rvRecent.setLayoutManager(new LinearLayoutManager(getContext()));

        mAuth = FirebaseAuth.getInstance();

        if (mAuth.getCurrentUser() == null) {
            startActivity(new Intent(getActivity(), LoginScreen.class));
            if (getActivity() != null) getActivity().finish();
            return v;
        }

        String uid = mAuth.getCurrentUser().getUid();

        cashRef = FirebaseDatabase.getInstance().getReference("Cashdata").child(uid);
        userRef = FirebaseDatabase.getInstance().getReference("Users").child(uid);
        savingsRef = FirebaseDatabase.getInstance().getReference("Savings").child(uid);

        // LOAD CURRENCY, THEN LOAD SUMMARY AND SAVINGS
        loadCurrency();

        // QUICK ACTION BUTTONS
        btnIncome.setOnClickListener(view ->
                startActivity(new Intent(getActivity(), IncomeActivity.class)));

        btnExpense.setOnClickListener(view ->
                startActivity(new Intent(getActivity(), ExpenseActivity.class)));

        btnTransfer.setOnClickListener(view ->
                Toast.makeText(getContext(), "Feature Coming Soon!", Toast.LENGTH_SHORT).show());

        btnScan.setOnClickListener(view ->
                Toast.makeText(getContext(), "Feature Coming Soon!", Toast.LENGTH_SHORT).show());

        // PROFILE → buka Profile_Fragment
        ivProfile.setOnClickListener(view -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).loadFragment(new Profile_Fragment());
            }
        });

        return v;
    }

    // LOAD CURRENCY FIRST
    private void loadCurrency() {
        CurrencyHelper.getCurrency(mAuth, c -> {
            if (c != null) currency = c;

            // setelah currency siap
            loadSummary();
            loadRecentTransactions();
            loadTotalSavings();
        });
    }

    // LOAD INCOME / EXPENSE / BALANCE
    private void loadSummary() {
        cashRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                long income = 0;
                long expense = 0;

                for (DataSnapshot s : snapshot.getChildren()) {
                    Datacash d = s.getValue(Datacash.class);
                    if (d == null) continue;

                    if ("income".equals(d.getType()))
                        income += d.getAmount();
                    else if ("expense".equals(d.getType()))
                        expense += d.getAmount();
                }

                tvTotalIncome.setText(currency + " " + income);
                tvTotalExpense.setText(currency + " " + expense);
                tvTotalBalance.setText(currency + " " + (income - expense));

                updateChartPlaceholder(income, expense);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    // CHART INDICATOR
    private void updateChartPlaceholder(long income, long expense) {
        if (income > expense)
            ivChartPlaceholder.setImageResource(R.drawable.chart_up);
        else if (expense > income)
            ivChartPlaceholder.setImageResource(R.drawable.chart_down);
        else
            ivChartPlaceholder.setImageResource(R.drawable.chart_equal);
    }

    // LOAD RECENT TRANSACTIONS
    private void loadRecentTransactions() {
        cashRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                recentList.clear();

                for (DataSnapshot s : snapshot.getChildren()) {
                    Datacash d = s.getValue(Datacash.class);
                    if (d != null) recentList.add(d);
                }

                Collections.reverse(recentList);

                ArrayList<Datacash> showList = recentList;
                if (recentList.size() > 3)
                    showList = new ArrayList<>(recentList.subList(0, 3));

                adapter = new TransactionAdapter(getContext(), showList, currency);
                rvRecent.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    // LOAD TOTAL SAVINGS
    private void loadTotalSavings() {
        savingsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                long total = 0;

                for (DataSnapshot s : snapshot.getChildren()) {
                    Savings sv = s.getValue(Savings.class);
                    if (sv != null) total += sv.getAmount();
                }

                tvHomeSavings.setText(currency + " " + total);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }
}
