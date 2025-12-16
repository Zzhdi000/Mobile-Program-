package com.example.financialapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;

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
import java.util.HashMap;

public class ExpenseActivity extends AppCompatActivity {

    private EditText etAmount;
    private Spinner spinnerCategory;
    private Button btnAdd;

    private FirebaseAuth mAuth;
    private DatabaseReference cashRef, limitRef;

    private String currency = "LKR";
    private boolean isEditing = false;

    private HashMap<String, Long> categoryLimitMap = new HashMap<>();
    private HashMap<String, Long> categoryUsedMap = new HashMap<>();

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

        limitRef = FirebaseDatabase.getInstance().getReference("BudgetLimit")
                .child(mAuth.getCurrentUser().getUid());

        loadCurrency();
        setupCategorySpinner();
        setupAmountFormatter();
        loadCategoryLimitAndUsage();

        btnAdd.setOnClickListener(v -> checkBeforeSaving());
    }

    private void loadCurrency() {
        FirebaseDatabase.getInstance().getReference("Users")
                .child(mAuth.getCurrentUser().getUid())
                .child("currency")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot s) {
                        if (s.exists()) currency = s.getValue(String.class);
                    }

                    @Override public void onCancelled(@NonNull DatabaseError error) {}
                });
    }

    private void loadCategoryLimitAndUsage() {

        int currentMonth = Calendar.getInstance().get(Calendar.MONTH) + 1;

        // RESET MAP
        categoryLimitMap.clear();
        categoryUsedMap.clear();

        // Load LIMIT
        limitRef.addValueEventListener(new ValueEventListener() {
            @Override public void onDataChange(@NonNull DataSnapshot snap) {

                categoryLimitMap.clear();

                for (DataSnapshot s : snap.getChildren()) {
                    BudgetLimit bl = s.getValue(BudgetLimit.class);
                    if (bl == null) continue;

                    if (bl.getMonth() == currentMonth) {
                        categoryLimitMap.put(bl.getCategory().toLowerCase(), (long) bl.getAmount());
                    }
                }

                // Load USED
                loadCategoryUsage(currentMonth);
            }

            @Override public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void loadCategoryUsage(int month) {

        cashRef.addValueEventListener(new ValueEventListener() {
            @Override public void onDataChange(@NonNull DataSnapshot cashSnap) {

                categoryUsedMap.clear();

                for (DataSnapshot s : cashSnap.getChildren()) {

                    Datacash d = s.getValue(Datacash.class);
                    if (d == null) continue;

                    if (d.getType().equals("expense") && d.getMonth() == month) {

                        String cat = d.getCategory().toLowerCase();

                        if (!categoryUsedMap.containsKey(cat))
                            categoryUsedMap.put(cat, 0L);

                        categoryUsedMap.put(cat, categoryUsedMap.get(cat) + d.getAmount());
                    }
                }
            }

            @Override public void onCancelled(@NonNull DatabaseError error) {}
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

        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, categories);

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

    // 🔥 Check limit before saving expense
    private void checkBeforeSaving() {

        String amountStr = etAmount.getText().toString().trim().replace(".", "");
        if (amountStr.isEmpty()) {
            Toast.makeText(this, "Enter expense amount", Toast.LENGTH_SHORT).show();
            return;
        }

        int amount = Integer.parseInt(amountStr);
        String category = spinnerCategory.getSelectedItem().toString().toLowerCase();

        long limit = categoryLimitMap.containsKey(category) ? categoryLimitMap.get(category) : 0;
        long used = categoryUsedMap.containsKey(category) ? categoryUsedMap.get(category) : 0;

        // IF OVER LIMIT → popup warning
        if (used + amount > limit && limit > 0) {

            new AlertDialog.Builder(this)
                    .setTitle("⚠ Limit Warning")
                    .setMessage(
                            "You have exceeded your " + category + " budget.\n\n" +
                                    "Limit   : " + limit + "\n" +
                                    "Current : " + used + "\n\n" +
                                    "Do you still want to add this expense?"
                    )
                    .setPositiveButton("Add Anyway", (d, w) -> saveExpense(amount))
                    .setNegativeButton("Cancel", null)
                    .show();

        } else {
            saveExpense(amount);
        }
    }

    private void saveExpense(int amount) {

        String category = spinnerCategory.getSelectedItem().toString();
        String date = new SimpleDateFormat("dd-MM-yyyy")
                .format(Calendar.getInstance().getTime());

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

        cashRef.child(id)
                .setValue(d)
                .addOnCompleteListener(task -> {
                    Toast.makeText(this, "Expense added", Toast.LENGTH_SHORT).show();
                    finish();
                });
    }
}