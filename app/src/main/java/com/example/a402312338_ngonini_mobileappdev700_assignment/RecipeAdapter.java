package com.example.a402312338_ngonini_mobileappdev700_assignment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Locale;

/**
 * Custom RecyclerView Adapter for Recipe items.
 * Variable names strictly follow snake_case convention.
 */
public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder> {

    private final Context adapter_context;
    private List<Recipe> recipe_item_list;
    private final OnRecipeClickListener click_listener;

    public interface OnRecipeClickListener {
        void onRecipeClick(Recipe recipe_obj);
    }

    public RecipeAdapter(Context adapter_context, List<Recipe> recipe_item_list, OnRecipeClickListener click_listener) {
        this.adapter_context = adapter_context;
        this.recipe_item_list = recipe_item_list;
        this.click_listener = click_listener;
    }

    public void updateList(List<Recipe> new_list) {
        this.recipe_item_list = new_list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent_view, int view_type) {
        View item_view = LayoutInflater.from(adapter_context).inflate(R.layout.item_recipe, parent_view, false);
        return new RecipeViewHolder(item_view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeViewHolder holder_obj, int position_idx) {
        Recipe recipe_obj = recipe_item_list.get(position_idx);

        holder_obj.tv_recipe_title.setText(recipe_obj.getRecipeName());
        holder_obj.tv_recipe_category.setText(recipe_obj.getRecipeCategory());
        holder_obj.tv_prep_time.setText(String.format(Locale.ROOT, "%d mins prep", recipe_obj.getPrepTimeMinutes()));

        if (recipe_obj.isStrictMatch()) {
            holder_obj.tv_match_badge.setText("100% Ready");
            holder_obj.tv_match_badge.setBackground(ContextCompat.getDrawable(adapter_context, R.drawable.bg_status_fresh));
        } else {
            holder_obj.tv_match_badge.setText("Missing 1 Ing.");
            holder_obj.tv_match_badge.setBackground(ContextCompat.getDrawable(adapter_context, R.drawable.bg_status_almost));
        }

        holder_obj.itemView.setOnClickListener(v_click -> {
            if (click_listener != null) {
                click_listener.onRecipeClick(recipe_obj);
            }
        });
    }

    @Override
    public int getItemCount() {
        return recipe_item_list != null ? recipe_item_list.size() : 0;
    }

    public static class RecipeViewHolder extends RecyclerView.ViewHolder {
        TextView tv_recipe_title;
        TextView tv_recipe_category;
        TextView tv_prep_time;
        TextView tv_match_badge;

        public RecipeViewHolder(@NonNull View item_view) {
            super(item_view);
            tv_recipe_title = item_view.findViewById(R.id.tv_recipe_title);
            tv_recipe_category = item_view.findViewById(R.id.tv_recipe_category);
            tv_prep_time = item_view.findViewById(R.id.tv_prep_time);
            tv_match_badge = item_view.findViewById(R.id.tv_match_badge);
        }
    }
}