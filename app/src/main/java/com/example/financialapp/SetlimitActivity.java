package com.example.financialapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Locale;

public class SetlimitActivity extends AppCompatActivity {

    private TextView amountCurrent_food, amountCurrent_Utility, amountCurrent_health, amountCurrent_Other;
    private EditText et_setLimitFood, et_setLimitUtility, et_setLimitHealth, et_setLimitOther;
    private Button btn_addFood, btn_addUtility, btn_addHealth, btn_addOther;

    private FirebaseAuth mAuth;
    private DatabaseReference dbRef;
    private ProgressDialog progressDialog;

    private String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setlimit);

        mAuth = FirebaseAuth.getInstance();
        dbRef = FirebaseDatabase.getInstance().getReference().child("BudgetLimit").child(mAuth.getCurrentUser().getUid());
        progressDialog = new ProgressDialog(this);

        amountCurrent_food = findViewById(R.id.amountCurrent_food);
        amountCurrent_Utility = findViewById(R.id.amountCurrent_Utility);
        amountCurrent_health = findViewById(R.id.amountCurrent_health);
        amountCurrent_Other = findViewById(R.id.amountCurrent_Other);

        et_setLimitFood = findViewById(R.id.et_setLimitFood);
        et_setLimitUtility = findViewById(R.id.et_setLimitUtility);
        et_setLimitHealth = findViewById(R.id.et_setLimitHealth);
        et_setLimitOther = findViewById(R.id.et_setLimitOther);

        btn_addFood = findViewById(R.id.btn_addFood);
        btn_addUtility = findViewById(R.id.btn_addUtility);
        btn_addHealth = findViewById(R.id.btn_addHealth);
        btn_addOther = findViewById(R.id.btn_addOther);

        btn_addFood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addFoodLimit();
            }
        });

        btn_addUtility.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addUtilityLimit();
            }
        });

        btn_addHealth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addHealthLimit();
            }
        });

        btn_addOther.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addOtherLimit();
            }
        });

    }

    private void addOtherLimit() {
        progressDialog.setMessage("Adding Others Limit");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        id = dbRef.push().getKey();

        String otherLimitString = et_setLimitOther.getText().toString();
        String category = "other";

        BudgetLimit budgetLimit = new BudgetLimit(category, Integer.parseInt(otherLimitString), id);

        dbRef.child(id).setValue(budgetLimit).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(SetlimitActivity.this, "Limit added", Toast.LENGTH_LONG).show();
                    et_setLimitOther.setText("");
                } else {
                    Toast.makeText(SetlimitActivity.this, task.getException().toString(), Toast.LENGTH_LONG).show();
                }
                progressDialog.dismiss();
            }
        });
    }

    private void addHealthLimit() {
        progressDialog.setMessage("Adding Health Limit");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        id = dbRef.push().getKey();

        String healthLimitString = et_setLimitHealth.getText().toString();
        String category = "health";

        BudgetLimit budgetLimit = new BudgetLimit(category, Integer.parseInt(healthLimitString), id);

        dbRef.child(id).setValue(budgetLimit).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(SetlimitActivity.this, "Limit added", Toast.LENGTH_LONG).show();
                    et_setLimitHealth.setText("");
                } else {
                    Toast.makeText(SetlimitActivity.this, task.getException().toString(), Toast.LENGTH_LONG).show();
                }
                progressDialog.dismiss();
            }
        });
    }

    private void addUtilityLimit() {

        progressDialog.setMessage("Adding Utility Limit");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        id = dbRef.push().getKey();

        String utilityLimitString = et_setLimitUtility.getText().toString();
        String category = "utility";

        BudgetLimit budgetLimit = new BudgetLimit(category, Integer.parseInt(utilityLimitString), id);

        dbRef.child(id).setValue(budgetLimit).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(SetlimitActivity.this, "Limit added", Toast.LENGTH_LONG).show();
                    et_setLimitUtility.setText("");
                } else {
                    Toast.makeText(SetlimitActivity.this, task.getException().toString(), Toast.LENGTH_LONG).show();
                }
                progressDialog.dismiss();
            }
        });
    }

    private void addFoodLimit() {

        progressDialog.setMessage("Adding Food Limit");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        id = dbRef.push().getKey();

        String foodLimitString = et_setLimitFood.getText().toString();
        String category = "food";

        BudgetLimit budgetLimit = new BudgetLimit(category, Integer.parseInt(foodLimitString), id);

        dbRef.child(id).setValue(budgetLimit).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(SetlimitActivity.this, "Limit added", Toast.LENGTH_LONG).show();
                    et_setLimitFood.setText("");
                } else {
                    Toast.makeText(SetlimitActivity.this, task.getException().toString(), Toast.LENGTH_LONG).show();
                }
                progressDialog.dismiss();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                String category = null;
                int categoryAmount_food = 0;

                for (DataSnapshot snap : snapshot.getChildren()) {

                    BudgetLimit budgetLimit = snap.getValue(BudgetLimit.class);

                    category = budgetLimit.getCategory();

                    if (category.equals("food")) {
                        categoryAmount_food = budgetLimit.getAmount();
                        System.out.println(String.valueOf(categoryAmount_food));
                        amountCurrent_food.setText("LKR " + categoryAmount_food +".00");
                    }

                    if (category.equals("utility")) {
                        categoryAmount_food = budgetLimit.getAmount();
                        amountCurrent_Utility.setText("LKR " + categoryAmount_food + ".00");
                    }

                    if (category.equals("health")) {
                        categoryAmount_food = budgetLimit.getAmount();
                        amountCurrent_health.setText("LKR " + categoryAmount_food+ ".00");
                    }

                    if (category.equals("other")) {
                        categoryAmount_food = budgetLimit.getAmount();
                        amountCurrent_Other.setText("LKR " + categoryAmount_food+ ".00");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}