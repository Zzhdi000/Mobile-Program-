package com.example.financialapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class SavingsAdapter extends RecyclerView.Adapter<SavingsAdapter.VH> {

    private final List<Savings> list;
    private final Context ctx;
    private final String currency;

    public SavingsAdapter(Context ctx, List<Savings> list, String currency) {
        this.ctx = ctx;
        this.list = list;
        this.currency = currency;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(ctx).inflate(R.layout.item_saving, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        Savings s = list.get(position);
        holder.tvAmount.setText("+ " + currency + " " + s.getAmount());
        holder.tvDate.setText(s.getDate());
        holder.tvNote.setText(s.getNote() == null ? "" : s.getNote());
    }

    @Override
    public int getItemCount() { return list.size(); }

    static class VH extends RecyclerView.ViewHolder {
        TextView tvAmount, tvDate, tvNote;
        VH(@NonNull View itemView) {
            super(itemView);
            tvAmount = itemView.findViewById(R.id.tvSaveAmount);
            tvDate = itemView.findViewById(R.id.tvSaveDate);
            tvNote = itemView.findViewById(R.id.tvSaveNote);
        }
    }
}
