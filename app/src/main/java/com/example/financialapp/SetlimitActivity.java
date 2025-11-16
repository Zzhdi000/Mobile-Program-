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

    // INPUT
    EditText etFood, etBills, etShopping, etTransport, etUtility, etHealth, etOther;

    // BUTTONS
    Button btnFood, btnBills, btnShopping, btnTransport, btnUtility, btnHealth, btnOther;

    // TEXT CURRENT LIMITS
    TextView tvFoodCurrent, tvBillsCurrent, tvShoppingCurrent, tvTransportCurrent,
            tvUtilityCurrent, tvHealthCurrent, tvOtherCurrent;

    // CARDS FOR DOUBLE TAP RESET
    CardView cardFood, cardBills, cardShopping, cardTransport, cardUtility, cardHealth, cardOther;

    // FIREBASE
    DatabaseReference dbRef, userRef;
    FirebaseAuth mAuth;

    String currency = "LKR";
    int currentMonth;

    // DOUBLE TAP TIMERS
    long lastTapFood = 0, lastTapBills = 0, lastTapShopping = 0,
            lastTapTransport = 0, lastTapUtility = 0, lastTapHealth = 0, lastTapOther = 0;

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

        // ========== INPUT FIELDS ==========
        etFood = findViewById(R.id.et_setLimitFood);
        etBills = findViewById(R.id.et_setLimitBills);
        etShopping = findViewById(R.id.et_setLimitShopping);
        etTransport = findViewById(R.id.et_setLimitTransport);
        etUtility = findViewById(R.id.et_setLimitUtility);
        etHealth = findViewById(R.id.et_setLimitHealth);
        etOther = findViewById(R.id.et_setLimitOther);

        // ========== BUTTON ID ==========
        btnFood = findViewById(R.id.btn_addFood);
        btnBills = findViewById(R.id.btn_addBills);
        btnShopping = findViewById(R.id.btn_addShopping);
        btnTransport = findViewById(R.id.btn_addTransport);
        btnUtility = findViewById(R.id.btn_addUtility);
        btnHealth = findViewById(R.id.btn_addHealth);
        btnOther = findViewById(R.id.btn_addOther);

        // ========== CURRENT TEXTVIEW ID ==========
        tvFoodCurrent = findViewById(R.id.amountCurrent_food);
        tvBillsCurrent = findViewById(R.id.amountCurrent_bills);
        tvShoppingCurrent = findViewById(R.id.amountCurrent_shopping);
        tvTransportCurrent = findViewById(R.id.amountCurrent_transport);
        tvUtilityCurrent = findViewById(R.id.amountCurrent_Utility);
        tvHealthCurrent = findViewById(R.id.amountCurrent_health);
        tvOtherCurrent = findViewById(R.id.amountCurrent_Other);

        // ========== CARD ID ==========
        cardFood = findViewById(R.id.cardFood);
        cardBills = findViewById(R.id.cardBills);
        cardShopping = findViewById(R.id.cardShopping);
        cardTransport = findViewById(R.id.cardTransport);
        cardUtility = findViewById(R.id.cardUtility);
        cardHealth = findViewById(R.id.cardHealth);
        cardOther = findViewById(R.id.cardOther);

        // ========== SAVE BUTTON HANDLER ==========
        btnFood.setOnClickListener(v -> save("food", etFood, btnFood));
        btnBills.setOnClickListener(v -> save("bills", etBills, btnBills));
        btnShopping.setOnClickListener(v -> save("shopping", etShopping, btnShopping));
        btnTransport.setOnClickListener(v -> save("transport", etTransport, btnTransport));
        btnUtility.setOnClickListener(v -> save("utility", etUtility, btnUtility));
        btnHealth.setOnClickListener(v -> save("health", etHealth, btnHealth));
        btnOther.setOnClickListener(v -> save("other", etOther, btnOther));

        // ========== ENABLE DOUBLE TAP RESET ==========
        enableDoubleTapReset(cardFood, "food", tvFoodCurrent, btnFood, 1);
        enableDoubleTapReset(cardBills, "bills", tvBillsCurrent, btnBills, 2);
        enableDoubleTapReset(cardShopping, "shopping", tvShoppingCurrent, btnShopping, 3);
        enableDoubleTapReset(cardTransport, "transport", tvTransportCurrent, btnTransport, 4);
        enableDoubleTapReset(cardUtility, "utility", tvUtilityCurrent, btnUtility, 5);
        enableDoubleTapReset(cardHealth, "health", tvHealthCurrent, btnHealth, 6);
        enableDoubleTapReset(cardOther, "other", tvOtherCurrent, btnOther, 7);

        // ========== LOAD INITIAL LIMITS ==========
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

    // ========================= LOAD LIMITS =========================
    private void loadCurrentLimits() {

        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snap) {

                for (DataSnapshot s : snap.getChildren()) {
                    BudgetLimit bl = s.getValue(BudgetLimit.class);
                    if (bl == null) continue;

                    switch (bl.getCategory()) {

                        case "food":
                            tvFoodCurrent.setText(currency + " " + bl.getAmount());
                            updateButtonState(btnFood, bl);
                            break;

                        case "bills":
                            tvBillsCurrent.setText(currency + " " + bl.getAmount());
                            updateButtonState(btnBills, bl);
                            break;

                        case "shopping":
                            tvShoppingCurrent.setText(currency + " " + bl.getAmount());
                            updateButtonState(btnShopping, bl);
                            break;

                        case "transport":
                            tvTransportCurrent.setText(currency + " " + bl.getAmount());
                            updateButtonState(btnTransport, bl);
                            break;

                        case "utility":
                            tvUtilityCurrent.setText(currency + " " + bl.getAmount());
                            updateButtonState(btnUtility, bl);
                            break;

                        case "health":
                            tvHealthCurrent.setText(currency + " " + bl.getAmount());
                            updateButtonState(btnHealth, bl);
                            break;

                        case "other":
                            tvOtherCurrent.setText(currency + " " + bl.getAmount());
                            updateButtonState(btnOther, bl);
                            break;
                    }
                }
            }

            @Override public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    // ========================= DISABLE BUTTON IF SAME MONTH =========================
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

            switch (code) {
                case 1: doubleTap = (now - lastTapFood < 300); lastTapFood = now; break;
                case 2: doubleTap = (now - lastTapBills < 300); lastTapBills = now; break;
                case 3: doubleTap = (now - lastTapShopping < 300); lastTapShopping = now; break;
                case 4: doubleTap = (now - lastTapTransport < 300); lastTapTransport = now; break;
                case 5: doubleTap = (now - lastTapUtility < 300); lastTapUtility = now; break;
                case 6: doubleTap = (now - lastTapHealth < 300); lastTapHealth = now; break;
                case 7: doubleTap = (now - lastTapOther < 300); lastTapOther = now; break;
            }

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
