package com.example.financialapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignInActivity extends AppCompatActivity {

    private EditText userName, password;
    private Button signIn;

    private FirebaseAuth mAuth;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        userName = findViewById(R.id.ET_Usernamesign);
        password = findViewById(R.id.ET_Passwordsign);
        signIn = findViewById(R.id.BTN_signIn);

        mAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);

        signIn.setOnClickListener(v -> {
            String email = userName.getText().toString().trim();
            String pass = password.getText().toString().trim();

            if (TextUtils.isEmpty(email)) {
                userName.setError("Email required");
                return;
            }
            if (TextUtils.isEmpty(pass)) {
                password.setError("Password required");
                return;
            }

            progressDialog.setMessage("Creating Account...");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();

            mAuth.createUserWithEmailAndPassword(email, pass)
                    .addOnCompleteListener(task -> {
                        progressDialog.dismiss();

                        if (task.isSuccessful()) {
                            Toast.makeText(SignInActivity.this,
                                    "Account created successfully!",
                                    Toast.LENGTH_LONG).show();

                            startActivity(new Intent(SignInActivity.this, LoginScreen.class));
                            finish();
                        } else {
                            Toast.makeText(SignInActivity.this,
                                    task.getException().getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    });
        });
    }
}
