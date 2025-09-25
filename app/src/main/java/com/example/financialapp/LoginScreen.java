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

public class LoginScreen extends AppCompatActivity {

    private EditText userName, password ;
    private Button loginBtn;
    private TextView registerUser, forgotPW;

    private FirebaseAuth mAuth;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);

        userName = findViewById(R.id.ET_userName);
        password = findViewById(R.id.ET_password);
        loginBtn = findViewById(R.id.btn_Login);
        registerUser = findViewById(R.id.tv_SignIn);
        forgotPW = findViewById(R.id.tv_FogotPW);

        mAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);

        registerUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginScreen.this, SignInActivity.class);
                startActivity(intent);
                finish();
            }
        });

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String usernameString  = userName.getText().toString();
                String passwordString = password.getText().toString();

                if(TextUtils.isEmpty(usernameString)){
                    userName.setError("Empty User Name");
                }

                if(TextUtils.isEmpty(passwordString)){
                    password.setError("Empty Password");
                }

                else{
                    progressDialog.setMessage("Login In Progress");
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.show();

                    mAuth.signInWithEmailAndPassword(usernameString, passwordString).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                             Intent intent = new Intent(LoginScreen.this,MainActivity.class);
                             startActivity(intent);
                             finish();
                             progressDialog.dismiss();
                            }else{
                                Toast.makeText(LoginScreen.this, task.getException().toString(), Toast.LENGTH_LONG).show();
                                progressDialog.dismiss();
                            }
                        }
                    });
                }
            }
        });

    }
}