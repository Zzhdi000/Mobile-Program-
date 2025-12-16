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

import java.util.List;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.ViewHolder> {

    private final List<Datacash> list;
    private final String currency;
    private final Context context;

    public TransactionAdapter(Context context, List<Datacash> list, String currency) {
        this.context = context;
        this.list = list;
        this.currency = currency;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_transaction_card, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Datacash d = list.get(position);

        String symbol = NumberFormatHelper.getCurrencySymbol(currency);
        String formattedAmount = NumberFormatHelper.formatCurrency(currency, d.getAmount());
        String prefix = d.getType().equals("income") ? "+ " : "- ";

        holder.tvAmount.setText(prefix + symbol + " " + formattedAmount);

        if (d.getType().equals("income")) {
            holder.tvAmount.setTextColor(Color.parseColor("#2ecc71"));
            holder.imgType.setImageResource(R.drawable.ic_income);
        } else {
            holder.tvAmount.setTextColor(Color.parseColor("#e74c3c"));
            holder.imgType.setImageResource(R.drawable.ic_expense);
        }

        holder.tvCategory.setText(d.getCategory());
        holder.tvDate.setText(d.getDate());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imgType;
        TextView tvAmount, tvCategory, tvDate;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imgType = itemView.findViewById(R.id.imgType);
            tvAmount = itemView.findViewById(R.id.tvAmount);
            tvCategory = itemView.findViewById(R.id.tvCategory);
            tvDate = itemView.findViewById(R.id.tvDate);
        }
    }
}
