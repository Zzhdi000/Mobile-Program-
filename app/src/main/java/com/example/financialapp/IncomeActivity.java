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

        mAuth = FirebaseAuth.getInstance();
        incomeRef = FirebaseDatabase.getInstance().getReference().child("Cashdata").child(mAuth.getCurrentUser().getUid());
        progressDialog = new ProgressDialog(this);

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
                }
                if (categoryString.equals("Select Item")) {
                    Toast.makeText(IncomeActivity.this, "Select Valid Item", Toast.LENGTH_LONG).show();
                } else {
                    progressDialog.setMessage("Adding Income");
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.show();

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
            }
        });
    }


}