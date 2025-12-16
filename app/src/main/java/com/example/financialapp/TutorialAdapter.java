package com.example.financialapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class TutorialAdapter extends RecyclerView.Adapter<TutorialAdapter.ViewHolder> {

    private int[] layouts;
    private Context context;

    public TutorialAdapter(Context context, int[] layouts) {
        this.context = context;
        this.layouts = layouts;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(viewType, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {}

    @Override
    public int getItemCount() {
        return layouts.length;
    }

    @Override
    public int getItemViewType(int position) {
        return layouts[position];
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
