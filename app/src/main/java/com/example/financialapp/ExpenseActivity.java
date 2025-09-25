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

public class ExpenseActivity extends AppCompatActivity {

    private EditText expenseAmount;
    private Button addExpense;
    private Spinner category;

    private FirebaseAuth mAuth;
    private DatabaseReference incomeRef;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense);

        mAuth = FirebaseAuth.getInstance();
        incomeRef = FirebaseDatabase.getInstance().getReference().child("Cashdata").child(mAuth.getCurrentUser().getUid());
        progressDialog = new ProgressDialog(this);

        expenseAmount = findViewById(R.id.ET_expensesAmount);
        addExpense = findViewById(R.id.BTN_addExpense);
        category = findViewById(R.id.expenseCategorySpinner);

        addExpense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String expenseAmountString = expenseAmount.getText().toString();
                String categoryString = category.getSelectedItem().toString();

                if (TextUtils.isEmpty(expenseAmountString)) {
                    expenseAmount.setError("Empty Amount");
                }
                if (categoryString.equals("Select Item")) {
                    Toast.makeText(ExpenseActivity.this, "Select Valid Item", Toast.LENGTH_LONG).show();
                } else {
                    progressDialog.setMessage("Adding Expense");
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

                    Datacash datacash = new Datacash(categoryString, "expense", id, date, Integer.parseInt(expenseAmountString), months.getMonths());

                    incomeRef.child(id).setValue(datacash).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(ExpenseActivity.this, "Expense added", Toast.LENGTH_LONG).show();
                                expenseAmount.setText("");
                            } else {
                                Toast.makeText(ExpenseActivity.this, task.getException().toString(), Toast.LENGTH_LONG).show();
                            }
                            progressDialog.dismiss();
                        }
                    });
                }
            }
        });
    }
}