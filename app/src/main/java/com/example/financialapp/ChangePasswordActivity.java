package com.example.financialapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class ChangePasswordActivity extends AppCompatActivity {

    EditText etEmail;
    Button btnSend;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        etEmail = findViewById(R.id.etEmailReset);
        btnSend = findViewById(R.id.btnSendReset);
        mAuth = FirebaseAuth.getInstance();

        // otomatis isi email user
        if (mAuth.getCurrentUser() != null) {
            etEmail.setText(mAuth.getCurrentUser().getEmail());
        }

        btnSend.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();

            if (TextUtils.isEmpty(email)) {
                etEmail.setError("Email required!");
                return;
            }

            mAuth.sendPasswordResetEmail(email)
                    .addOnSuccessListener(aVoid ->
                            Toast.makeText(this, "Reset link sent!", Toast.LENGTH_LONG).show())
                    .addOnFailureListener(e ->
                            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show());
        });
    }
}
