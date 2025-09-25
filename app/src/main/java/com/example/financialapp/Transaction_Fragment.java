package com.example.financialapp;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class Transaction_Fragment extends Fragment {
    EditText datePickerStart, datePickerEnd;

    DatePickerDialog.OnDateSetListener onDateSetListener;

    private RecyclerView recyclerView;

    private FirebaseAuth mAuth;
    private DatabaseReference dbRef;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_transaction_, container, false);

        mAuth = FirebaseAuth.getInstance();
        dbRef = FirebaseDatabase.getInstance().getReference().child("Cashdata").child(mAuth.getCurrentUser().getUid());

        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        datePickerStart = view.findViewById(R.id.startDate);
        datePickerEnd = view.findViewById(R.id.endDate);
        recyclerView = view.findViewById(R.id.transactionRecycle);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        datePickerStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        getActivity(), android.R.style.Theme_Holo_Dialog_MinWidth,
                        onDateSetListener, year, month, day);
                datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                datePickerDialog.show();
            }
        });
        datePickerEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        getActivity(), android.R.style.Theme_Holo_Dialog_MinWidth,
                        onDateSetListener, year, month, day);
                datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                datePickerDialog.show();
            }
        });
        onDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month = month + 1;
                String date = dayOfMonth + "/" + month + "/" + year;
                datePickerStart.setText(date);
                datePickerEnd.setText(date);
            }
        };
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();


        FirebaseRecyclerOptions<Datacash> recyclerOptions = new FirebaseRecyclerOptions.Builder<Datacash>()
                .setQuery(dbRef, Datacash.class)
                .build();

        FirebaseRecyclerAdapter<Datacash, transactionHolder> adapter = new FirebaseRecyclerAdapter<Datacash, transactionHolder>(recyclerOptions) {
            @Override
            protected void onBindViewHolder(@NonNull transactionHolder holder, int i, @NonNull Datacash datacash) {
                holder.setAmountText("LKR. " + String.valueOf(datacash.getAmount()));
                holder.setCategoryText(datacash.getCategoryData());
                holder.setDate(datacash.getDate());
                holder.setType(String.valueOf(datacash.getType()));
            }

            @NonNull
            @Override
            public transactionHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout, parent, false);
                return new transactionHolder(view);
            }
        };

        recyclerView.setAdapter(adapter);
        adapter.startListening();

        synchronized (adapter) {
            adapter.notify();
        }


    }

    public class transactionHolder extends RecyclerView.ViewHolder {

        public TextView amountText, categoryText;
        View view;

        public transactionHolder(@NonNull View itemView) {

            super(itemView);
            view = itemView;

        }

        private TextView amountTextView;
        private TextView categoryTextView;

        public void setAmountText(String amountText) {
            amountTextView = view.findViewById(R.id.categoryAmount);
            amountTextView.setText(amountText);
        }

        public void setCategoryText(String categoryText) {
            categoryTextView = view.findViewById(R.id.categoryName);
            categoryTextView.setText(categoryText);
        }

        public void setDate(String date) {
            TextView dateTextView = view.findViewById(R.id.dateTF);
            dateTextView.setText(date);
        }

        public void setType(String type) {

            CardView cardView = view.findViewById(R.id.cardItem);

            if (type.equals("income")) {
                cardView.setCardBackgroundColor(Color.parseColor("#DCEDC8"));
            }

            if (type.equals("expense")) {
                cardView.setCardBackgroundColor(Color.parseColor("#FFCDD2"));
            }

        }
    }
}