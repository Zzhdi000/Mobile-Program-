package com.example.financialapp;

import androidx.appcompat.app.AppCompatActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.widget.*;

import com.google.firebase.auth.FirebaseAuth;

public class LoginScreen extends AppCompatActivity {

    private EditText userName, password;
    private Button loginBtn;
    private TextView registerUser, forgotPW;
    private FirebaseAuth mAuth;

    private boolean isPasswordVisible = false;

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

        setupPasswordToggle();   // 🔥 toggle gembok

        loginBtn.setOnClickListener(v -> loginUser());

        registerUser.setOnClickListener(v ->
                startActivity(new Intent(this, SignInActivity.class))
        );

        forgotPW.setOnClickListener(v ->
                startActivity(new Intent(this, ForgotPasswordActivity.class))
        );
    }

    private void setupPasswordToggle() {
        password.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {

                int drawableLeft = 0; // index drawableStart

                // cek jika disentuh di area gembok kiri
                if (event.getRawX() <= (password.getLeft()
                        + password.getCompoundDrawables()[drawableLeft].getBounds().width() + 60)) {

                    if (isPasswordVisible) {
                        // 🔐 HIDE PASSWORD
                        password.setInputType(InputType.TYPE_CLASS_TEXT |
                                InputType.TYPE_TEXT_VARIATION_PASSWORD);

                        password.setCompoundDrawablesWithIntrinsicBounds(
                                R.drawable.ic_lock, // gembok tertutup
                                0, 0, 0
                        );

                        isPasswordVisible = false;

                    } else {
                        // 🔓 SHOW PASSWORD
                        password.setInputType(InputType.TYPE_CLASS_TEXT |
                                InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);

                        password.setCompoundDrawablesWithIntrinsicBounds(
                                R.drawable.ic_lock_open, // gembok terbuka
                                0, 0, 0
                        );

                        isPasswordVisible = true;
                    }

                    password.setSelection(password.getText().length());
                    return true;
                }
            }
            return false;
        });
    }

    private void loginUser() {
        String email = userName.getText().toString();
        String pass = password.getText().toString();

        if (TextUtils.isEmpty(email)) {
            userName.setError("Email required");
            return;
        }
        if (TextUtils.isEmpty(pass)) {
            password.setError("Password required");
            return;
        }

        ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Logging in...");
        dialog.show();

        mAuth.signInWithEmailAndPassword(email, pass)
                .addOnCompleteListener(task -> {
                    dialog.dismiss();

                    if (!task.isSuccessful()) {
                        Toast.makeText(this,
                                task.getException().getMessage(),
                                Toast.LENGTH_SHORT).show();
                        return;
                    }

                    startActivity(new Intent(this, MainActivity.class));
                    finish();
                });
    }
}
