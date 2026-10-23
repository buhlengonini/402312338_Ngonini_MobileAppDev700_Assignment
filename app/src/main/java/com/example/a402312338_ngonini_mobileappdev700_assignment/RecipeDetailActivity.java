package com.example.a402312338_ngonini_mobileappdev700_assignment;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;

import java.util.List;
import java.util.Locale;

/**
 * Activity displaying detailed recipe information, required ingredients status, and preparation steps.
 * Allows user to deduct recipe ingredients from pantry upon cooking.
 * Variable names strictly follow snake_case convention.
 */
public class RecipeDetailActivity extends AppCompatActivity {

    private DatabaseHelper db_helper;
    private Recipe current_recipe;
    private int recipe_id = -1;

    private TextView tv_detail_title;
    private TextView tv_detail_category;
    private TextView tv_detail_prep_time;
    private TextView tv_instructions;
    private RecyclerView rv_recipe_ingredients;
    private MaterialButton btn_cook_recipe;

    private RecipeIngredientAdapter ingredient_adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        db_helper = new DatabaseHelper(this);

        tv_detail_title = findViewById(R.id.tv_detail_title);
        tv_detail_category = findViewById(R.id.tv_detail_category);
        tv_detail_prep_time = findViewById(R.id.tv_detail_prep_time);
        tv_instructions = findViewById(R.id.tv_instructions);
        rv_recipe_ingredients = findViewById(R.id.rv_recipe_ingredients);
        btn_cook_recipe = findViewById(R.id.btn_cook_recipe);

        rv_recipe_ingredients.setLayoutManager(new LinearLayoutManager(this));

        if (getIntent().hasExtra("EXTRA_RECIPE_ID")) {
            recipe_id = getIntent().getIntExtra("EXTRA_RECIPE_ID", -1);
        }

        if (recipe_id != -1) {
            loadRecipeData();
        } else {
            Toast.makeText(this, "Error loading recipe details", Toast.LENGTH_SHORT).show();
            finish();
        }

        btn_cook_recipe.setOnClickListener(v_click -> showCookConfirmationDialog());
    }

    private void loadRecipeData() {
        // Evaluate recipe against current pantry state
        List<Recipe> evaluated_recipes = db_helper.getSuggestedRecipes(true);
        for (Recipe r_obj : evaluated_recipes) {
            if (r_obj.getRecipeId() == recipe_id) {
                current_recipe = r_obj;
                break;
            }
        }

        if (current_recipe == null) {
            // If not 100% match, fetch directly
            current_recipe = db_helper.getRecipeById(recipe_id);
            // Re-eval ingredients against pantry
            List<PantryItem> pantry_items = db_helper.getAllPantryItems(null);
            for (RecipeIngredient req_ing : current_recipe.getIngredientList()) {
                boolean found_in_pantry = false;
                for (PantryItem p_item : pantry_items) {
                    if (StringNormalizer.areIngredientsMatching(p_item.getIngredientName(), req_ing.getIngredientName())) {
                        if (StringNormalizer.isQuantitySufficient(
                                p_item.getItemQuantity(), p_item.getItemUnit(),
                                req_ing.getRequiredQuantity(), req_ing.getIngredientUnit())) {
                            found_in_pantry = true;
                            break;
                        }
                    }
                }
                req_ing.setIsInPantry(found_in_pantry);
            }
        }

        if (current_recipe != null) {
            tv_detail_title.setText(current_recipe.getRecipeName());
            tv_detail_category.setText(current_recipe.getRecipeCategory());
            tv_detail_prep_time.setText(String.format(Locale.ROOT, "%d mins prep", current_recipe.getPrepTimeMinutes()));
            tv_instructions.setText(current_recipe.getRecipeInstructions());

            ingredient_adapter = new RecipeIngredientAdapter(this, current_recipe.getIngredientList());
            rv_recipe_ingredients.setAdapter(ingredient_adapter);
        }
    }

    private void showCookConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.dialog_cook_title)
                .setMessage(R.string.dialog_cook_msg)
                .setPositiveButton("Deduct Ingredients", (dialog_interface, which_idx) -> {
                    boolean success_val = db_helper.deductRecipeIngredientsFromPantry(recipe_id);
                    if (success_val) {
                        Toast.makeText(this, R.string.toast_deducted_success, Toast.LENGTH_SHORT).show();
                        loadRecipeData();
                    }
                })
                .setNegativeButton(R.string.btn_cancel, null)
                .show();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item_menu) {
        if (item_menu.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item_menu);
    }
}