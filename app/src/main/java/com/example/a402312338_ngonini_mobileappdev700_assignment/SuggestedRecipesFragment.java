package com.example.a402312338_ngonini_mobileappdev700_assignment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Fragment executing the Core Strict-Matching Algorithm.
 * Filters recipes against user's pantry items in SQLite.
 * Variable names strictly follow snake_case convention.
 */
public class SuggestedRecipesFragment extends Fragment implements RecipeAdapter.OnRecipeClickListener {

    private DatabaseHelper db_helper;
    private TabLayout tab_layout_recipes;
    private RecyclerView rv_suggested_recipes;
    private LinearLayout layout_empty_recipes;
    private RecipeAdapter recipe_adapter;
    private List<Recipe> suggested_recipe_list;
    private boolean is_strict_mode = true;

    public SuggestedRecipesFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View fragment_view = inflater.inflate(R.layout.fragment_suggested_recipes, container, false);

        db_helper = new DatabaseHelper(requireContext());

        tab_layout_recipes = fragment_view.findViewById(R.id.tab_layout_recipes);
        rv_suggested_recipes = fragment_view.findViewById(R.id.rv_suggested_recipes);
        layout_empty_recipes = fragment_view.findViewById(R.id.layout_empty_recipes);

        rv_suggested_recipes.setLayoutManager(new LinearLayoutManager(requireContext()));
        suggested_recipe_list = new ArrayList<>();
        recipe_adapter = new RecipeAdapter(requireContext(), suggested_recipe_list, this);
        rv_suggested_recipes.setAdapter(recipe_adapter);

        tab_layout_recipes.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab_obj) {
                is_strict_mode = (tab_obj.getPosition() == 0);
                loadRecipeSuggestions();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab_obj) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab_obj) {}
        });

        return fragment_view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadRecipeSuggestions();
    }

    private void loadRecipeSuggestions() {
        try {
            suggested_recipe_list = db_helper.getSuggestedRecipes(is_strict_mode);
            if (suggested_recipe_list == null) {
                suggested_recipe_list = new ArrayList<>();
            }
            recipe_adapter.updateList(suggested_recipe_list);

            if (suggested_recipe_list.isEmpty()) {
                layout_empty_recipes.setVisibility(View.VISIBLE);
                rv_suggested_recipes.setVisibility(View.GONE);
            } else {
                layout_empty_recipes.setVisibility(View.GONE);
                rv_suggested_recipes.setVisibility(View.VISIBLE);
            }
        } catch (Exception e_err) {
            e_err.printStackTrace();
        }
    }

    @Override
    public void onRecipeClick(Recipe recipe_obj) {
        if (recipe_obj == null || !isAdded() || getContext() == null) return;
        Intent detail_intent = new Intent(requireContext(), RecipeDetailActivity.class);
        detail_intent.putExtra("EXTRA_RECIPE_ID", recipe_obj.getRecipeId());
        startActivity(detail_intent);
    }
}