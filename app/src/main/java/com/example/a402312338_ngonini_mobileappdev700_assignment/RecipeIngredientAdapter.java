package com.example.a402312338_ngonini_mobileappdev700_assignment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Locale;

/**
 * RecyclerView Adapter for displaying ingredients inside RecipeDetailActivity.
 * All internal variable names strictly use snake_case.
 */
public class RecipeIngredientAdapter extends RecyclerView.Adapter<RecipeIngredientAdapter.IngredientViewHolder> {

    private final Context adapter_context;
    private final List<RecipeIngredient> ingredient_list;

    public RecipeIngredientAdapter(Context adapter_context, List<RecipeIngredient> ingredient_list) {
        this.adapter_context = adapter_context;
        this.ingredient_list = ingredient_list;
    }

    @NonNull
    @Override
    public IngredientViewHolder onCreateViewHolder(@NonNull ViewGroup parent_view, int view_type) {
        View item_view = LayoutInflater.from(adapter_context).inflate(R.layout.item_recipe_ingredient, parent_view, false);
        return new IngredientViewHolder(item_view);
    }

    @Override
    public void onBindViewHolder(@NonNull IngredientViewHolder holder_obj, int position_idx) {
        RecipeIngredient req_ing = ingredient_list.get(position_idx);

        holder_obj.tv_ingredient_detail_name.setText(req_ing.getIngredientName());

        double req_qty = req_ing.getRequiredQuantity();
        String qty_str = (req_qty == (long) req_qty) ? String.format(Locale.ROOT, "%d", (long) req_qty) : String.format(Locale.ROOT, "%.2f", req_qty);
        holder_obj.tv_ingredient_detail_qty.setText(qty_str + " " + req_ing.getIngredientUnit());

        if (req_ing.isInPantry()) {
            holder_obj.iv_status_icon.setImageResource(R.drawable.ic_check);
            holder_obj.iv_status_icon.setColorFilter(ContextCompat.getColor(adapter_context, R.color.status_fresh));
            holder_obj.tv_ingredient_detail_name.setTextColor(ContextCompat.getColor(adapter_context, R.color.text_primary));
        } else {
            holder_obj.iv_status_icon.setImageResource(R.drawable.ic_warning);
            holder_obj.iv_status_icon.setColorFilter(ContextCompat.getColor(adapter_context, R.color.status_missing));
            holder_obj.tv_ingredient_detail_name.setTextColor(ContextCompat.getColor(adapter_context, R.color.status_missing));
        }
    }

    @Override
    public int getItemCount() {
        return ingredient_list != null ? ingredient_list.size() : 0;
    }

    public static class IngredientViewHolder extends RecyclerView.ViewHolder {
        ImageView iv_status_icon;
        TextView tv_ingredient_detail_name;
        TextView tv_ingredient_detail_qty;

        public IngredientViewHolder(@NonNull View item_view) {
            super(item_view);
            iv_status_icon = item_view.findViewById(R.id.iv_status_icon);
            tv_ingredient_detail_name = item_view.findViewById(R.id.tv_ingredient_detail_name);
            tv_ingredient_detail_qty = item_view.findViewById(R.id.tv_ingredient_detail_qty);
        }
    }
}