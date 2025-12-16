package com.example.financialapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TransferBottomSheet extends BottomSheetDialogFragment {

    private EditText etAmount, etNote;
    private Button btnSubmit;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_transfer, container, false);

        etAmount = v.findViewById(R.id.etTransferAmount);
        etNote   = v.findViewById(R.id.etTransferNote);
        btnSubmit = v.findViewById(R.id.btnSubmitTransfer);

        btnSubmit.setOnClickListener(view -> saveTransfer());

        return v;
    }

    private void saveTransfer() {
        String amountStr = etAmount.getText().toString().trim();
        String note = etNote.getText().toString().trim();

        if (amountStr.isEmpty()) {
            Toast.makeText(getContext(), "Amount is required", Toast.LENGTH_SHORT).show();
            return;
        }

        long amount = Long.parseLong(amountStr);
        String uid = FirebaseAuth.getInstance().getUid();

        if (uid == null) {
            Toast.makeText(getContext(), "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference("Cashdata")
                .child(uid)
                .push();

        String id = ref.getKey();
        String date = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
                .format(new Date());

        // ====================================================
        // TRANSFER = EXPENSE (tidak menambah income)
        // ====================================================
        Datacash data = new Datacash(
                id,
                "expense",                         // tetap EXPENSE
                (int) amount,                      // nominal
                note.isEmpty() ? "Transfer" : note,
                date
        );

        ref.setValue(data).addOnCompleteListener(t -> {
            if (t.isSuccessful()) {
                Toast.makeText(getContext(), "Transfer recorded", Toast.LENGTH_SHORT).show();
                dismiss();
            } else {
                Toast.makeText(getContext(), "Failed to save", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
