package com.example.financialapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

public class EditProfileActivity extends AppCompatActivity {

    EditText etName;
    Button btnSave;

    FirebaseAuth mAuth;
    DatabaseReference userRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        etName = findViewById(R.id.etName);
        btnSave = findViewById(R.id.btnSave);

        // Firebase
        mAuth = FirebaseAuth.getInstance();
        userRef = FirebaseDatabase.getInstance()
                .getReference("Users")
                .child(mAuth.getCurrentUser().getUid());

        // Load current name
        loadCurrentName();

        // Save new name
        btnSave.setOnClickListener(v -> saveName());
    }

    private void loadCurrentName() {
        userRef.child("name").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                String currentName = snapshot.getValue(String.class);
                if (currentName != null) etName.setText(currentName);
            }

            @Override
            public void onCancelled(DatabaseError error) { }
        });
    }

    private void saveName() {
        String newName = etName.getText().toString().trim();

        if (newName.isEmpty()) {
            etName.setError("Name can't be empty");
            return;
        }

        userRef.child("name").setValue(newName)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Name updated successfully!", Toast.LENGTH_SHORT).show();
                    finish(); // close activity
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
    }
}
