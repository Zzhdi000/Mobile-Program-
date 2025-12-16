package com.example.financialapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.InputType;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class SavingsActivity extends AppCompatActivity {

    private TextView tvTotal;
    private EditText etAmount;
    private Button btnSave, btnWithdraw;
    private RecyclerView rv;

    private FirebaseAuth mAuth;
    private DatabaseReference savingsRef, userRef, cashRef;

    private List<Savings> list = new ArrayList<>();
    private SavingsAdapter adapter;
    private String currency = "LKR";

    private boolean isEditing = false;
    private long totalSavings = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_savings);

        tvTotal = findViewById(R.id.tvTotalSavings);
        etAmount = findViewById(R.id.etSaveAmount);
        btnSave = findViewById(R.id.btnAddSave);
        btnWithdraw = findViewById(R.id.btnUseSavings);
        rv = findViewById(R.id.rvSavings);

        mAuth = FirebaseAuth.getInstance();

        String uid = mAuth.getCurrentUser() != null ? mAuth.getCurrentUser().getUid() : null;
        if (uid == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        savingsRef = FirebaseDatabase.getInstance().getReference("Savings").child(uid);
        cashRef = FirebaseDatabase.getInstance().getReference("Cashdata").child(uid);
        userRef = FirebaseDatabase.getInstance().getReference("Users").child(uid);

        rv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new SavingsAdapter(this, list, currency);
        rv.setAdapter(adapter);

        loadCurrency();
        setupFormatter();
        loadSavingsList();

        btnSave.setOnClickListener(v -> addSaving());
        btnWithdraw.setOnClickListener(v -> showWithdrawDialog());
    }

    private void loadCurrency() {
        userRef.child("currency").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) currency = snapshot.getValue(String.class);
                adapter = new SavingsAdapter(SavingsActivity.this, list, currency);
                rv.setAdapter(adapter);
                updateTotalDisplay(totalSavings);
            }
            @Override public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void setupFormatter() {
        etAmount.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int st, int c, int a) {}
            @Override public void onTextChanged(CharSequence s, int st, int b, int c) {}
            @Override public void afterTextChanged(Editable s) {
                if (isEditing) return;
                isEditing = true;
                try {
                    String clean = s.toString().replace(".", "");
                    if (clean.isEmpty()) { isEditing = false; return; }
                    long val = Long.parseLong(clean);
                    DecimalFormat df = new DecimalFormat("#,###");
                    String f = df.format(val).replace(",", ".");
                    etAmount.setText(f);
                    etAmount.setSelection(f.length());
                } catch (Exception ignored) {}
                isEditing = false;
            }
        });
    }

    private void loadSavingsList() {
        savingsRef.addValueEventListener(new ValueEventListener() {
            @Override public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                totalSavings = 0;

                for (DataSnapshot s : snapshot.getChildren()) {
                    Savings ss = s.getValue(Savings.class);
                    if (ss != null) {
                        list.add(ss);
                        totalSavings += ss.getAmount();  // negatif otomatis dihitung
                    }
                }

                Collections.reverse(list); // newest first
                adapter = new SavingsAdapter(SavingsActivity.this, list, currency);
                rv.setAdapter(adapter);

                updateTotalDisplay(totalSavings);
            }

            @Override public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void updateTotalDisplay(long total) {
        tvTotal.setText(currency + " " + NumberFormatHelper.formatCurrency(currency, total));
    }

    private void addSaving() {
        String raw = etAmount.getText().toString().trim().replace(".", "");
        if (raw.isEmpty()) {
            Toast.makeText(this, "Enter amount", Toast.LENGTH_SHORT).show();
            return;
        }
        long amount;
        try { amount = Long.parseLong(raw); } catch (Exception e) { amount = 0; }

        if (amount <= 0) {
            Toast.makeText(this, "Enter valid amount", Toast.LENGTH_SHORT).show();
            return;
        }

        String id = savingsRef.push().getKey();
        if (id == null) return;

        String date = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        int month = Calendar.getInstance().get(Calendar.MONTH) + 1;

        Savings s = new Savings(id, amount, date, month, "in");

        savingsRef.child(id).setValue(s).addOnSuccessListener(x -> {
            Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show();
            etAmount.setText("");
        });
    }

    // ============================================
    //       WITHDRAW (USE SAVINGS)
    // ============================================

    private void showWithdrawDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Use Savings");

        final EditText input = new EditText(this);
        input.setHint("Enter amount");
        input.setInputType(InputType.TYPE_CLASS_NUMBER);

        builder.setView(input);

        builder.setPositiveButton("Use", (dialog, which) -> {

            String raw = input.getText().toString().trim();
            if (raw.isEmpty()) {
                Toast.makeText(this, "Enter amount", Toast.LENGTH_SHORT).show();
                return;
            }

            long amount = Long.parseLong(raw);
            if (amount <= 0) {
                Toast.makeText(this, "Invalid amount", Toast.LENGTH_SHORT).show();
                return;
            }

            if (amount > totalSavings) {
                Toast.makeText(this, "Not enough savings!", Toast.LENGTH_SHORT).show();
                return;
            }

            useSavings(amount);
        });

        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void useSavings(long amount) {

        // 1. Kurangi savings (hapus entry satu per satu sesuai jumlah)
        savingsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override public void onDataChange(@NonNull DataSnapshot snapshot) {

                long remaining = amount;

                for (DataSnapshot s : snapshot.getChildren()) {
                    Savings save = s.getValue(Savings.class);
                    if (save == null) continue;

                    long current = save.getAmount();

                    if (remaining <= 0) break;

                    if (current <= remaining) {
                        remaining -= current;
                        s.getRef().removeValue();
                    } else {
                        s.getRef().child("amount").setValue(current - remaining);
                        remaining = 0;
                    }
                }

                // Add history inside Savings list (OUT)
                addSavingsHistoryOut(amount);

                // Add to CashData as expense
                addWithdrawalExpense(amount);

                Toast.makeText(SavingsActivity.this, "Savings used!", Toast.LENGTH_SHORT).show();
            }

            @Override public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void addSavingsHistoryOut(long amount) {

        String id = savingsRef.push().getKey();
        if (id == null) return;

        String date = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        int month = Calendar.getInstance().get(Calendar.MONTH) + 1;

        Savings s = new Savings(id, -amount, date, month, "out");

        savingsRef.child(id).setValue(s);
    }

    private void addWithdrawalExpense(long amount) {

        String id = cashRef.push().getKey();
        if (id == null) return;

        String date = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        int month = Calendar.getInstance().get(Calendar.MONTH) + 1;

        Datacash d = new Datacash(
                "Use Savings",
                "expense",
                id,
                date,
                (int) amount,
                month
        );

        cashRef.child(id).setValue(d);
    }
}