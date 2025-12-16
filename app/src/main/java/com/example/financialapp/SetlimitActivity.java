package com.example.financialapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
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

    EditText etFood, etBills, etShopping, etTransport, etUtility, etHealth, etOther;

    Button btnFood, btnBills, btnShopping, btnTransport, btnUtility, btnHealth, btnOther;

    TextView tvFoodCurrent, tvBillsCurrent, tvShoppingCurrent,
            tvTransportCurrent, tvUtilityCurrent, tvHealthCurrent, tvOtherCurrent;

    CardView cardFood, cardBills, cardShopping, cardTransport,
            cardUtility, cardHealth, cardOther;

    // MENU buttons
    ImageView btnMenuFood, btnMenuBills, btnMenuShopping, btnMenuTransport,
            btnMenuUtility, btnMenuHealth, btnMenuOther;

    DatabaseReference dbRef, userRef;
    FirebaseAuth mAuth;

    String currency = "LKR";
    int currentMonth;

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

        // ========================= INPUT =========================
        etFood = findViewById(R.id.et_setLimitFood);
        etBills = findViewById(R.id.et_setLimitBills);
        etShopping = findViewById(R.id.et_setLimitShopping);
        etTransport = findViewById(R.id.et_setLimitTransport);
        etUtility = findViewById(R.id.et_setLimitUtility);
        etHealth = findViewById(R.id.et_setLimitHealth);
        etOther = findViewById(R.id.et_setLimitOther);

        // ========================= BUTTON =========================
        btnFood = findViewById(R.id.btn_addFood);
        btnBills = findViewById(R.id.btn_addBills);
        btnShopping = findViewById(R.id.btn_addShopping);
        btnTransport = findViewById(R.id.btn_addTransport);
        btnUtility = findViewById(R.id.btn_addUtility);
        btnHealth = findViewById(R.id.btn_addHealth);
        btnOther = findViewById(R.id.btn_addOther);

        // ========================= MENU BUTTON =========================
        btnMenuFood = findViewById(R.id.btnMenuFood);
        btnMenuBills = findViewById(R.id.btnMenuBills);
        btnMenuShopping = findViewById(R.id.btnMenuShopping);
        btnMenuTransport = findViewById(R.id.btnMenuTransport);
        btnMenuUtility = findViewById(R.id.btnMenuUtility);
        btnMenuHealth = findViewById(R.id.btnMenuHealth);
        btnMenuOther = findViewById(R.id.btnMenuOther);

        // ========================= CURRENT LIMIT TEXT =========================
        tvFoodCurrent = findViewById(R.id.amountCurrent_food);
        tvBillsCurrent = findViewById(R.id.amountCurrent_bills);
        tvShoppingCurrent = findViewById(R.id.amountCurrent_shopping);
        tvTransportCurrent = findViewById(R.id.amountCurrent_transport);
        tvUtilityCurrent = findViewById(R.id.amountCurrent_Utility);
        tvHealthCurrent = findViewById(R.id.amountCurrent_health);
        tvOtherCurrent = findViewById(R.id.amountCurrent_Other);

        // ========================= CARDS =========================
        cardFood = findViewById(R.id.cardFood);
        cardBills = findViewById(R.id.cardBills);
        cardShopping = findViewById(R.id.cardShopping);
        cardTransport = findViewById(R.id.cardTransport);
        cardUtility = findViewById(R.id.cardUtility);
        cardHealth = findViewById(R.id.cardHealth);
        cardOther = findViewById(R.id.cardOther);

        // ========================= SAVE BUTTON EVENT =========================
        btnFood.setOnClickListener(v -> saveLimit("food", etFood, btnFood));
        btnBills.setOnClickListener(v -> saveLimit("bills", etBills, btnBills));
        btnShopping.setOnClickListener(v -> saveLimit("shopping", etShopping, btnShopping));
        btnTransport.setOnClickListener(v -> saveLimit("transport", etTransport, btnTransport));
        btnUtility.setOnClickListener(v -> saveLimit("utility", etUtility, btnUtility));
        btnHealth.setOnClickListener(v -> saveLimit("health", etHealth, btnHealth));
        btnOther.setOnClickListener(v -> saveLimit("other", etOther, btnOther));

        // ========= DOUBLE TAP STILL WORKS =========
        enableDoubleTap(cardFood, "food", tvFoodCurrent, btnFood, 1);
        enableDoubleTap(cardBills, "bills", tvBillsCurrent, btnBills, 2);
        enableDoubleTap(cardShopping, "shopping", tvShoppingCurrent, btnShopping, 3);
        enableDoubleTap(cardTransport, "transport", tvTransportCurrent, btnTransport, 4);
        enableDoubleTap(cardUtility, "utility", tvUtilityCurrent, btnUtility, 5);
        enableDoubleTap(cardHealth, "health", tvHealthCurrent, btnHealth, 6);
        enableDoubleTap(cardOther, "other", tvOtherCurrent, btnOther, 7);

        // ========= MENU DOTS =========
        setupMenu(btnMenuFood, "food", tvFoodCurrent, btnFood);
        setupMenu(btnMenuBills, "bills", tvBillsCurrent, btnBills);
        setupMenu(btnMenuShopping, "shopping", tvShoppingCurrent, btnShopping);
        setupMenu(btnMenuTransport, "transport", tvTransportCurrent, btnTransport);
        setupMenu(btnMenuUtility, "utility", tvUtilityCurrent, btnUtility);
        setupMenu(btnMenuHealth, "health", tvHealthCurrent, btnHealth);
        setupMenu(btnMenuOther, "other", tvOtherCurrent, btnOther);
    }

    // ========================= LOAD CURRENCY =========================
    private void loadCurrency() {
        userRef.child("currency").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot ds) {
                if (ds.exists()) {
                    currency = ds.getValue(String.class);
                    loadCurrentLimits();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    // ========================= SAVE LIMIT =========================
    private void saveLimit(String category, EditText et, Button btn) {

        if (et.getText().toString().isEmpty()) {
            Toast.makeText(this, "Enter limit first", Toast.LENGTH_SHORT).show();
            return;
        }

        int amount = Integer.parseInt(et.getText().toString());
        String id = dbRef.push().getKey();

        BudgetLimit limit = new BudgetLimit(category, amount, id);
        limit.setMonth(currentMonth);

        dbRef.child(id).setValue(limit).addOnSuccessListener(a -> {
            Toast.makeText(this, "Limit saved", Toast.LENGTH_SHORT).show();

            et.setText("");
            btn.setEnabled(false);
            btn.setAlpha(0.5f);

            loadCurrentLimits();
        });
    }

    // ========================= LOAD CURRENT LIMITS =========================
    private void loadCurrentLimits() {

        String symbol = NumberFormatHelper.getCurrencySymbol(currency);

        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snap) {

                for (DataSnapshot s : snap.getChildren()) {

                    BudgetLimit bl = s.getValue(BudgetLimit.class);
                    if (bl == null) continue;

                    String formatted = NumberFormatHelper.formatCurrency(currency, bl.getAmount());

                    switch (bl.getCategory()) {

                        case "food":
                            tvFoodCurrent.setText(symbol + " " + formatted);
                            updateButtonState(btnFood, bl);
                            break;

                        case "bills":
                            tvBillsCurrent.setText(symbol + " " + formatted);
                            updateButtonState(btnBills, bl);
                            break;

                        case "shopping":
                            tvShoppingCurrent.setText(symbol + " " + formatted);
                            updateButtonState(btnShopping, bl);
                            break;

                        case "transport":
                            tvTransportCurrent.setText(symbol + " " + formatted);
                            updateButtonState(btnTransport, bl);
                            break;

                        case "utility":
                            tvUtilityCurrent.setText(symbol + " " + formatted);
                            updateButtonState(btnUtility, bl);
                            break;

                        case "health":
                            tvHealthCurrent.setText(symbol + " " + formatted);
                            updateButtonState(btnHealth, bl);
                            break;

                        case "other":
                            tvOtherCurrent.setText(symbol + " " + formatted);
                            updateButtonState(btnOther, bl);
                            break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void updateButtonState(Button btn, BudgetLimit bl) {
        if (bl.getMonth() == currentMonth) {
            btn.setEnabled(false);
            btn.setAlpha(0.5f);
        } else {
            btn.setEnabled(true);
            btn.setAlpha(1f);
        }
    }

    // ========================= POPUP MENU ⋮ =========================
    private void setupMenu(ImageView menuBtn, String category, TextView tv, Button btn) {

        menuBtn.setOnClickListener(v -> {

            PopupMenu popup = new PopupMenu(SetlimitActivity.this, menuBtn);
            MenuInflater inflater = popup.getMenuInflater();
            popup.getMenu().add("Reset Limit");

            popup.setOnMenuItemClickListener(item -> {
                if (item.getTitle().equals("Reset Limit")) {
                    showResetDialog(category, tv, btn);
                }
                return true;
            });

            popup.show();
        });
    }

    // ========================= DOUBLE TAP RESET =========================
    private void enableDoubleTap(CardView card, String category, TextView tv, Button btn, int code) {

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

    // ========================= RESET LIMIT =========================
    private void showResetDialog(String category, TextView tv, Button btn) {

        String symbol = NumberFormatHelper.getCurrencySymbol(currency);

        new AlertDialog.Builder(this)
                .setTitle("Reset Limit")
                .setMessage("Reset limit for " + category + "?")
                .setPositiveButton("Yes", (d, w) -> {

                    dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snap) {

                            for (DataSnapshot s : snap.getChildren()) {
                                BudgetLimit bl = s.getValue(BudgetLimit.class);

                                if (bl != null && bl.getCategory().equals(category))
                                    s.getRef().removeValue();
                            }

                            tv.setText(symbol + " 0");
                            btn.setEnabled(true);
                            btn.setAlpha(1f);

                            Toast.makeText(SetlimitActivity.this,
                                    "Limit reset for " + category,
                                    Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {}
                    });
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}