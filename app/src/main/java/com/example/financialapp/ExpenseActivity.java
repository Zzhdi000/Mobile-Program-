package com.example.financialapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class ExpenseActivity extends AppCompatActivity {

    private EditText etAmount;
    private Spinner spinnerCategory;
    private Button btnAdd;

    private FirebaseAuth mAuth;
    private DatabaseReference cashRef;
    private String currency = "LKR";

    private boolean isEditing = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense);

        etAmount = findViewById(R.id.etAmount);
        spinnerCategory = findViewById(R.id.spinnerCategory);
        btnAdd = findViewById(R.id.btnAdd);

        mAuth = FirebaseAuth.getInstance();
        cashRef = FirebaseDatabase.getInstance().getReference("Cashdata")
                .child(mAuth.getCurrentUser().getUid());

        loadCurrency();
        setupCategorySpinner();
        setupAmountFormatter();

        btnAdd.setOnClickListener(v -> saveExpense());
    }

    // ==========================
    // LOAD CURRENCY (FIXED)
    // ==========================
    private void loadCurrency() {
        FirebaseDatabase.getInstance().getReference("Users")
                .child(mAuth.getCurrentUser().getUid())
                .child("currency")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot s) {
                        if (s.exists()) currency = s.getValue(String.class);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) { }
                });
    }

    private void setupCategorySpinner() {
        ArrayList<String> categories = new ArrayList<>();

        categories.add("Food");
        categories.add("Bills");
        categories.add("Shopping");
        categories.add("Transport");
        categories.add("Health");
        categories.add("Other");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                categories
        );
        spinnerCategory.setAdapter(adapter);
    }

    private void setupAmountFormatter() {

        etAmount.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (isEditing) return;
                isEditing = true;

                try {
                    String clean = s.toString().replace(".", "");
                    if (clean.isEmpty()) {
                        isEditing = false;
                        return;
                    }

                    long parsed = Long.parseLong(clean);
                    DecimalFormat df = new DecimalFormat("#,###");
                    String formatted = df.format(parsed).replace(",", ".");
                    etAmount.setText(formatted);
                    etAmount.setSelection(formatted.length());

                } catch (Exception ignored) {}

                isEditing = false;
            }
        });
    }

    private void saveExpense() {

        String amountStr = etAmount.getText().toString().trim().replace(".", "");
        if (amountStr.isEmpty()) {
            Toast.makeText(this, "Enter expense amount", Toast.LENGTH_SHORT).show();
            return;
        }

        int amount = Integer.parseInt(amountStr);
        String category = spinnerCategory.getSelectedItem().toString();

        String date = new SimpleDateFormat("dd-MM-yyyy").format(Calendar.getInstance().getTime());
        int month = Calendar.getInstance().get(Calendar.MONTH) + 1;

        String id = cashRef.push().getKey();

        Datacash d = new Datacash(
                category,
                "expense",
                id,
                date,
                amount,
                month
        );

        d.setCurrency(currency);

        cashRef.child(id).setValue(d).addOnCompleteListener(t -> {
            Toast.makeText(this, "Expense added", Toast.LENGTH_SHORT).show();
            finish();
        });
    }
}
