package com.example.financialapp;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {

    private final Context context;
    private final ArrayList<Datacash> list;
    private final String currency;

    public HistoryAdapter(Context context, ArrayList<Datacash> list, String currency) {
        this.context = context;
        this.list = list;
        this.currency = currency;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context)
                .inflate(R.layout.item_transaction_card, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Datacash d = list.get(position);

        // ==========================
        // Category & Date
        // ==========================
        holder.tvCategory.setText(d.getCategory());
        holder.tvDate.setText(d.getDate());

        // ==========================
        // Format nominal
        // ==========================
        String formatted = NumberFormatHelper.formatCurrency(currency, d.getAmount());

        if (d.getType().equals("income")) {
            holder.tvAmount.setText("+ " + currency + " " + formatted);
            holder.tvAmount.setTextColor(Color.parseColor("#2ECC71"));
            holder.imgType.setImageResource(R.drawable.ic_income);

        } else {
            holder.tvAmount.setText("- " + currency + " " + formatted);
            holder.tvAmount.setTextColor(Color.parseColor("#E74C3C"));
            holder.imgType.setImageResource(R.drawable.ic_expense);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgType;
        TextView tvCategory, tvDate, tvAmount;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imgType = itemView.findViewById(R.id.imgType);
            tvCategory = itemView.findViewById(R.id.tvCategory);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvAmount = itemView.findViewById(R.id.tvAmount);
        }
    }
}
