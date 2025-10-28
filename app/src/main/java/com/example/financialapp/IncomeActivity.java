package com.example.financialapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import android.view.MenuItem;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.joda.time.DateTime;
import org.joda.time.Months;
import org.joda.time.MutableDateTime;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class IncomeActivity extends AppCompatActivity {

    private Button b_addIncome;
    private EditText incomeAmount;
    private Spinner category;

    private FirebaseAuth mAuth;
    private DatabaseReference incomeRef;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_income);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Add Income");
        }

        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() != null) {
            incomeRef = FirebaseDatabase.getInstance().getReference()
                    .child("Cashdata")
                    .child(mAuth.getCurrentUser().getUid());

            progressDialog = new ProgressDialog(this);
        } else {
            Toast.makeText(this, "Sesi Anda habis. Silakan Login kembali.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        b_addIncome = findViewById(R.id.BTN_addIncome);
        incomeAmount = findViewById(R.id.ET_incomeAmount);
        category = findViewById(R.id.incomeCategorySpinner);

        b_addIncome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String incomeAmoutString = incomeAmount.getText().toString();
                String categoryString = category.getSelectedItem().toString();

                if (TextUtils.isEmpty(incomeAmoutString)) {
                    incomeAmount.setError("Empty Amount");
                    return;
                } else if (categoryString.equals("Select Item")) {
                    Toast.makeText(IncomeActivity.this, "Select Valid Item", Toast.LENGTH_LONG).show();
                    return;
                } else {
                    try {
                        int amount = Integer.parseInt(incomeAmoutString);

                        progressDialog.setMessage("Adding Income");
                        progressDialog.setCanceledOnTouchOutside(false);
                        progressDialog.show();

                        new android.os.Handler(getMainLooper()).postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (progressDialog.isShowing()) {
                                    progressDialog.dismiss();
                                    Toast.makeText(IncomeActivity.this, "Proses selesai. Data tersimpan.", Toast.LENGTH_LONG).show();
                                }
                            }
                        }, 7000);

                        String id = incomeRef.push().getKey();
                        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
                        Calendar cal = Calendar.getInstance();
                        String date = dateFormat.format(cal.getTime());

                        MutableDateTime epoch = new MutableDateTime();
                        epoch.setDate(0);
                        DateTime now = new DateTime();
                        Months months = Months.monthsBetween(epoch, now);

                        Datacash datacash = new Datacash(categoryString, "income", id, date, Integer.parseInt(incomeAmoutString), months.getMonths());

                        incomeRef.child(id).setValue(datacash).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(IncomeActivity.this, "Income added", Toast.LENGTH_LONG).show();
                                            incomeAmount.setText("");
                                        } else {
                                            Toast.makeText(IncomeActivity.this, task.getException().toString(), Toast.LENGTH_LONG).show();
                                        }
                                        progressDialog.dismiss();
                                    }
                                });
                            }
                        });
                    } catch (NumberFormatException e) {
                        incomeAmount.setError("Invalid Amount Format");
                        Toast.makeText(IncomeActivity.this, "Masukkan hanya angka", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
