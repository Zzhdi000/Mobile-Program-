package com.example.financialapp;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class CurrencyHelper {

    public interface CurrencyCallback {
        void onLoaded(String currency);
    }

    public static void getCurrency(FirebaseAuth mAuth, CurrencyCallback callback) {

        FirebaseDatabase.getInstance().getReference("Users")
                .child(mAuth.getCurrentUser().getUid())
                .child("currency")
                .get()
                .addOnSuccessListener(s -> {
                    if (s.exists()) callback.onLoaded(s.getValue(String.class));
                    else callback.onLoaded("LKR");
                })
                .addOnFailureListener(e -> callback.onLoaded("LKR"));
    }
}

