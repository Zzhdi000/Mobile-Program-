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

public class HistoryUnifiedAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;

    private final Context context;
    private final List<Object> list;
    private final String currency;

    public HistoryUnifiedAdapter(Context context, List<Object> list, String currency) {
        this.context = context;
        this.list = list;
        this.currency = currency;
    }

    @Override
    public int getItemViewType(int position) {
        if (list.get(position) instanceof HistoryHeader)
            return TYPE_HEADER;
        else
            return TYPE_ITEM;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int type) {

        if (type == TYPE_HEADER) {
            View v = LayoutInflater.from(context)
                    .inflate(R.layout.item_history_header, parent, false);
            return new HeaderHolder(v);
        } else {
            View v = LayoutInflater.from(context)
                    .inflate(R.layout.item_transaction_card, parent, false);
            return new ItemHolder(v);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder h, int pos) {

        if (h instanceof HeaderHolder) {

            HistoryHeader header = (HistoryHeader) list.get(pos);
            ((HeaderHolder) h).tvHeader.setText(header.getTitle());

        } else if (h instanceof ItemHolder) {

            Datacash d = (Datacash) list.get(pos);
            ItemHolder holder = (ItemHolder) h;

            // =============== AMOUNT & COLOR ===============
            String prefix = d.getType().equals("income") ? "+ " : "- ";

            holder.tvAmount.setText(prefix + currency + " " + d.getAmount());

            if (d.getType().equals("income")) {
                holder.tvAmount.setTextColor(Color.parseColor("#2ecc71")); // Hijau
                holder.imgType.setImageResource(R.drawable.ic_income);
            } else {
                holder.tvAmount.setTextColor(Color.parseColor("#e74c3c")); // Merah
                holder.imgType.setImageResource(R.drawable.ic_expense);
            }

            // =============== CATEGORY ===============
            holder.tvCategory.setText(d.getCategory());

            // =============== DATE ===============
            holder.tvDate.setText(d.getDate());
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    // ===================== HOLDER UNTUK HEADER =====================
    public static class HeaderHolder extends RecyclerView.ViewHolder {
        TextView tvHeader;

        public HeaderHolder(@NonNull View v) {
            super(v);
            tvHeader = v.findViewById(R.id.tvHeaderTitle);
        }
    }

    // ===================== HOLDER UNTUK ITEM TRANSAKSI =====================
    public static class ItemHolder extends RecyclerView.ViewHolder {

        ImageView imgType;
        TextView tvAmount, tvCategory, tvDate;

        public ItemHolder(@NonNull View itemView) {
            super(itemView);

            imgType = itemView.findViewById(R.id.imgType);
            tvAmount = itemView.findViewById(R.id.tvAmount);
            tvCategory = itemView.findViewById(R.id.tvCategory);
            tvDate = itemView.findViewById(R.id.tvDate);
        }
    }
}
