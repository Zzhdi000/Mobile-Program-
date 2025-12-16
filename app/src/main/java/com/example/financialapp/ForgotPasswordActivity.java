package com.example.financialapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordActivity extends AppCompatActivity {

    EditText etEmail;
    Button btnReset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        etEmail = findViewById(R.id.etEmail);
        btnReset = findViewById(R.id.btnReset);

        btnReset.setOnClickListener(v -> {

            // ================== VALIDASI EMAIL ==================
            String email = etEmail.getText().toString().trim();

            if (email.isEmpty()) {
                etEmail.setError("Email cannot be empty");
                return;
            }
            // =====================================================

            FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(this,
                                    "Reset link has been sent to your email.",
                                    Toast.LENGTH_LONG).show();
                            finish();
                        } else {
                            Toast.makeText(this,
                                    "Failed: " + task.getException().getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    });
        });
    }
}
