package com.example.financialapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
    private DatabaseReference savingsRef, userRef;

    private List<Savings> list = new ArrayList<>();
    private SavingsAdapter adapter;
    private String currency = "LKR";

    private boolean isEditing = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_savings);

        tvTotal = findViewById(R.id.tvTotalSavings);
        etAmount = findViewById(R.id.etSaveAmount);
        btnSave = findViewById(R.id.btnAddSave);
        btnWithdraw = findViewById(R.id.btnWithdraw);
        rv = findViewById(R.id.rvSavings);

        mAuth = FirebaseAuth.getInstance();
        String uid = mAuth.getCurrentUser() != null ? mAuth.getCurrentUser().getUid() : null;
        if (uid == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        savingsRef = FirebaseDatabase.getInstance().getReference("Savings").child(uid);
        userRef = FirebaseDatabase.getInstance().getReference("Users").child(uid);

        rv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new SavingsAdapter(this, list, currency);
        rv.setAdapter(adapter);

        loadCurrency();
        setupFormatter();
        loadSavingsList();

        btnSave.setOnClickListener(v -> addSaving());
        btnWithdraw.setOnClickListener(v -> withdrawDialog()); // currently hidden; keep for future
    }

    private void loadCurrency() {
        userRef.child("currency").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) currency = snapshot.getValue(String.class);
                adapter = new SavingsAdapter(SavingsActivity.this, list, currency);
                rv.setAdapter(adapter);
                updateTotalDisplay(0);
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
                long total = 0;
                for (DataSnapshot s : snapshot.getChildren()) {
                    Savings ss = s.getValue(Savings.class);
                    if (ss != null) {
                        list.add(ss);
                        total += ss.getAmount();
                    }
                }
                // newest first
                Collections.reverse(list);
                adapter = new SavingsAdapter(SavingsActivity.this, list, currency);
                rv.setAdapter(adapter);
                updateTotalDisplay(total);
            }
            @Override public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void updateTotalDisplay(long total) {
        tvTotal.setText(currency + " " + total);
    }

    private void addSaving() {
        String raw = etAmount.getText().toString().trim().replace(".", "");
        if (raw.isEmpty()) {
            Toast.makeText(this, "Enter amount", Toast.LENGTH_SHORT).show();
            return;
        }
        long amount;
        try { amount = Long.parseLong(raw); } catch (NumberFormatException e) { amount = 0; }
        if (amount <= 0) {
            Toast.makeText(this, "Enter valid amount", Toast.LENGTH_SHORT).show();
            return;
        }

        String id = savingsRef.push().getKey();
        if (id == null) {
            Toast.makeText(this, "Try again", Toast.LENGTH_SHORT).show();
            return;
        }

        String date = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        int month = Calendar.getInstance().get(Calendar.MONTH) + 1;

        Savings s = new Savings(id, amount, date, month, "");

        savingsRef.child(id).setValue(s).addOnCompleteListener(t -> {
            if (t.isSuccessful()) {
                Toast.makeText(SavingsActivity.this, "Saved", Toast.LENGTH_SHORT).show();
                etAmount.setText("");
            } else {
                Toast.makeText(SavingsActivity.this, "Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void withdrawDialog() {
        // left as placeholder if you want a withdraw UI later
        Toast.makeText(this, "Withdraw feature not enabled yet", Toast.LENGTH_SHORT).show();
    }
}
