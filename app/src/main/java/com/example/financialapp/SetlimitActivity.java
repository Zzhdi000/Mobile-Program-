package com.example.financialapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.AlertDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;

public class SetlimitActivity extends AppCompatActivity {

    EditText etFood, etUtility, etHealth, etOther;
    Button btnFood, btnUtility, btnHealth, btnOther;

    TextView tvFoodCurrent, tvUtilityCurrent, tvHealthCurrent, tvOtherCurrent;

    CardView cardFood, cardUtility, cardHealth, cardOther;

    DatabaseReference dbRef, userRef;
    FirebaseAuth mAuth;

    String currency = "LKR";
    int currentMonth;

    long lastTapFood = 0, lastTapUtility = 0, lastTapHealth = 0, lastTapOther = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setlimit);

        mAuth = FirebaseAuth.getInstance();

        dbRef = FirebaseDatabase.getInstance()
                .getReference("BudgetLimit")
                .child(mAuth.getCurrentUser().getUid());

        userRef = FirebaseDatabase.getInstance()
                .getReference("Users")
                .child(mAuth.getCurrentUser().getUid());

        currentMonth = Calendar.getInstance().get(Calendar.MONTH) + 1;

        loadCurrency();

        // INPUT
        etFood = findViewById(R.id.et_setLimitFood);
        etUtility = findViewById(R.id.et_setLimitUtility);
        etHealth = findViewById(R.id.et_setLimitHealth);
        etOther = findViewById(R.id.et_setLimitOther);

        // BUTTONS
        btnFood = findViewById(R.id.btn_addFood);
        btnUtility = findViewById(R.id.btn_addUtility);
        btnHealth = findViewById(R.id.btn_addHealth);
        btnOther = findViewById(R.id.btn_addOther);

        // CURRENT DISPLAY
        tvFoodCurrent = findViewById(R.id.amountCurrent_food);
        tvUtilityCurrent = findViewById(R.id.amountCurrent_Utility);
        tvHealthCurrent = findViewById(R.id.amountCurrent_health);
        tvOtherCurrent = findViewById(R.id.amountCurrent_Other);

        // CARDS (PASTIKAN ADA ID DI XML)
        cardFood = findViewById(R.id.cardFood);
        cardUtility = findViewById(R.id.cardUtility);
        cardHealth = findViewById(R.id.cardHealth);
        cardOther = findViewById(R.id.cardOther);

        // SAVE HANDLER
        btnFood.setOnClickListener(v -> save("food", etFood, btnFood));
        btnUtility.setOnClickListener(v -> save("utility", etUtility, btnUtility));
        btnHealth.setOnClickListener(v -> save("health", etHealth, btnHealth));
        btnOther.setOnClickListener(v -> save("other", etOther, btnOther));

        // DOUBLE TAP RESET
        enableDoubleTapReset(cardFood, "food", tvFoodCurrent, btnFood, 1);
        enableDoubleTapReset(cardUtility, "utility", tvUtilityCurrent, btnUtility, 2);
        enableDoubleTapReset(cardHealth, "health", tvHealthCurrent, btnHealth, 3);
        enableDoubleTapReset(cardOther, "other", tvOtherCurrent, btnOther, 4);

        loadCurrentLimits();
    }

    // ========================= LOAD CURRENCY =========================
    private void loadCurrency() {
        userRef.child("currency").addValueEventListener(new ValueEventListener() {
            @Override public void onDataChange(@NonNull DataSnapshot ds) {
                if (ds.exists()) currency = ds.getValue(String.class);
            }
            @Override public void onCancelled(@NonNull DatabaseError e) {}
        });
    }

    // ========================= SAVE LIMIT =========================
    private void save(String category, EditText et, Button btn) {

        if (et.getText().toString().isEmpty()) {
            Toast.makeText(this, "Enter limit first", Toast.LENGTH_SHORT).show();
            return;
        }

        int amount = Integer.parseInt(et.getText().toString());
        String id = dbRef.push().getKey();

        BudgetLimit limit = new BudgetLimit(category, amount, id);
        limit.setMonth(currentMonth);

        dbRef.child(id).setValue(limit)
                .addOnSuccessListener(a -> {
                    Toast.makeText(this, "Limit saved (" + currency + ")", Toast.LENGTH_SHORT).show();

                    et.setText("");
                    btn.setEnabled(false);
                    btn.setAlpha(0.5f);

                    loadCurrentLimits();
                });
    }

    // ========================= LOAD LIMIT TABLE =========================
    private void loadCurrentLimits() {

        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snap) {

                for (DataSnapshot s : snap.getChildren()) {
                    BudgetLimit bl = s.getValue(BudgetLimit.class);
                    if (bl == null) continue;

                    if (bl.getCategory().equals("food")) {
                        tvFoodCurrent.setText(currency + " " + bl.getAmount());
                        updateButtonState(btnFood, bl);
                    }

                    if (bl.getCategory().equals("utility")) {
                        tvUtilityCurrent.setText(currency + " " + bl.getAmount());
                        updateButtonState(btnUtility, bl);
                    }

                    if (bl.getCategory().equals("health")) {
                        tvHealthCurrent.setText(currency + " " + bl.getAmount());
                        updateButtonState(btnHealth, bl);
                    }

                    if (bl.getCategory().equals("other")) {
                        tvOtherCurrent.setText(currency + " " + bl.getAmount());
                        updateButtonState(btnOther, bl);
                    }
                }
            }

            @Override public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    // ========================= DISABLE MONTH LOCK =========================
    private void updateButtonState(Button btn, BudgetLimit bl) {
        if (bl.getMonth() == currentMonth) {
            btn.setEnabled(false);
            btn.setAlpha(0.5f);
        } else {
            btn.setEnabled(true);
            btn.setAlpha(1f);
        }
    }

    // ========================= DOUBLE TAP RESET =========================
    private void enableDoubleTapReset(CardView card, String category, TextView tv, Button btn, int code) {

        card.setOnClickListener(v -> {

            long now = System.currentTimeMillis();
            boolean doubleTap = false;

            if (code == 1 && now - lastTapFood < 300) doubleTap = true;
            if (code == 2 && now - lastTapUtility < 300) doubleTap = true;
            if (code == 3 && now - lastTapHealth < 300) doubleTap = true;
            if (code == 4 && now - lastTapOther < 300) doubleTap = true;

            if (code == 1) lastTapFood = now;
            if (code == 2) lastTapUtility = now;
            if (code == 3) lastTapHealth = now;
            if (code == 4) lastTapOther = now;

            if (doubleTap) {
                showResetDialog(category, tv, btn);
            }
        });
    }

    // ========================= POP UP RESET =========================
    private void showResetDialog(String category, TextView tv, Button btn) {

        new AlertDialog.Builder(this)
                .setTitle("Reset Limit")
                .setMessage("Reset limit for " + category + "?")
                .setPositiveButton("Yes", (d, w) -> {

                    dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snap) {

                            for (DataSnapshot s : snap.getChildren()) {
                                BudgetLimit bl = s.getValue(BudgetLimit.class);
                                if (bl != null && bl.getCategory().equals(category)) {
                                    s.getRef().removeValue();
                                }
                            }

                            Toast.makeText(SetlimitActivity.this,
                                    "Limit reset for " + category,
                                    Toast.LENGTH_SHORT).show();

                            tv.setText(currency + " 0");

                            btn.setEnabled(true);
                            btn.setAlpha(1f);
                        }

                        @Override public void onCancelled(@NonNull DatabaseError e) {}
                    });

                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}
