package com.example.financialapp;

import android.content.Intent;
import android.graphics.Color;
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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Calendar;
import java.util.HashMap;

public class Home_Fragment extends Fragment {

    private TextView tvTotalBalance, tvTotalIncome, tvTotalExpense, tvHomeSavings, btnSeeAll, btn_savings_manage;
    private ImageButton btnIncome, btnExpense, btnTransfer, btnScan;
    private ImageView ivProfile;

    private RecyclerView rvRecent;
    private TransactionAdapter adapter;
    private ArrayList<Datacash> recentList = new ArrayList<>();

    private FirebaseAuth mAuth;
    private DatabaseReference cashRef, savingsRef, limitRef;

    private String currency = "LKR";

    private LineChart lineChart;
    private View rootView;

    private HashMap<String, Long> limitMap = new HashMap<>();
    private HashMap<String, Long> usedMap = new HashMap<>();

    public Home_Fragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_home_, container, false);
        rootView = v;

        // UI INIT
        tvTotalBalance = v.findViewById(R.id.TV_totalBalance);
        tvTotalIncome  = v.findViewById(R.id.TV_totalIncome);
        tvTotalExpense = v.findViewById(R.id.TV_totalExpense);
        tvHomeSavings  = v.findViewById(R.id.tvTotalSavings);
        btn_savings_manage = v.findViewById(R.id.btn_savings_manage);
        btnSeeAll = v.findViewById(R.id.btn_see_all);

        btnIncome   = v.findViewById(R.id.btn_quick_income);
        btnExpense  = v.findViewById(R.id.btn_quick_expense);
        btnTransfer = v.findViewById(R.id.btn_quick_transfer);
        btnScan     = v.findViewById(R.id.btn_quick_scan);
        ivProfile = v.findViewById(R.id.iv_profile);

        rvRecent = v.findViewById(R.id.rv_recent_transactions);
        rvRecent.setLayoutManager(new LinearLayoutManager(getContext()));

        lineChart = v.findViewById(R.id.lineChartHome);
        setupChart();

        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() == null) return v;

        String uid = mAuth.getCurrentUser().getUid();

        cashRef = FirebaseDatabase.getInstance().getReference("Cashdata").child(uid);
        savingsRef = FirebaseDatabase.getInstance().getReference("Savings").child(uid);
        limitRef = FirebaseDatabase.getInstance().getReference("BudgetLimit").child(uid);

        setupButtons();
        loadCurrency();

        // LISTEN REALTIME FOR BALANCE & LIMIT CHECK
        listenCashChanges();
        listenLimitChanges();

        // Recent & Savings tetap realtime
        loadRecentTransactions();
        loadTotalSavings();

        return v;
    }

    // ========================= BUTTONS =========================
    private void setupButtons() {
        btnIncome.setOnClickListener(v ->
                startActivity(new Intent(getActivity(), IncomeActivity.class)));

        btnExpense.setOnClickListener(v ->
                startActivity(new Intent(getActivity(), ExpenseActivity.class)));

        btnTransfer.setOnClickListener(v -> {
            TransferBottomSheet sheet = new TransferBottomSheet();
            sheet.show(getChildFragmentManager(), "TransferSheet");
        });

        btnScan.setOnClickListener(v ->
                startActivity(new Intent(getActivity(), ScanActivity.class)));

        btn_savings_manage.setOnClickListener(v ->
                startActivity(new Intent(getActivity(), SavingsActivity.class)));

        ivProfile.setOnClickListener(v -> {
            if (getActivity() instanceof MainActivity)
                ((MainActivity)getActivity()).loadFragment(new Profile_Fragment());
        });

        btnSeeAll.setOnClickListener(v -> {
            if (getActivity() instanceof MainActivity)
                ((MainActivity)getActivity()).loadFragment(new Transaction_Fragment());
        });
    }

    // ========================= LOAD CURRENCY =========================
    private void loadCurrency() {
        CurrencyHelper.getCurrency(mAuth, c -> {
            if (c != null) currency = c;
        });
    }

    // =================================================================
    // -------------------- 1. LISTEN CASHDATA REALTIME ----------------
    // =================================================================
    private void listenCashChanges() {

        cashRef.addValueEventListener(new ValueEventListener() {
            @Override public void onDataChange(@NonNull DataSnapshot snapshot) {

                long income = 0, expense = 0;

                usedMap.clear();

                long[] incomeMonth = new long[12];
                long[] expenseMonth = new long[12];

                for (DataSnapshot s : snapshot.getChildren()) {

                    Datacash d = s.getValue(Datacash.class);
                    if (d == null) continue;

                    int m = d.getMonth();

                    if ("income".equals(d.getType())) {
                        income += d.getAmount();

                        if (m >= 1 && m <= 12)
                            incomeMonth[m - 1] += d.getAmount();
                    }

                    if ("expense".equals(d.getType())) {

                        expense += d.getAmount();

                        String cat = d.getCategory().toLowerCase();
                        usedMap.put(cat, usedMap.getOrDefault(cat, 0L) + d.getAmount());

                        if (m >= 1 && m <= 12)
                            expenseMonth[m - 1] += d.getAmount();
                    }
                }

                updateBalanceUI(income, expense);
                updateChart(incomeMonth, expenseMonth);
                checkLimitExceeded();
            }

            @Override public void onCancelled(@NonNull DatabaseError error) { }
        });
    }

    // =================================================================
    // -------------------- 2. LISTEN LIMIT CHANGES ---------------------
    // =================================================================
    private void listenLimitChanges() {

        limitRef.addValueEventListener(new ValueEventListener() {
            @Override public void onDataChange(@NonNull DataSnapshot snapshot) {

                limitMap.clear();

                for (DataSnapshot s : snapshot.getChildren()) {
                    BudgetLimit bl = s.getValue(BudgetLimit.class);
                    if (bl != null)
                        limitMap.put(bl.getCategory().toLowerCase(), (long) bl.getAmount());
                }

                // re-evaluate limit warning
                checkLimitExceeded();
            }

            @Override public void onCancelled(@NonNull DatabaseError error) { }
        });
    }

    // ========================= UPDATE UI =========================
    private void updateBalanceUI(long income, long expense) {

        String sym = NumberFormatHelper.getCurrencySymbol(currency);

        tvTotalIncome.setText(sym + " " + NumberFormatHelper.formatCurrency(currency, income));
        tvTotalExpense.setText(sym + " " + NumberFormatHelper.formatCurrency(currency, expense));
        tvTotalBalance.setText(sym + " " + NumberFormatHelper.formatCurrency(currency, income - expense));
    }

    // ========================= LIMIT CHECK =========================
    private void checkLimitExceeded() {

        int warn = 0;

        for (String cat : limitMap.keySet()) {
            long limit = limitMap.get(cat);
            long used = usedMap.getOrDefault(cat, 0L);

            if (used > limit)
                warn++;
        }

        if (warn > 0)
            showInlineNotification("You exceeded limits in " + warn + " categories.");
        else
            hideInlineNotification();
    }

    private void showInlineNotification(String msg) {
        LinearLayout layout = rootView.findViewById(R.id.layout_notification);
        TextView tv = rootView.findViewById(R.id.tv_notification);

        layout.setVisibility(View.VISIBLE);
        tv.setText(msg);
    }

    private void hideInlineNotification() {
        rootView.findViewById(R.id.layout_notification).setVisibility(View.GONE);
    }

    // ========================= RECENT =========================
    private void loadRecentTransactions() {
        cashRef.addValueEventListener(new ValueEventListener() {
            @Override public void onDataChange(@NonNull DataSnapshot snapshot) {

                recentList.clear();

                for (DataSnapshot s : snapshot.getChildren()) {
                    Datacash d = s.getValue(Datacash.class);
                    if (d != null) recentList.add(d);
                }

                Collections.reverse(recentList);

                ArrayList<Datacash> showList =
                        recentList.size() > 3 ?
                                new ArrayList<>(recentList.subList(0, 3)) :
                                recentList;

                adapter = new TransactionAdapter(getContext(), showList, currency);
                rvRecent.setAdapter(adapter);
            }

            @Override public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    // ========================= SAVINGS =========================
    private void loadTotalSavings() {
        savingsRef.addValueEventListener(new ValueEventListener() {
            @Override public void onDataChange(@NonNull DataSnapshot snapshot) {

                long total = 0;

                for (DataSnapshot s : snapshot.getChildren()) {
                    Savings sv = s.getValue(Savings.class);
                    if (sv != null) total += sv.getAmount();
                }

                String sym = NumberFormatHelper.getCurrencySymbol(currency);
                tvHomeSavings.setText(sym + " " + NumberFormatHelper.formatCurrency(currency, total));
            }

            @Override public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    // ========================= CHART =========================
    private void setupChart() {

        lineChart.setDrawGridBackground(false);
        lineChart.getDescription().setEnabled(false);
        lineChart.getLegend().setEnabled(false);

        XAxis x = lineChart.getXAxis();
        x.setPosition(XAxis.XAxisPosition.BOTTOM);
        x.setGranularity(1f);
        x.setLabelRotationAngle(45);

        x.setValueFormatter(new IndexAxisValueFormatter(
                new String[]{"Jan","Feb","Mar","Apr","May","Jun",
                        "Jul","Aug","Sep","Oct","Nov","Dec"}));

        YAxis left = lineChart.getAxisLeft();
        left.setDrawGridLines(true);
        lineChart.getAxisRight().setEnabled(false);
    }

    private void updateChart(long[] income, long[] expense) {

        ArrayList<Entry> list = new ArrayList<>();

        for (int i = 0; i < 12; i++) {
            long net = income[i] - expense[i];
            list.add(new Entry(i, net));
        }

        LineDataSet set = new LineDataSet(list, "");
        set.setLineWidth(2f);
        set.setCircleRadius(4f);
        set.setColor(Color.parseColor("#2980B9"));
        set.setCircleColor(Color.parseColor("#2980B9"));
        set.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        set.setDrawFilled(true);
        set.setFillColor(Color.parseColor("#552980B9"));

        lineChart.setData(new LineData(set));
        lineChart.invalidate();
    }
}
