package com.example.smartpantry.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartpantry.R;
import com.example.smartpantry.model.PantryItem;

import java.util.List;

/**
 * RecyclerView Adapter for displaying pantry items.
 * Binds PantryItem data to card layouts in PantryListActivity.
 * Supports tap to edit, long-press to edit, and delete button to remove.
 */
public class PantryAdapter extends RecyclerView.Adapter<PantryAdapter.PantryViewHolder> {

    private List<PantryItem> items;
    private OnPantryItemClickListener listener;

    public interface OnPantryItemClickListener {
        void onItemClick(PantryItem item);
        void onItemLongClick(PantryItem item);
        void onDeleteClick(PantryItem item);
    }

    public PantryAdapter(List<PantryItem> items, OnPantryItemClickListener listener) {
        this.items = items;
        this.listener = listener;
    }

    public void updateList(List<PantryItem> newItems) {
        this.items = newItems;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PantryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_pantry, parent, false);
        return new PantryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PantryViewHolder holder, int position) {
        PantryItem item = items.get(position);
        holder.tvName.setText(capitalize(item.getName()));
        holder.tvQty.setText(item.getQuantity() + " " + item.getUnit());
        if (item.getExpiryDate() != null && !item.getExpiryDate().isEmpty()) {
            holder.tvExpiry.setText("Expires: " + item.getExpiryDate());
            holder.tvExpiry.setVisibility(View.VISIBLE);
        } else {
            holder.tvExpiry.setVisibility(View.GONE);
        }

        // Tap card to edit
        holder.itemView.setOnClickListener(v -> listener.onItemClick(item));

        // Long-press card to delete (with confirmation handled in Activity)
        holder.itemView.setOnLongClickListener(v -> {
            listener.onItemLongClick(item);
            return true;
        });

        // Visible Delete button
        holder.btnDelete.setOnClickListener(v -> listener.onDeleteClick(item));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    private String capitalize(String text) {
        if (text == null || text.isEmpty()) return text;
        return text.substring(0, 1).toUpperCase() + text.substring(1);
    }

    static class PantryViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvQty, tvExpiry;
        Button btnDelete;

        PantryViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvPantryName);
            tvQty = itemView.findViewById(R.id.tvPantryQty);
            tvExpiry = itemView.findViewById(R.id.tvPantryExpiry);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}
