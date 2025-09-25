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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignInActivity extends AppCompatActivity {

    private TextView userName, password;
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

        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String usernameString = userName.getText().toString();
                String passwordString = password.getText().toString();

                if (TextUtils.isEmpty(usernameString)) {
                    userName.setError("Empty User Name");
                }

                if (TextUtils.isEmpty(passwordString)) {
                    password.setError("Empty Password");
                } else {
                    progressDialog.setMessage("Sign In Progress");
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.show();

                    mAuth.createUserWithEmailAndPassword(usernameString, passwordString).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Intent intent = new Intent(SignInActivity.this, LoginScreen.class);
                                startActivity(intent);
                                finish();
                                progressDialog.dismiss();
                            } else {
                                Toast.makeText(SignInActivity.this, task.getException().toString(), Toast.LENGTH_LONG).show();
                                progressDialog.dismiss();
                            }
                        }
                    });
                }
            }
        });
    }
}