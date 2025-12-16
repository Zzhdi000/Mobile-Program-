package com.example.financialapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class SignInActivity extends AppCompatActivity {

    private EditText etName, etEmail, etPassword;
    private Button btnSignUp;

    private FirebaseAuth mAuth;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        etName = findViewById(R.id.ET_NameSign);
        etEmail = findViewById(R.id.ET_EmailSign);
        etPassword = findViewById(R.id.ET_Passwordsign);
        btnSignUp = findViewById(R.id.BTN_signIn);

        mAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);

        btnSignUp.setOnClickListener(v -> registerUser());
    }

    private void registerUser() {
        String name = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String pass = etPassword.getText().toString().trim();

        if (TextUtils.isEmpty(name)) {
            etName.setError("Name required");
            return;
        }
        if (TextUtils.isEmpty(email)) {
            etEmail.setError("Email required");
            return;
        }
        if (TextUtils.isEmpty(pass)) {
            etPassword.setError("Password required");
            return;
        }

        progressDialog.setMessage("Creating Account...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        mAuth.createUserWithEmailAndPassword(email, pass)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {

                        String uid = mAuth.getCurrentUser().getUid();

                        DatabaseReference ref = FirebaseDatabase.getInstance()
                                .getReference("Users")
                                .child(uid);

                        HashMap<String, Object> map = new HashMap<>();
                        map.put("name", name);
                        map.put("email", email);
                        map.put("currency", "IDR");
                        map.put("isOnboarded", false);  // <-- WAJIB

                        ref.setValue(map).addOnCompleteListener(done -> {

                            progressDialog.dismiss();
                            Toast.makeText(this, "Account created!", Toast.LENGTH_LONG).show();

                            startActivity(new Intent(this, LoginScreen.class));
                            finish();
                        });

                    } else {
                        progressDialog.dismiss();
                        Toast.makeText(
                                this,
                                "Error: " + task.getException().getMessage(),
                                Toast.LENGTH_LONG
                        ).show();
                    }
                });
    }
}
