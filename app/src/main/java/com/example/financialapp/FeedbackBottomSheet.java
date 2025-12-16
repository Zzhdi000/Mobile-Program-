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

public class FeedbackBottomSheet extends BottomSheetDialogFragment {

    private EditText etFeedback;
    private Button btnSend;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.feedback_bottomsheet, container, false);

        etFeedback = v.findViewById(R.id.etFeedback);
        btnSend = v.findViewById(R.id.btnSendFeedback);

        btnSend.setOnClickListener(view -> sendFeedback());

        return v;
    }

    private void sendFeedback() {
        String message = etFeedback.getText().toString().trim();

        if (message.isEmpty()) {
            Toast.makeText(getContext(), "Feedback cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = FirebaseAuth.getInstance().getUid();
        if (userId == null) {
            Toast.makeText(getContext(), "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        // Firebase path
        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference("Feedback")
                .push();

        String id = ref.getKey();
        String date = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
                .format(new Date());

        Feedback fb = new Feedback(id, userId, message, date);

        ref.setValue(fb).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(getContext(), "Feedback sent", Toast.LENGTH_SHORT).show();
                dismiss();  // close bottom sheet
            } else {
                Toast.makeText(getContext(), "Failed to send feedback", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
