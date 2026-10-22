package com.example.a402312338_ngonini_mobileappdev700_assignment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Custom RecyclerView Adapter for Pantry Items.
 * All internal variable names strictly use snake_case.
 */
public class PantryAdapter extends RecyclerView.Adapter<PantryAdapter.PantryViewHolder> {

    private final Context adapter_context;
    private List<PantryItem> pantry_item_list;
    private final OnPantryItemActionListener action_listener;

    public interface OnPantryItemActionListener {
        void onEditItem(PantryItem item_obj);
        void onDeleteItem(PantryItem item_obj);
    }

    public PantryAdapter(Context adapter_context, List<PantryItem> pantry_item_list, OnPantryItemActionListener action_listener) {
        this.adapter_context = adapter_context;
        this.pantry_item_list = pantry_item_list;
        this.action_listener = action_listener;
    }

    public void updateList(List<PantryItem> new_list) {
        this.pantry_item_list = new_list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PantryViewHolder onCreateViewHolder(@NonNull ViewGroup parent_view, int view_type) {
        View item_view = LayoutInflater.from(adapter_context).inflate(R.layout.item_pantry, parent_view, false);
        return new PantryViewHolder(item_view);
    }

    @Override
    public void onBindViewHolder(@NonNull PantryViewHolder holder_obj, int position_idx) {
        PantryItem item_obj = pantry_item_list.get(position_idx);

        holder_obj.tv_ingredient_name.setText(item_obj.getIngredientName());

        // Format quantity (remove trailing decimal if whole number)
        double qty_val = item_obj.getItemQuantity();
        String qty_str = (qty_val == (long) qty_val) ? String.format(Locale.ROOT, "%d", (long) qty_val) : String.format(Locale.ROOT, "%.2f", qty_val);
        holder_obj.tv_ingredient_quantity.setText(qty_str + " " + item_obj.getItemUnit());

        // Expiry date handling
        String expiry_val = item_obj.getExpiryDate();
        if (expiry_val != null && !expiry_val.trim().isEmpty()) {
            holder_obj.tv_expiry_date.setVisibility(View.VISIBLE);
            holder_obj.tv_expiry_date.setText("Expires: " + expiry_val);

            // Expiry status check
            try {
                SimpleDateFormat sdf_obj = new SimpleDateFormat("yyyy-MM-dd", Locale.ROOT);
                Date exp_date = sdf_obj.parse(expiry_val);
                Date today_date = new Date();

                long diff_time = exp_date.getTime() - today_date.getTime();
                long diff_days = diff_time / (1000 * 60 * 60 * 24);

                if (diff_days < 0) {
                    holder_obj.tv_expiry_date.setBackground(ContextCompat.getDrawable(adapter_context, R.drawable.bg_status_expired));
                    holder_obj.tv_expiry_date.setText("Expired (" + expiry_val + ")");
                } else if (diff_days <= 3) {
                    holder_obj.tv_expiry_date.setBackground(ContextCompat.getDrawable(adapter_context, R.drawable.bg_status_expiring));
                    holder_obj.tv_expiry_date.setText("Expiring Soon (" + expiry_val + ")");
                } else {
                    holder_obj.tv_expiry_date.setBackground(ContextCompat.getDrawable(adapter_context, R.drawable.bg_status_fresh));
                    holder_obj.tv_expiry_date.setText("Fresh (" + expiry_val + ")");
                }
            } catch (Exception e_err) {
                holder_obj.tv_expiry_date.setBackground(ContextCompat.getDrawable(adapter_context, R.drawable.bg_status_fresh));
            }
        } else {
            holder_obj.tv_expiry_date.setVisibility(View.GONE);
        }

        holder_obj.btn_edit.setOnClickListener(v_click -> {
            if (action_listener != null) {
                action_listener.onEditItem(item_obj);
            }
        });

        holder_obj.btn_delete.setOnClickListener(v_click -> {
            if (action_listener != null) {
                action_listener.onDeleteItem(item_obj);
            }
        });
    }

    @Override
    public int getItemCount() {
        return pantry_item_list != null ? pantry_item_list.size() : 0;
    }

    public static class PantryViewHolder extends RecyclerView.ViewHolder {
        TextView tv_ingredient_name;
        TextView tv_ingredient_quantity;
        TextView tv_expiry_date;
        ImageButton btn_edit;
        ImageButton btn_delete;

        public PantryViewHolder(@NonNull View item_view) {
            super(item_view);
            tv_ingredient_name = item_view.findViewById(R.id.tv_ingredient_name);
            tv_ingredient_quantity = item_view.findViewById(R.id.tv_ingredient_quantity);
            tv_expiry_date = item_view.findViewById(R.id.tv_expiry_date);
            btn_edit = item_view.findViewById(R.id.btn_edit);
            btn_delete = item_view.findViewById(R.id.btn_delete);
        }
    }
}